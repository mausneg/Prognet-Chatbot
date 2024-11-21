import tensorflow as tf
import tensorflow_transform as tft
from tfx.components.trainer.fn_args_utils import FnArgs
import os
from transform import FEATURE_KEY, LABEL_KEY, transformed_name
from tuner import input_fn

LABEL = 'tags'
BATCH_SIZE = 16
EPOCHS = 50


def _get_serve_tf_examples_fn(model, tf_transform_output):
    model.tft_layer = tf_transform_output.transform_features_layer()

    @tf.function(input_signature=[
        tf.TensorSpec(shape=[None], dtype=tf.string, name="examples"),
    ])
    def serve_tf_examples_fn(serialized_tf_examples):
        """Return the output to be used in the serving signature."""

        feature_spec = tf_transform_output.raw_feature_spec()
        feature_spec.pop(LABEL)

        parsed_features = tf.io.parse_example(
            serialized_tf_examples, feature_spec,
        )

        transformed_features = model.tft_layer(parsed_features)
        return model(transformed_features)

    return serve_tf_examples_fn

VOCAB_SIZE = 2500
SEQUENCE_LENGTH = 30
 
vectorize_layer = tf.keras.layers.TextVectorization(
    standardize="lower_and_strip_punctuation",
    max_tokens=VOCAB_SIZE,
    output_mode='int',
    output_sequence_length=SEQUENCE_LENGTH)

def build_model(hp):
    inputs = tf.keras.layers.Input(shape=(1,), dtype=tf.string, name=transformed_name(FEATURE_KEY))
    reshaped_narrative = tf.reshape(inputs, [-1])
    x = vectorize_layer(reshaped_narrative)
    x = tf.keras.layers.Embedding(VOCAB_SIZE, hp['embedding_dim'])(x)
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(hp['lstm_units_0'], return_sequences=True))(x)
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(hp['lstm_units_1']))(x)
    for i in range(hp['n_layers']):
        x = tf.keras.layers.Dense(hp['units_' + str(i)], activation='relu')(x)
        x = tf.keras.layers.Dropout(hp['dropout_' + str(i)])(x)
    outputs = tf.keras.layers.Dense(LABEL_KEY['tags'], activation='softmax')(x)
    model = tf.keras.Model(inputs, outputs)
    model.compile(
        optimizer=tf.keras.optimizers.Adam(hp['learning_rate']),
        loss=tf.keras.losses.CategoricalCrossentropy(),
        metrics=[tf.keras.metrics.CategoricalAccuracy()]
    )
    return model

def run_fn(fn_args: FnArgs):
    hp = fn_args.hyperparameters['values']
    log_dir = os.path.join(fn_args.model_run_dir, 'logs')
    tensorboard_callback = tf.keras.callbacks.TensorBoard(
        log_dir=log_dir, 
        update_freq='batch'
    )
    early_stopping_callback = tf.keras.callbacks.EarlyStopping(
        monitor='val_loss',
        min_delta=0.01, 
        patience=40,
        restore_best_weights=True
    )
    tf_transform_output = tft.TFTransformOutput(fn_args.transform_graph_path)
    train_set = input_fn(fn_args.train_files, tf_transform_output, num_epochs=EPOCHS, batch_size=BATCH_SIZE)
    val_set =  input_fn(fn_args.eval_files, tf_transform_output, num_epochs=EPOCHS, batch_size=BATCH_SIZE)

    vectorize_layer.adapt(
        [j[0].numpy()[0] for j in [
            i[0][transformed_name(FEATURE_KEY)]
                for i in list(train_set)]])
    
    model = build_model(hp)
    model.fit(
        train_set,
        validation_data=val_set,
        callbacks=[tensorboard_callback, early_stopping_callback],
        epochs=hp['tuner/epochs'],
    )
    signatures = {
        'serving_default':
        _get_serve_tf_examples_fn(model, tf_transform_output).get_concrete_function(
                                    tf.TensorSpec(
                                    shape=[None],
                                    dtype=tf.string,
                                    name='examples'))
    }

    model.save(fn_args.serving_model_dir, save_format='tf', signatures=signatures)
