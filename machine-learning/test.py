import requests
import json

url = "http://localhost:5000/predict"
input_text = "apa itu kkn?"
payload = {"text": input_text}
headers = {
    "Content-Type": "application/json"
}

response = requests.post(url, data=json.dumps(payload), headers=headers)
print("Status Code:", response.status_code)
print("Response:", response.json()['response'])