#!/bin/bash

if [ "$#" -ne 1 ]; then
  echo "Please supply topic name"
  exit 1
fi


nohup bin/zookeeper-server-start.sh config/zookeeper.properties
sleep 2
nohup bin/kafka-server-start.sh config/server.properties
sleep 2

$(dirname "$0")/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic $1
$(dirname "$0")/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic $1