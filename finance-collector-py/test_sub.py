#!/usr/bin/env python
import pika
import json

host = 'home.jowookjae.in'
exchange = 'currentPrice'
port = 5672

connection = pika.BlockingConnection(
    pika.ConnectionParameters(host=host, port=port))
channel = connection.channel()
channel.exchange_declare(exchange=exchange, exchange_type='fanout')
result = channel.queue_declare(queue='', exclusive=True)
queue_name = result.method.queue

channel.queue_bind(exchange=exchange, queue=queue_name)

print(f'Waiting for message received from {exchange}@{host}. To exit press CTRL+C')


def callback(ch, method, properties, body):
    if body:
        for item in json.loads(body):
            if item['code'] == '012170':
                print(item['price'])


channel.basic_consume(
    queue=queue_name, on_message_callback=callback, auto_ack=True)

channel.start_consuming()
