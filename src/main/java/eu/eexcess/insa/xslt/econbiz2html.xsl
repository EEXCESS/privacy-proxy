<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:json="http://www.objectml.org/ns/data/xjson">

	<xsl:output method="html" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<ul data-hits="{count(/json:object/json:object/json:array[@name='hits']/json:object)}">
			<xsl:apply-templates select="/json:object/json:object/json:array[@name='hits']/json:object" />
		</ul>	
	</xsl:template>


	<xsl:template match="json:array[@name='hits']/json:object">
		
			<li>
				<h4> 
					<xsl:choose>
						<xsl:when test="current()/json:array[@name='identifier_url']/json:value">
							  <a href="{current()/json:array[@name='identifier_url']/json:value/text()}" target="_blank" > 
									<xsl:value-of select="current()/json:value[@name='title']"/>
							  </a>
						</xsl:when>
						<xsl:otherwise>
							
								<xsl:value-of select="current()/json:value[@name='title']"/>
							 
						</xsl:otherwise>	  
						  
					 </xsl:choose>
				</h4>
				<p>
					Score : <xsl:value-of select="current()/json:value[@name='_score']/text()"/>
				</p>
			</li>
		
		
	
	</xsl:template>
	


	

</xsl:stylesheet>
