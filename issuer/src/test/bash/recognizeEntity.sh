#!/bin/bash

source ./common.sh

if [ "$#" -eq 3 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	File=$3
	ServiceURL=`echo $ServiceURL"recognizeEntity"`
	#curl -X $Method $ServiceURL -d @$File --header "Content-Type: application/json"
	curl -X POST "http://zaire.dimis.fim.uni-passau.de:8999/doser-disambiguationserverstable/webclassify/entityAndCategoryStatistic" -d @$File --header "Content-Type: application/json"
else 
	echo "Usage 'entityRecognition local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> POST input-test-file.json '"
fi