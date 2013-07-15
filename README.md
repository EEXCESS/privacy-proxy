privacy-proxy
=============

The EEXCESS privacy preserving proxy server


Installation 
=============

Contents
--------
<ul>
	<li><a href="#elasticsearch">Install ElasticSearch</a></li>
	<li>	<ul>
			<li><a href="#eclipse">Install Eclipse</a></li>
			<li><a href="#gitplugin">Install a git integration plugin</a></li>
			<li><a href="#mavenplugin">Install a maven integration plugin</a></li>
		</ul>
	</li>
	<li><a href="#project">Install and set up the project</a></li>
</ul>



<a name="elasticsearch"></a>
ElasticSearch
-------------
Download and extract the latest version : http://www.elasticsearch.org/download/

You can get more informations about installing and setting elasticsearch at 
	<http://www.elasticsearch.org/guide/reference/setup/>

You can add the head plugin which allows you to interact easily with your ElasticSearch cluster by running :
	elasticsearch/bin/plugin -install mobz/elasticsearch-head
Then all you need is to open the following url into your web browser :
	<http://localhost:9200/_plugin/head/>

( doc : <http://mobz.github.io/elasticsearch-head/>   )


Eclipse and the needed plugins
------------------------------
<a name="eclipse"></a>
You can download Eclipse's latest version at http://www.eclipse.org/downloads/

<a name="gitplugin"></a>
You should have a git integration plugin already installed, but if it is not the case you can get it via the Eclipse menu "Help > Install New Software ...". Add a new site, the repository name is updates and the location http://download.eclipse.org/egit/updates
Then you have to choose Eclipse Git Team Provider, and to follow the instructios to install it or to update it if you allready have it.

<a name="mavenplugin"></a>
You also need a Maven Integration Plugin for Eclipse, you can get one in the Install New Software panel, at the following site : 
	http://download.eclipse.org/technology/m2e/releases


<a name="project"></a>
Downloading and setting up the project
-----------------------------------

Import the projects with the git plugin : File > import , select Git > Projects from Git

The URI for the json-n-xml project is :
	https://github.com/bhabegger/json-n-xml.git

The URI for the privacy-proxy is :
	https://github.com/EEXCESS/privacy-proxy.git

Set the json-n-xml and the privacy-proxy projects as maven projects by right clicking on them in the project explorer panel, configure > convert to maven project

Run privacy-proxy/src/config/mapping.sh to configure the ElasticSearch indexes

Install the Google Chrome plugin :
	chrome://extensions, enable the developer mode checkbox, load unpacked extension and select the privacy-proxy/src/chrome folder in the privacy-proxy sources.

Run the proxy ( APIService.java ) as "java application"




