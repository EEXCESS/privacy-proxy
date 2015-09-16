#!/bin/bash

source ./common.sh

if [ "$#" -eq 3 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	File=$3
	ServiceURL=`echo $ServiceURL"entityRecognition"`
	curl -X $Method $ServiceURL -d @$File --header "Content-Type: application/json"
else 
	echo "Usage 'entityRecognition local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> POST input-test-file.json '"
fi