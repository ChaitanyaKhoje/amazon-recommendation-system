import json
import sys
import os
import socket
from kafka import KafkaConsumer

from textblob import TextBlob

all_objects = []
fragments = []


def analyze():
    print("Performing sentiment analysis..")
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


def perform_operations():
    # Read connection details from the connections.json
    dir_path = os.path.dirname(os.path.realpath(__file__))  # Get current directory
    f = open(dir_path + '/connections.json', 'r')
    json_result = json.load(f)
    json_list = json_result['servers']
    conn_details = json_list[0].get('python')
    ip = conn_details[0].get('ip', '')
    port = int(conn_details[1].get('port', ''))
    f.close()

    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = (ip, port)
    print("---------------------------")
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
                if not chunk:
                    break
                fragments.append(chunk)
        except socket.timeout:
            print("Done reading.. connection timed out after receiving all data.")

        print("Processing received data...")
        result = "".join(fragments)
        messages = result.split('||')
        for message in messages:
            if message.rstrip("\n"):
                data = json.loads(message)
                all_objects.append(data)
        analyze()
        print("Sentiment analysis complete.")
        print("Attempting to send results..")
        # res = ','.join(str(v) for v in all_objects)
        # conn.sendall(res.encode('UTF-8'))
        conn.sendall(json.dumps(all_objects).encode("UTF-8"))
        conn.close()
        print("Done.")


if __name__ == '__main__':
    # Accept incoming socket from the java program
    perform_operations()
