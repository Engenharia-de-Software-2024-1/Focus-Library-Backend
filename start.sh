#!/bin/bash

sudo docker-compose up -d
docker start postgre_focus
chmod +x gradlew
./gradlew bootrun
