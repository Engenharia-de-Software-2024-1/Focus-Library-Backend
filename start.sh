#!/bin/bash

sudo docker-compose up -d
sudo docker start postgre_focus
chmod +x gradlew
./gradlew bootrun
