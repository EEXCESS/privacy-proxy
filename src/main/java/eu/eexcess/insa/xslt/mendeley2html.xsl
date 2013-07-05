<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:json="http://www.objectml.org/ns/data/xjson">

	<xsl:output method="html" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<ul data-hits="{count(/json:object/json:array[@name='documents']/json:object)}">
			<xsl:apply-templates select="/json:object/json:array[@name='documents']/json:object" />
		</ul>	
	</xsl:template>


	<xsl:template match="json:array[@name='documents']/json:object">
		
			<li>
				<img src="http://www.mendeley.com/favicon.ico" alt="Mendeley"></img>
				<h4 class="recommendation"> 
					<a href="{current()/json:value[@name='mendeley_url']/text()}" target="_blank" > 
							<xsl:value-of select="current()/json:value[@name='title']/text()"/>
					  </a>
				</h4>
				
			</li>
		
		
	
	</xsl:template>
	


	

</xsl:stylesheet>
