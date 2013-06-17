#!/bin/bash
curl -XPOST http://localhost:9200/privacy_example/trace/ -d '{
  "document": {
    "url": "http://www.google.com/",
    "title": "Google"
  }
}'
