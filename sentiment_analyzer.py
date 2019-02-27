import json
import sys
import os

from textblob import TextBlob

all_objects = []


def read_data(file):
    # Clearing any previous data from the previous read file
    all_objects.clear()

    # Looping over file lines
    for line in open(file):
        data = json.loads(line)
        all_objects.append(data)


def write_to_file(objs, fname, dir):
    with open(dir + fname, 'r+') as f:
        f.seek(0)
        for line in objs:
            line = json.dumps(line)
            f.write(line + '\n')
        f.truncate()
    print("Sentiment analysis completed on " + fname)


def analyze(objects, filename, path):
    for obj in objects:
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
    write_to_file(all_objects, filename, path)


def initialize():
    try:
        if len(sys.argv) > 1:
            path = sys.argv[1]
            for file in os.listdir(path):
                if file.endswith(".json"):
                    read_data(path + file)
                    analyze(all_objects, file, path)
        else:
            print("ERROR! Please provide a file path!\nUsage: ./controller.sh <file-path>")
            sys.exit()
    except IndexError:
        print("ERROR! An exception has occurred, please check if the arguments were passed correctly and rerun")
        sys.exit()


initialize()
