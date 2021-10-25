#!/usr/bin/env python
import pika
import sys
import time

connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost', port=5672)
)

channel = connection.channel()
channel.exchange_declare(exchange='logs', exchange_type='fanout')

count = 1
while True:
    message = f'{count}'
    channel.basic_publish(exchange='logs', routing_key='', body=message)
    print(message)
    time.sleep(1)
    count += 1

connection.close()
