<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2010, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

  <xsl:template match="/config">
    <xsl:comment>
      *****************************************************************
      Licensed Materials - Property of IBM

      Copyright IBM Corp. 2001, 2010 All Rights Reserved.

      US Government Users Restricted Rights - Use, duplication or
      disclosure restricted by GSA ADP Schedule Contract with
      IBM Corp.
      *****************************************************************
    </xsl:comment>

    <config	id="contentreview-config"
			version="3.0"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="contentreview-config.xsd">

<xsl:comment>
	Flag categories are used when issueCategorization is enabled for a specific service. 
	When turned on, end users who flag content as inappropriate must select a category for 
	the content. Two default categories are provided. You can edit those categories or extend 
	the list by adding new categories in this section. For more information, see the topic 
	"Managing content moderation and flagged content" in the Administration section of the 
	product documentation.
</xsl:comment>

	<xsl:copy-of select="flagCategories" />

<xsl:comment>
	content moderation settings
</xsl:comment>

<xsl:comment>

Settings for instance level communities onwer moderation. 

"forceForAllCommunities" is used to set whether to let each community owners turn on/off moderation of their own 
community.

"enabledByCreation" is used to set whether moderation should be turn on/off by creation of new communities.

</xsl:comment>

<commModerationConfiguration>
	<preModeration>
		<forceForAllCommunities enabled="false" />
		<enabledByCreation enabled="false" />
	</preModeration>
	<postModeration>
		<forceForAllCommunities enabled="false" />
		<enabledByCreation enabled="false" />
	</postModeration>
</commModerationConfiguration>

<xsl:comment>
	See the topic "Managing content moderation" in the Connection Information Center for details.

	&lt;reviewer&gt;, these are the reviewers that will receive email notification when categorization is
	turned on for the flag inappropriate content feature. Reviewers must be assigned the J2EE moderator role.
	&lt;flagCategory id="xxxx"&gt;, id is the &lt;id&gt; element defined in the &lt;flagCategory&gt;
	When categorization is turned on, flagging content will require users to select a category
	and e-mail notification will be sent to the reviewers assigned to the selected category
</xsl:comment>

    <serviceConfiguration>
	<xsl:for-each select="serviceConfiguration">
		<xsl:for-each select="service">
			<xsl:choose>
				<xsl:when test="@id='blogs'">
				<service id="blogs">
					<xsl:copy-of select="contentApproval" />
	<contentFlagging>
		<xsl:attribute name="enabled">
				    <xsl:value-of select="contentFlagging/@enabled"/>
				</xsl:attribute>
		<ownerModerate enabled="false"/>
	<xsl:copy-of select="contentFlagging/issueCategorization" />
	</contentFlagging>
<xsl:comment>
	Moderators specified here will receive e-mail notification about content 
	needing moderation for this service. 
</xsl:comment>
					<xsl:copy-of select="moderator" />
				</service>
				</xsl:when>


				<xsl:when test="@id='files'">
				<service id="files">

	<contentApproval>
		<xsl:attribute name="enabled">
				    <xsl:value-of select="contentApproval/@enabled"/>
				</xsl:attribute>
		<ownerModerate enabled="false"/>
	</contentApproval>

	<contentFlagging>
		<xsl:attribute name="enabled">
				    <xsl:value-of select="contentFlagging/@enabled"/>
				</xsl:attribute>
		<ownerModerate enabled="false"/>
	<xsl:copy-of select="contentFlagging/issueCategorization" />
	</contentFlagging>
<xsl:comment>
	Moderators specified here will receive e-mail notification about content 
	needing moderation for this service. 
</xsl:comment>
					<xsl:copy-of select="moderator" />
				</service>
				</xsl:when>

				<xsl:when test="@id='forums'">
					<xsl:copy-of select="." />
				</xsl:when>
			</xsl:choose>

      </xsl:for-each>


      </xsl:for-each>
    </serviceConfiguration>

    </config>

  </xsl:template>

</xsl:stylesheet>


























