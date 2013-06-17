#!/bin/bash

curl -XPOST "http://localhost:8888/api/v0/eexcess/trace" -d '
  "document":"Google"
'


#curl -XGET "http://localhost:8888/v0/eexcess/trace/_search?q=test:001" #| jsontest 'hits.total == 1'





