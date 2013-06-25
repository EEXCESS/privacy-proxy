#!/bin/bash

curl -XPOST "http://localhost:12564/api/v0/users/data" -d {"email":"toto@insa","uuid":"123456","username":"toto","password":"pizza"}


#curl -XGET "http://localhost:8888/v0/eexcess/trace/_search?q=test:001" #| jsontest 'hits.total == 1'





