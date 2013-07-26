<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:json="http://www.objectml.org/ns/data/xjson">

	<xsl:output method="xml"/>

	<xsl:template match="/">
		<json:object>
			<!--  for each field : check if there is a value in the eexcess datas, if not take the corresponding datas from mendeley -->
			<json:value name="username">
				<xsl:apply-templates select="/" mode="username" />
			</json:value>
			<json:value name="email">
				<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='email']"/>
			</json:value>
			<json:value name="password">
				<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text()='eexcess']//json:value[@name='password']/text()"/>
			</json:value>
			<json:object name="privacy">
				
				<json:value name="email">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='email']"/>		
				</json:value>
				<json:value name="gender">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='gender']"/>						
				</json:value>
				<json:value name="title">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='title']"/>		
				</json:value>
				<json:value name="traces">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='traces']"/>		
				</json:value>
				<json:value name="geoloc">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='geoloc']"/>						
				</json:value>
				<json:value name="age">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='age']"/>						
				</json:value>
				<json:value name="address">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='privacy']/json:value[@name='address']"/>						
				</json:value>
			</json:object>
				 <json:value name="title">
						<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='title']/text()"/>
				</json:value>
						  
		  <!--   <xsl:choose>
				<xsl:when test="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='lastname']">
			-->		
					 <json:value name="lastname">
						<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='lastname']/text()"/>			
					</json:value>
				<!--  
				</xsl:when>
				<xsl:otherwise>
							
					<xsl:value-of select="current()/json:value[@name='title']"/>
							 
				</xsl:otherwise>	  
						  
			</xsl:choose>
			-->
			
			<json:value name="firstname">
				<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='firstname']/text()"/>			
			</json:value>
			<json:value name="gender">
				<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='gender']/text()"/>			
			</json:value>
			<json:value name="birthdate">
				<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']/json:value[@name='birthdate']/text()"/>			
			</json:value>
			<json:object name="address">
				<json:value name="street">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']//json:value[@name='street']/text()"/>				
				</json:value>
				<json:value name="postalcode">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']//json:value[@name='postalcode']/text()"/>								
				</json:value>
				<json:value name="city">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']//json:value[@name='city']/text()"/>								
				</json:value>
				<json:value name="country">
					<xsl:value-of select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:object[@name='profile_data']//json:value[@name='country']/text()"/>								
				</json:value>
			</json:object>
			
			
			
			
			<json:array name="topics">
			
				<xsl:apply-templates select="//json:object[@name='_source' and json:value[@name='source']/text() = 'eexcess']//json:array[@name='topics']/json:object" />
				
			</json:array> 
			
			
			
		</json:object>
		
		
		
	</xsl:template>
	<xsl:template match="json:object[./json:value[@name='label']]">
		
		<json:object>
			<json:value name='label'>
				<xsl:value-of select="current()/json:value[@name='label']"/>
			</json:value>
		</json:object>
	
	</xsl:template>


	

	<xsl:template match="/" mode="username">
		<xsl:variable name="userGiven" select="//json:object[@name='_source' and json:value[@name='source']/text()='eexcess']//json:value[@name='username']/text()"/>
		<xsl:choose>
			<xsl:when test="$userGiven"></xsl:when>
			<xsl:when test="mendeley"
		</xsl:choose>
						
	
	</xsl:template>
</xsl:stylesheet>
