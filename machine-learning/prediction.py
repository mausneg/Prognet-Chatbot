import pandas as pd
import tensorflow as tf
import requests
import json
import base64

def prepare_json(text):
    feature_spec = {
        "patterns": tf.train.Feature(bytes_list=tf.train.BytesList(value=[bytes(text, "utf-8")])),
    }
    example = tf.train.Example(
        features=tf.train.Features(feature=feature_spec)
    ).SerializeToString()
    result = [
        {
            "examples": base64.b64encode(example).decode()
        }
    ]
    return json.dumps({
        "signature_name": "serving_default",
        "instances": result
    })

def predict(input):
    json_data = prepare_json(input)
    print(json_data)
    endpoint = "http://localhost:8080/v1/models/chatbot-psti-model:predict"
    headers = {"Content-Type": "application/json"}
    response = requests.post(endpoint, data=json_data, headers=headers)
    print(response.json())
    prediction = response.json().get("predictions")
    return prediction


if __name__ == "__main__":
    prediction = predict("apa itu kkn?")
    print(prediction)