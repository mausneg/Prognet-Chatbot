from typing import Any, Dict, NamedTuple, Text
import keras_tuner as kt
import tensorflow as tf
from keras_tuner.engine import base_tuner
import tensorflow_transform as tft
from tfx.components.trainer.fn_args_utils import FnArgs
import os
from transform import FEATURE_KEY, LABEL_KEY, transformed_name

EPOCHS = 50
BATCH_SIZE = 16



TunerFnResult = NamedTuple("TunerFnResult", [
    ("tuner", base_tuner.BaseTuner),
    ("fit_kwargs", Dict[Text, Any]),
])

def gzip_reader_fn(filenames):
    return tf.data.TFRecordDataset(filenames=filenames, compression_type='GZIP')

def input_fn(file_pattern, 
             tf_transform_output,
             num_epochs,
             batch_size=64)->tf.data.Dataset:
    
    transform_feature_spec = (
        tf_transform_output.transformed_feature_spec().copy())
    
    dataset = tf.data.experimental.make_batched_features_dataset(
        file_pattern=file_pattern,
        batch_size=batch_size,
        features=transform_feature_spec,
        reader=gzip_reader_fn,
        num_epochs=num_epochs,
        label_key = transformed_name('tags')
    )
    return dataset

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
    x = tf.keras.layers.Embedding(VOCAB_SIZE, hp.Int('embedding_dim', min_value=64, max_value=512, step=64))(x)
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(hp.Int('lstm_units_0', min_value=64, max_value=512, step=64), return_sequences=True))(x)
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(hp.Int('lstm_units_1', min_value=64, max_value=512, step=64)))(x)
    for i in range(hp.Int('n_layers', 1, 3)):
        x = tf.keras.layers.Dense(hp.Int('units_' + str(i), min_value=64, max_value=512, step=64), activation='relu')(x)
        x = tf.keras.layers.Dropout(hp.Float('dropout_' + str(i), 0.1, 0.5, step=0.1))(x)

    outputs = tf.keras.layers.Dense(LABEL_KEY['tags'], activation='softmax')(x)
    model = tf.keras.Model(inputs, outputs)
    model.compile(
        optimizer=tf.keras.optimizers.Adam(hp.Choice('learning_rate', [1e-2, 1e-3, 1e-4])),
        loss=tf.keras.losses.CategoricalCrossentropy(),
        metrics=[tf.keras.metrics.CategoricalAccuracy()]
    )
    return model


def tuner_fn(fn_args: FnArgs):
    tf_transform_output = tft.TFTransformOutput(fn_args.transform_graph_path)
    train_dataset = input_fn(fn_args.train_files, tf_transform_output, EPOCHS, BATCH_SIZE)
    eval_dataset = input_fn(fn_args.eval_files, tf_transform_output, EPOCHS, BATCH_SIZE)

    vectorize_layer.adapt(
        [j[0].numpy()[0] for j in [
            i[0][transformed_name(FEATURE_KEY)]
                for i in list(train_dataset)]])
    
    early_stopping_callback = tf.keras.callbacks.EarlyStopping(
        monitor='val_loss',
        min_delta=0.01, 
        patience=40,
        restore_best_weights=True
    )

    tuner = kt.Hyperband(
        hypermodel=build_model,
        objective=kt.Objective('val_categorical_accuracy', direction='max'),
        max_epochs=EPOCHS,
        factor=2,
        directory=fn_args.working_dir,
        project_name='chatbot',
    )

    tuner.oracle.max_trials = 20

    return TunerFnResult(
        tuner=tuner,
        fit_kwargs={
            'x': train_dataset,
            'validation_data': eval_dataset,
            'callbacks': [
                early_stopping_callback, 
            ]
        }
    )
