#!/bin/bash

source ./common.sh

if [ "$#" -eq 3 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	File=$3
	ServiceURL=`echo $ServiceURL"recommend"`
	curl -X $Method $ServiceURL -d @$File --header "Content-Type: application/json"
else 
	echo "Usage 'recommend local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> POST|OPTIONS input-test-file.json'"
fi
