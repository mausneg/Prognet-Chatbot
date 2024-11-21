from flask import Flask, request, jsonify
import pandas as pd
import tensorflow as tf
import random
import ast

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    input_text = request.json.get('text')
    if not input_text:
        return jsonify({'error': 'No input text provided'}), 400

    model = tf.keras.models.load_model('machine-learning/serving_model_dir/chatbot-psti-model/1732104784')
    prediction = model.predict([input_text])
    prediction = tf.argmax(prediction, axis=1).numpy()[0]
    tags_to_responses = pd.read_csv('machine-learning/data/tags_to_responses.csv')
    response = tags_to_responses[tags_to_responses['tags'].index+1 == prediction]['responses'].values[0]
    response = ast.literal_eval(response)
    response = random.choice(response)
    return jsonify({'response': response})

@app.route('/')
def home():
    return "Hello World!"

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)