FROM python:3.10.0-buster

WORKDIR /app
COPY . .
RUN python3 -m pip install -r requirements.txt

ENV RMQ_HOST home.jowookjae.in
ENV RMQ_PORT 9090

CMD [ "python3", "main.py"]