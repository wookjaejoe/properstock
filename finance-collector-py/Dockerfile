FROM python:3.10.0-buster

WORKDIR /app
COPY . .
RUN python3 -m pip install -r requirements.txt

CMD [ "python3", "main.py"]