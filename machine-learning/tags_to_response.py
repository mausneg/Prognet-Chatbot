import pandas as pd

tags = pd.read_csv('machine-learning/data/tags')
tags.columns = ['tags']
intents = pd.read_csv('machine-learning/data/intents.csv')
intents.drop('patterns', axis=1, inplace=True)

df_merged = pd.merge(tags, intents, on='tags', how='inner')
df_merged.drop_duplicates(inplace=True)
df_merged.reset_index(drop=True, inplace=True)
print(df_merged)
df_merged.to_csv('machine-learning/data/tags_to_responses.csv', index=False)
