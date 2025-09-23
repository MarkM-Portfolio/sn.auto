<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2012  All Rights Reserved.                    -->

<xsl:stylesheet version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tns="http://www.ibm.com/dogear-config-cell"
	xsi:schemaLocation="http://www.ibm.com/dogear-config-cell dogear-config-cell.xsd">

  <xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
  <xsl:template match="/tns:config">
    <xsl:comment>
      *****************************************************************

      Licensed Materials - Property of IBM

      5724-S68

      Copyright IBM Corp. 2001, 2010  All Rights Reserved.

      US Government Users Restricted Rights - Use, duplication or
      disclosure restricted by GSA ADP Schedule Contract with
      IBM Corp.

      *****************************************************************
    </xsl:comment>

    <config id="dogearcell" buildlevel="@BUILD_LEVEL@"
			xmlns="http://www.ibm.com/dogear-config-cell"
			xmlns:tns="http://www.ibm.com/dogear-config-cell"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.ibm.com/dogear-config-cell dogear-config-cell.xsd">

      <xsl:copy-of select="tns:contextParameters"/>

      <xsl:comment>Fav Icon Service</xsl:comment>
      <xsl:copy-of select="tns:favIconService"/>
      <xsl:comment>Broken URL Service</xsl:comment>
      <xsl:copy-of select="tns:brokenURLService"/>
      <xsl:comment>
        Intranet IP Range (inclusive)
      </xsl:comment>
      <xsl:copy-of select="tns:privateIntranetAllocationTable"/>
      <xsl:comment>
        Dogear Link Thresholds
      </xsl:comment>
      <xsl:copy-of select="tns:linkThresholds"/>
      <xsl:comment>
        Dogear Tag Thresholds
      </xsl:comment>
      <xsl:copy-of select="tns:tagThresholds"/>
      <xsl:comment>
        Dogear Person Thresholds
      </xsl:comment>
      <xsl:copy-of select="tns:personThresholds"/>
      <xsl:comment>
        Content Type Definitions
      </xsl:comment>
      <xsl:copy-of select="tns:contentTypes"/>

      <xsl:copy-of select="tns:activeContentFilter"/>

      <xsl:copy-of select="tns:redirectDogearThis"/>

      <xsl:copy-of select="tns:cachePublicData"/>
      <xsl:comment>
        Dogear Cache Factory
        Note: Some config params may be ignored by certain cache implementations. The class
        attribute is always required.  The LruCacheFactory is not clusterable.
      </xsl:comment>
      <xsl:copy-of select="tns:cacheFactory"/>
      <xsl:comment>
        The background link purging task
      </xsl:comment>
      <xsl:copy-of select="tns:linkPurgingTask"/>
      <xsl:comment>
        The configurable behaviors in Bookmarks (aka Dogear)
      </xsl:comment>
      <xsl:copy-of select="tns:serviceBehavior" />
      <xsl:comment>
        The embed snippet CSS white list
      </xsl:comment>
      <xsl:call-template name="embedStyle-new"/>
    </config>
  </xsl:template>
  <xsl:template name="embedStyle-new">
    <xsl:element name="embedStyle" namespace="http://www.ibm.com/dogear-config-cell">
      <xsl:attribute name="enabled">false</xsl:attribute>
      <xsl:comment> font family white list</xsl:comment>
      <xsl:element name="stringProperty" namespace="http://www.ibm.com/dogear-config-cell">
        <xsl:attribute name="name">font.familyList</xsl:attribute>Times,Arial,Times New Roman,Verdana,Helvetica,Geneva</xsl:element>
      <xsl:comment> font color white list</xsl:comment>
      <xsl:element name="stringProperty" namespace="http://www.ibm.com/dogear-config-cell">
        <xsl:attribute name="name">font.colorList</xsl:attribute>red,blue,green,yellow,orange,black,white,gold,snow</xsl:element>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
