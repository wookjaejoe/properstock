FROM python:3.8-slim-buster

WORKDIR /app
COPY . .
RUN pip3 install -r requirements.txt

ENV RMQ_HOST home.jowookjae.in
ENV RMQ_PORT 9090

CMD [ "python3", "main.py"]