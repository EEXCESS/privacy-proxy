#!/bin/bash


curl -XPUT 'http://localhost:9200/privacy/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'

curl -XPUT 'http://localhost:9200/recommend/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'

curl -XPUT "http://localhost:9200/privacy/trace/_mapping" -d '
{
	"trace": {
		"properties": {
			"user.email": {"type": "string","index": "not_analyzed"},
			"temporal.begin": {"type": "string"},
			"temporal.end": {"type": "string"}
		}
	}
}
'
