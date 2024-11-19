import tensorflow as tf
import tensorflow_transform as tft

FEATURE_KEY = 'patterns'
LABEL_KEY = {
    'tags': 78
}

def transformed_name(key):
    return key + '_xf'

def preprocessing_fn(inputs):
    outputs = {}
    outputs[transformed_name(FEATURE_KEY)] = tf.strings.lower(inputs[FEATURE_KEY])
    for key, value in LABEL_KEY.items():
        integerized = tft.compute_and_apply_vocabulary(inputs[key], vocab_filename=key)
        outputs[transformed_name(key)] = tf.reshape(tf.one_hot(integerized, value), [-1, value])
    return outputs