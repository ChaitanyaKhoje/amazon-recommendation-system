import json
import sys
import os
import socket
import pickle
from kafka import KafkaConsumer

from textblob import TextBlob

all_objects = []
fragments = []


def analyze():
    for obj in all_objects:
        # Add sentiment column to the record
        obj['sentiment'] = 0
        sentence = obj['reviewText']
        if sentence:
            analysis = TextBlob(sentence)
            if analysis.sentiment[0] > 0 and obj['overall'] >= 3:
                # print("Positive")
                obj['sentiment'] = 1
            else:
                # print("Negative")
                obj['sentiment'] = -1
            # print(obj)


def perform_operations():
    # Read connection details from the connections.txt
    dir_path = os.path.dirname(os.path.realpath(__file__))  # Get current directory
    f = open(dir_path + '/connections.txt', 'r')
    c = f.readlines()
    temp = c[0].split(",")
    ip = temp[0]
    port = int(temp[1])
    f.close()

    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = (ip, port)
    print('Starting up on {} port {}'.format(*server_address))

    try:
        # Bind the socket to the port
        sock.bind(server_address)
    except socket.error as err:
        print('Bind failed. Error Code : ' .format(err))
        sys.exit(-1)

    # Listen for incoming connections
    sock.listen(1)

    while True:
        # Wait for a connection
        print('waiting for a connection')
        conn, client_address = sock.accept()
        try:
            print('connection from', client_address)
            conn.settimeout(3)
            while True:
                chunk = conn.recv(100000).decode('UTF-8')
                print(chunk)
                if not chunk:
                    break
                fragments.append(chunk)
        except socket.timeout:
            print("Done reading.. connection timed out after receiving all data.")

        result = "".join(fragments)
        messages = result.split('||')
        for message in messages:
            if message.rstrip("\n"):
                data = json.loads(message)
                all_objects.append(data)
        analyze()
        res = ','.join(str(v) for v in all_objects)
        #conn.sendall(res.encode('UTF-8'))
        conn.send(json.dumps(res).encode("UTF-8"))


if __name__ == '__main__':
    # Accept incoming socket from the java program
    perform_operations()
