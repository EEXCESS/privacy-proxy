privacy-proxy
=============

The EEXCESS privacy preserving proxy server


Requirements
============
The json-n-xml project needs to be intalled, you can get it at 
	https://github.com/bhabegger/json-n-xml.git

ElasticSearch on the same host, listening to default port ( 9200 )


Installation 
=============
Be sure to have the maven integation plugin installed in Eclipse (m2e-Maven Integration for Eclipse (Incubation))

Set the json-n-xml and the privacy-proxy projects as maen projects

Run privacy-proxy/src/config/mapping.sh to configure the ElasticSearch indexes

Install the Google Chrome plugin :
	chrome://extensions, enable the developer mode checkbox, load unpacked extension and select the privacy-proxy/src/chrome folder in the privacy-proxy sources.

Run the proxy ( APIService.java )




