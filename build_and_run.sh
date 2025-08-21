#! /bin/bash

docker build . -t web-crawler-api
docker run -e BASE_URL=http://172.17.0.2:8080 -p 4567:4567 --rm web-crawler-api
