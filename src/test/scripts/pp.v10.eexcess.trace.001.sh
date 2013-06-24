#!/bin/bash

curl -XPOST "http://localhost:9200/privacy/trace/nZ7BngPpRfaRjGltQLOMjA" -d '{"user":{"email":"test@insa-lyon.fr"},"temporal":{"begin":"2013-06-24T14:12Z", "end":"2013-06-24T14:12Z"},"document":{"url":"http:fr.wikipedia.org/wiki/Liste_des_gouverneurs_du_Colorado","title":"Liste des gouverneurs du Colorado - Wikipédia}"}}'


#curl -XGET "http://localhost:8888/v0/eexcess/trace/_search?q=test:001" #| jsontest 'hits.total == 1'





