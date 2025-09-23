<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes"/>
  <xsl:attribute-set name="img-std_attributes">
    <xsl:attribute name="width">300px</xsl:attribute>
    <xsl:attribute name="length">200px</xsl:attribute>
    <xsl:attribute name="alt"/>
  </xsl:attribute-set>
  <xsl:template match="/">
    <images>
      <xsl:for-each select="images/image">
        <img src="{@href}" xsl:use-attribute-sets="img-std_attributes"/>
      </xsl:for-each>
    </images>
  </xsl:template>
</xsl:stylesheet> 