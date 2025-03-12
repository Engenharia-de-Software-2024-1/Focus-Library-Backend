#!/bin/bash

sudo docker-compose up -d
chmod +x gradlew
./gradlew bootrun
