#!/bin/bash

curl -XPUT "http://localhost:9200/privacy/trace/_mapping" -d '
{
	"trace": {
		"properties": {
			"user.email": {"type": "string","index": "not_analyzed"}
		}
	}
}
'