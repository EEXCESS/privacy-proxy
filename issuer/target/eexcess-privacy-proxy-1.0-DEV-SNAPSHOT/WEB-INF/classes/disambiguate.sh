#!/bin/bash

source ./common.sh

if [ "$#" -eq 3 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	File=$3
	ServiceURL=$ServiceURL"api/v1/disambiguate"
	curl -X $Method $ServiceURL -d @$File --header "Content-Type: application/json"
	#curl -X POST "http://zaire.dimis.fim.uni-passau.de:8383/code-server/disambiguation/categorysuggestion" -d @$File --header "Content-Type: application/json"
else 
	echo "Usage 'disambiguate local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> POST|OPTIONS input-test-file.json '"
fi

