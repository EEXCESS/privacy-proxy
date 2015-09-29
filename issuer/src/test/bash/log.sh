#!/bin/bash

source ./common.sh

if [ "$#" -eq 4 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	Interaction=$3
	File=$4
	ServiceURL=`echo $ServiceURL"log/$3"`
	echo $ServiceURL
	curl -X $Method $ServiceURL -d @$File --header "Content-Type: application/json"
else 
	echo "Usage 'log local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> POST|OPTIONS moduleOpened|...|itemRated input-file.json'"
fi
