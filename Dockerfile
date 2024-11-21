FROM python:3.10-slim

WORKDIR /app

COPY machine-learning/prediction.py /app/machine-learning/prediction.py
COPY machine-learning/data/tags_to_responses.csv /app/machine-learning/data/tags_to_responses.csv
COPY machine-learning/serving_model_dir/chatbot-psti-model/1732104784 /app/machine-learning/serving_model_dir/chatbot-psti-model/1732104784
RUN pip install flask pandas tensorflow==2.13.1 requests

EXPOSE 5000

CMD ["python", "machine-learning/prediction.py"]