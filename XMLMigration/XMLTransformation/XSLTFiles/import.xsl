<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:template match="products">
		<PRODUCTS>
			<xsl:apply-templates/>
		</PRODUCTS>
	</xsl:template>
	
	<xsl:template match="product">
		<PRODUCT id="{@id}" price="{@price}" stock="{@stock}"/>
	</xsl:template>
	
</xsl:stylesheet> 