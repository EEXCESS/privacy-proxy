<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:json="http://www.objectml.org/ns/data/xjson">
	<xsl:output method="text" omit-xml-declaration="yes" />

	<xsl:template match="eexcess-results">
		<xsl:text>{</xsl:text>
		<xsl:text> "provider": "</xsl:text><xsl:value-of select="@provider" /><xsl:text>",</xsl:text>
		<xsl:text> "totalResults": "</xsl:text><xsl:value-of select="@totalResults" /><xsl:text>",</xsl:text>
			<xsl:text> "results": [</xsl:text>
				<xsl:apply-templates select="result" />
			<xsl:text>]</xsl:text>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<xsl:template match="result">
		<xsl:text>{</xsl:text>
		<xsl:for-each select="id|uri|title|creator|description|previewImage">
	        <xsl:text> "</xsl:text><xsl:value-of select="local-name()" /><xsl:text>":</xsl:text>
	        <xsl:text> "</xsl:text><xsl:value-of select="translate(.,'\&#x22;','')" /><xsl:text>", </xsl:text>
		</xsl:for-each>
        <xsl:text> "facets": {</xsl:text>
			<xsl:apply-templates select="facets/*" mode="facet" />
		<xsl:text>}</xsl:text>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() != last()">, </xsl:if>
	</xsl:template>

	<xsl:template match="*" mode="facet">
		<xsl:text>"</xsl:text>
			<xsl:choose>
				<xsl:when test="@name">
					<xsl:value-of select="@name" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="local-name()" />
				</xsl:otherwise>
			</xsl:choose>
		<xsl:text>": </xsl:text>
		<xsl:text>"</xsl:text><xsl:value-of select="." /><xsl:text>"</xsl:text>
		<xsl:if test="position() != last()">, </xsl:if>
	</xsl:template>
</xsl:stylesheet>
