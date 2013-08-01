#!/bin/bash


curl -XPUT 'http://localhost:9200/privacy/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'

curl -XPUT 'http://localhost:9200/users/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'

curl -XPUT 'http://localhost:9200/profiles/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'

curl -XPUT "http://localhost:9200/users/data/_mapping" -d '
{
	"data": {
		"properties": {
			"email": {"type": "string","index": "not_analyzed"}
		}
	}
}
'

curl -XPUT "http://localhost:9200/privacy/trace/_mapping" -d '
{
	"trace": {
		"properties": {
			"plugin.uuid": {"type": "string","index": "not_analyzed"},
			"user.email": {"type": "string","index": "not_analyzed"},
			"temporal.begin": {"type": "date"},
			"temporal.end": {"type": "date"},
			"user.user_id": {"type": "string","index": "not_analyzed"}
		}
	}
}
'
