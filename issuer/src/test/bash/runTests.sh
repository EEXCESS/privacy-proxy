#!/bin/bash

source ./common.sh

if [ "$#" -eq 1 ]; then 

	# Calls
	R1=`./getRegisteredPartners.sh $1 GET`
	R2=`./recommend.sh $1 POST ../resources/query-QF1.json`
	R3=`./recommend.sh $1 POST ../resources/query-QF2.json`
	R4=`./getDetails.sh $1 POST ../resources/query-QF3.json`
	R5=`./getCoOccurrenceGraph.sh $1 GET`
	R6=`./getMaximalCliques.sh $1 GET`
	#R7=`./suggestCategories.sh $1 POST ../resources/suggestCategories.json`
	#R8=`./recognizeEntity.sh $1 POST ../resources/recognizeEntity.json`
	R9a=`./log.sh $1 POST moduleOpened ../resources/log-moduleOpenedClosed.json`
	R9b=`./log.sh $1 POST moduleClosed ../resources/log-moduleOpenedClosed.json`
	R9c=`./log.sh $1 POST moduleStatisticsCollected ../resources/log-moduleStatistics.json`
	R9d=`./log.sh $1 POST itemOpened ../resources/log-itemClosed.json`
	R9e=`./log.sh $1 POST itemClosed ../resources/log-itemClosed.json`
	R9f=`./log.sh $1 POST itemCitedAsText ../resources/log-itemCited.json`
	R9g=`./log.sh $1 POST itemCitedAsImage ../resources/log-itemCited.json`
	R9h=`./log.sh $1 POST itemCitedAsHyperlink ../resources/log-itemCited.json`
	R9i=`./log.sh $1 POST itemRated ../resources/log-itemRated.json`
	R9j=`./log.sh $1 POST itemBookmarked ../resources/log-itemBookmarked.json`
	
	File1="Mendeley.png"
	rm $File1
	./getPartnerFavIcon.sh $1 GET Mendeley $File1
	File2="unknown.png"
	rm $File2
	./getPreviewImage.sh $1 GET unknown $File2
	
	# Verdicts	
	printVerdictJson "getRegisteredPartners" $R1
	printVerdictJson "recommend QF1" $R2
	printVerdictJson "recommend QF2" $R3
	printVerdictJson "getDetails" $R4
	printVerdictJson "getCoOccurrenceGraph" $R5
	printVerdictJson "getMaximalCliques" $R6
	
	printVerdictFile "getPartnerFavIcon" $File1
	printVerdictFile "getPreviewImage" $File2
	
	#printVerdictJson "suggestCategories" $R7
	#printVerdictJson "recognizeEntity" $R8
	
	printVerdictJson "log-moduleOpened" $R9a
	printVerdictJson "log-moduleClosed" $R9b
	printVerdictJson "log-moduleStatisticsCollected" $R9c
	printVerdictJson "log-itemOpened" $R9d
	printVerdictJson "log-itemClosed" $R9e
	printVerdictJson "log-itemCitedAsText" $R9f
	printVerdictJson "log-itemCitedAsImage" $R9g
	printVerdictJson "log-itemCitedAsHyperlink" $R9h
	printVerdictJson "log-itemRated" $R9i
	printVerdictJson "log-itemBookmarked" $R9j
	
	# Cleaning
	rm $File1
	rm $File2

else 
	echo "Usage 'runTests local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/'"
fi