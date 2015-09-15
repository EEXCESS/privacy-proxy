#!/bin/bash

source ./common.sh

if [ "$#" -eq 4 ]; then 
	ServiceURL=`extractUrl $1`
	Method=$2
	Type=$3
	OuputFile=$4
	ServiceURL=`echo $ServiceURL"getPreviewImage?type={$3}"`
	curl -X $Method $ServiceURL -o $4
else 
	echo "Usage 'getPreviewImage local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/> GET other|unknown|text|audio|3d|image|video outputFile'"
fi

