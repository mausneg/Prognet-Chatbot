import json
import pandas as pd  
import os

with open('machine-learning/data/intents.json') as f:
    data = json.load(f)

patterns = []
tags = []
responses = []

for intent in data['intents']:
    for pattern in intent['patterns']:
        patterns.append(pattern)
        tags.append(intent['tag'])
        responses.append(intent['responses'])

unique_tags = list(set(tags))
print("Total unique tags: ", len(unique_tags))
df = pd.DataFrame({'patterns': patterns, 'tags': tags, 'responses': responses})
df.to_csv('machine-learning/data/final/intents.csv', index=False)