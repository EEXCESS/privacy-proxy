#!/bin/bash

source ./common.sh

if [ "$#" -eq 1 ]; then 

	R1=`./getRegisteredPartners.sh $1 GET`
	R2=`./recommend.sh $1 POST ../resources/query-QF1.json`
	R3=`./recommend.sh $1 POST ../resources/query-QF2.json`
	R4=`./getDetails.sh $1 POST ../resources/query-QF3.json`
	R5=`./getCoOccurrenceGraph.sh $1 GET`
	R6=`./getMaximalCliques.sh $1 GET`
	File1="Mendeley.png"
	rm $File1
	./getPartnerFavIcon.sh $1 GET Mendeley $File1
	File2="unknown.png"
	rm $File2
	./getPreviewImage.sh $1 GET unknown $File2
	
	printVerdictJson "getRegisteredPartners" $R1
	printVerdictJson "recommend QF1" $R2
	printVerdictJson "recommend QF2" $R3
	printVerdictJson "getDetails" $R4
	printVerdictJson "getCoOccurrenceGraph" $R5
	printVerdictJson "getMaximalCliques" $R6

	printVerdictFile "getPartnerFavIcon" $File1
	printVerdictFile "getPreviewImage" $File2

else 
	echo "Usage 'runTests local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/'"
fi