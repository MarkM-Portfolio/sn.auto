<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs">
  <xsl:import-schema schema-location="import-schema_1.xsd"/>
  <xsl:output indent="yes"/>
  <xsl:template match="/">
  <xsl:document validation="strict">
    <products>
      <xsl:copy-of select="products/product"/>
      <product id="p6" name="Romeo" price="2250" stock="5" country="South Africa"/>
    </products>
    </xsl:document>
  </xsl:template>
</xsl:stylesheet> 