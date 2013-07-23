<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:json="http://www.objectml.org/ns/data/xjson">

	<xsl:output method="html" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<ul data-hits="{count(/json:object/json:array[@name='Documents']/json:object)}">
			<xsl:apply-templates select="/json:object/json:array[@name='Documents']/json:object" />
		</ul>	
	</xsl:template>


	<xsl:template match="json:array[@name='Documents']/json:object">
		
			<li>
			
			
			
			<xsl:choose>
				<xsl:when test="current()/json:value[@name='origin']/text()= 'EconBiz'">
					<image src="http://www.econbiz.de/favicon.ico" alt="EconBiz"></image>
				</xsl:when>
				<xsl:when test="current()/json:value[@name='origin']/text()= 'Mendeley'">
					<img src="http://www.mendeley.com/favicon.ico" alt="Mendeley"></img>
				</xsl:when>
			</xsl:choose>
			
				<h4 class="recommendation"> 
					<xsl:choose>
						<xsl:when test="current()/json:value[@name='url']">
							  <a href="{current()/json:value[@name='url']/text()}" target="_blank" > 
									<xsl:value-of select="current()/json:value[@name='title']"/>
							  </a>
						</xsl:when>
						<xsl:otherwise>
							
								<xsl:value-of select="current()/json:value[@name='title']"/>
							 
						</xsl:otherwise>	  
						  
					 </xsl:choose>
				</h4>
				<p>
					<xsl:if test="current()/json:value[@name='score']">
						Score : <xsl:value-of select="current()/json:value[@name='score']/text()"/>
					</xsl:if>
					
				</p>
			</li>
		
		
	
	</xsl:template>
	


	

</xsl:stylesheet>
