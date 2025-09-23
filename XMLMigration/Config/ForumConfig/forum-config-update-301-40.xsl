<?xml version="1.0" encoding="UTF-8" ?>
<!-- Copyright IBM Corp. 2009, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:noNamespaceSchemaLocation="forum-config.xsd">

  <xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
  <xsl:template match="/config">
    <xsl:comment>
      *****************************************************************

      IBM Confidential
      
      OCO Source Materials

      Copyright IBM Corp. 2009, 2012

      The source code for this program is not published or otherwise
      divested of its trade secrets, irrespective of what has been
      deposited with the U.S. Copyright Office.

      *****************************************************************
      
      5724-S68
    </xsl:comment>
	<config version="4" id="forum-config" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:noNamespaceSchemaLocation="forum-config.xsd">
     	<xsl:comment>Forums Configuration</xsl:comment>

      	<xsl:comment>
       		 The Event Broker is used to control the transmission of events to the Homepage
    	</xsl:comment>
     	<eventBroker>
      		<xsl:comment>
      			Maintain this XML formatting for the subscriber element so it can be handled by the installer
      			This provider sends Audit / News events to the Homepage when data changing events occu
      		</xsl:comment>
     	 	<xsl:copy-of select="eventBroker/subscriber"/>
     	</eventBroker>
      
		<scheduledTasks>
			<xsl:comment>
				cluster scoped jobs
			</xsl:comment>
			
			<xsl:comment>
				TrashAutoPurgeJob - weekly on Sundays at 2:00AM  0 0 2 ? * SUN
			</xsl:comment>
			<xsl:copy-of select="scheduledTasks/task"/>
     	</scheduledTasks>
     	
     	<xsl:comment>
     		used to configure deployment mode 'with categories' or 'no categories
     	</xsl:comment>

     	<deployment>
     		<xsl:attribute name="enableCategory" ><xsl:value-of select="deployment/@enableCategory"/></xsl:attribute>
     		<xsl:attribute name="enableLotusLive" >false</xsl:attribute>
     		<xsl:choose>
	     		<xsl:when test="deployment/@enableNonMemberContributor">
	     			<xsl:attribute name="enableNonMemberContributor" ><xsl:value-of select="deployment/@enableNonMemberContributor"/></xsl:attribute>
	     		</xsl:when>
	     		<xsl:otherwise>
	     			<xsl:attribute name="enableNonMemberContributor" >false</xsl:attribute>
	     		</xsl:otherwise>
	     	</xsl:choose>
     	</deployment>
     	
     	
      	<xsl:comment>
	        The Object Store is used to store attachments. Multiple object stores can be specified, but
	        only one can be marked as "default". The default store is used for all new content. The "id"
	        element must be a unique name, it does not need to specify a specific type or be a keyword
	        like "filesystem".
     	</xsl:comment>
     	<xsl:copy-of select="objectStore"/>

		<xsl:comment>
			used to be 'management'
		</xsl:comment>
     	<xsl:copy-of select="registerMbeans"/>

      	<xsl:comment>
       		Define content that will be removed from forum data
     	</xsl:comment>
      	<xsl:copy-of select="activeContentFilter"/>
      	
      	<xsl:comment>
       		Define whether "discuss this" enabled and remote deployment information
     	</xsl:comment>
		<discussThis enabled="false">
			<targetBookmarklet>http://{server}/connections/bookmarklet</targetBookmarklet>
		</discussThis>
    </config>
  </xsl:template>
</xsl:stylesheet>
