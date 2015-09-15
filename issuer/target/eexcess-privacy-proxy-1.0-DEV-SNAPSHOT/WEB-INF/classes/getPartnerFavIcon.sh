#!/bin/bash

source ./common.sh

if [ "$#" -eq 4 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	Id=$3
	OuputFile=$4
	ServiceURL=`echo $ServiceURL"api/v1/getPartnerFavIcon?partnerId={$Id}"`
	curl -X $Method $ServiceURL -o $4
else 
	echo "Usage 'getPartnerFavIcon local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> GET partnerID outputFile'"
fi

