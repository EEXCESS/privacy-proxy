#!/bin/bash

echo -n "Creating privacy index: "
curl -XPUT 'http://localhost:9200/privacy/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'
echo

echo -n "Creating users index: "
curl -XPUT 'http://localhost:9200/users/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'
echo

echo -n "Creating profiles index: "
curl -XPUT 'http://localhost:9200/profiles/' -d '
index :
    number_of_shards : 5
    number_of_replicas : 1
'
echo

echo -n "Creating mappings for 'data' type in user index: "
curl -XPUT "http://localhost:9200/users/data/_mapping" -d '
{
	"data": {
		"properties": {
			"email": {"type": "string","index": "not_analyzed"}
		}
	}
}
'
echo

echo -n "Creating mapping for 'trace' type in privacy index: "
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
echo
