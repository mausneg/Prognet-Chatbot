from flask import Flask, request, jsonify
import pandas as pd
import tensorflow as tf
import requests
import json
import base64
import random
import ast

app = Flask(__name__)

def prepare_json(text):
    feature_spec = {
        "patterns": tf.train.Feature(bytes_list=tf.train.BytesList(value=[bytes(text, "utf-8")])),
        "responses": tf.train.Feature(bytes_list=tf.train.BytesList(value=[b""]))
    }
    example = tf.train.Example(
        features=tf.train.Features(feature=feature_spec)
    ).SerializeToString()
    
    result = [
        {
            "examples": {
                "b64": base64.b64encode(example).decode()
            }
        }
    ]
    return json.dumps({
        "signature_name": "serving_default",
        "instances": result
    })

def predict(input):
    json_data = prepare_json(input)
    endpoint = "http://localhost:8501/v1/models/chatbot-psti-model:predict"
    response = requests.post(endpoint, data=json_data)
    prediction = response.json().get("predictions")
    prediction = tf.argmax(prediction[0]).numpy()
    return prediction

@app.route('/predict', methods=['POST'])
def predict_route():
    data = request.json
    input_text = data.get('text')
    prediction = predict(input_text)
    tags_to_responses = pd.read_csv('machine-learning/data/tags_to_responses.csv')
    response = tags_to_responses[tags_to_responses['tags'].index+1 == prediction]['responses'].values[0]
    response = ast.literal_eval(response)
    response = random.choice(response)
    return jsonify({'prediction': response})

@app.route('/')
def home():
    return "Hello, World!"

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)