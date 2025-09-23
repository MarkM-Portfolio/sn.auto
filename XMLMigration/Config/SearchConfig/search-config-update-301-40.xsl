<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2009, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:tns="http://www.ibm.com/LotusConnectionsSearch"
	xsi:schemaLocation="http://www.ibm.com/LotusConnections-config search-config.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/tns:config">
		<xsl:comment>
*****************************************************************
                                                                 
 Licensed Materials - Property of IBM                            
                                                                 
 5724-E76                                                        
                                                                 
 Copyright IBM Corp. 2001, 2012  All Rights Reserved.            
                                                                 
 US Government Users Restricted Rights - Use, duplication or     
 disclosure restricted by GSA ADP Schedule Contract with         
 IBM Corp.                                                       
                                                                 
*****************************************************************
		</xsl:comment>

		<config id="searchConfig" version="14"
			buildlevel="${config.BuildLevelStr}"
			xmlns="http://www.ibm.com/LotusConnectionsSearch"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.ibm.com/LotusConnectionsSearch search-config.xsd ">

			<xsl:copy-of select="tns:indexingSchedule" />
		
			<xsl:apply-templates select="tns:indexSettings"/>			
			
			<xsl:apply-templates select="tns:crawlerSettings"/>
			
			<xsl:copy-of select="tns:languageSupport"/>
			
			<xsl:call-template name="migrate-services-element"/>
					
			<xsl:call-template name="migrate-content-sources-element"/>
			
			<xsl:apply-templates select="tns:sandTuning"/>	
					
			<xsl:apply-templates select="tns:attachmentHandling"/>
			
			<xsl:copy-of select="tns:sandConfiguration" xmlns="http://www.ibm.com/LotusConnectionsSearch"/> 
		
			<xsl:copy-of select="tns:backupSettings" xmlns="http://www.ibm.com/LotusConnectionsSearch"/> 
			
			<verboseLogging xmlns="http://www.ibm.com/LotusConnectionsSearch" enabled="true">
				<seedlistRequests xmlns="http://www.ibm.com/LotusConnectionsSearch" interval="1" />
				<initial xmlns="http://www.ibm.com/LotusConnectionsSearch" interval="250" />
				<incrementalCrawling xmlns="http://www.ibm.com/LotusConnectionsSearch" interval="100"/>
				<incrementalBuilding xmlns="http://www.ibm.com/LotusConnectionsSearch" interval="100"/>
			</verboseLogging>
		</config>
	</xsl:template>
	
	<xsl:template  name="migrate-index-settings-element" match="tns:indexSettings">
		<xsl:copy>
				<xsl:attribute name="location">${SEARCH_INDEX_DIR}</xsl:attribute>
				<xsl:if test="string-length(@maxIndexSize)>0">
					<xsl:attribute name="maxIndexSize">
						<xsl:value-of select="@maxIndexSize"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="maxIndexerThreads">2</xsl:attribute>	
				<xsl:attribute name="allowResumption">true</xsl:attribute>				
		</xsl:copy>	
	</xsl:template>
	
	<xsl:template name="migrate-services-element">
		<services xmlns="http://www.ibm.com/LotusConnectionsSearch">
			<connService  xmlns="http://www.ibm.com/LotusConnectionsSearch" name="activities" contentSource="activitiesContentSource"/>
			<connService  xmlns="http://www.ibm.com/LotusConnectionsSearch" name="calendar" contentSource="calendarContentSource"/>
			<xsl:for-each select="/tns:config/tns:seedlistSettings/tns:service[@name!='activities']">
					<connService>
						<xsl:attribute name="name">
							<xsl:value-of select="@name"/>
						</xsl:attribute>
						<xsl:attribute name="contentSource">
							<xsl:value-of select="concat(@name,'SeedlistContentSource')"/>
						</xsl:attribute>
					</connService>
			</xsl:for-each>
		</services>
	</xsl:template>

	<xsl:template name="migrate-content-sources-element">
		<contentSources xmlns="http://www.ibm.com/LotusConnectionsSearch">
			<seedlist xmlns="http://www.ibm.com/LotusConnectionsSearch" name="activitiesContentSource" pageSize="100"/>
			<seedlist xmlns="http://www.ibm.com/LotusConnectionsSearch" name="calendarContentSource" seedlistLocation="calendar/seedlist/myserver"/>
			<xsl:for-each select="/tns:config/tns:seedlistSettings/tns:service[@name!='activities']">
					<seedlist>
						<xsl:attribute name="name">
							<xsl:value-of select="concat(@name,'SeedlistContentSource')"/>
						</xsl:attribute>
						<xsl:if test="string-length(@pageSize)>0">
								<xsl:attribute name="pageSize">
									<xsl:value-of select="@pageSize"/>
								</xsl:attribute>
						</xsl:if>				
						<xsl:if test="string-length(@servletLocation)>0">
								<xsl:attribute name="seedlistLocation">
									<xsl:value-of select="@servletLocation"/>
								</xsl:attribute>
						</xsl:if>		
					</seedlist>
			</xsl:for-each>
		</contentSources>
	</xsl:template>	
		
	<xsl:template  name="migrate-backup-setttings-element" match="tns:backupSettings">
		<backupSettings 	xmlns="http://www.ibm.com/LotusConnectionsSearch">
				<xsl:attribute name="type">
					<xsl:value-of select="@type"/>					
				</xsl:attribute>
				<xsl:if test="string-length(@oncomplete)>0">
					<xsl:attribute name="oncomplete">
						<xsl:value-of select="@oncomplete"/>
					</xsl:attribute>
				</xsl:if>	
				<xsl:attribute name="location">	
					<xsl:value-of select="@location"/>					
				</xsl:attribute>					
		</backupSettings>	
	</xsl:template>

	<xsl:template name="migrate-crawler-settings" match="tns:crawlerSettings">
		<xsl:element name="crawlerSettings" 	xmlns="http://www.ibm.com/LotusConnectionsSearch">
				<xsl:attribute name="maxCrawlerThreads">3</xsl:attribute>		
				<xsl:attribute name="persistenceLocation">${CRAWLER_PAGE_PERSISTENCE_DIR}</xsl:attribute>
				<xsl:variable name="oldSeedlistSettingsPageSize" select="/tns:config/tns:seedlistSettings/@pageSize"/>
				<xsl:choose>
					<xsl:when test="string-length($oldSeedlistSettingsPageSize)>0">
						<xsl:attribute name="pageSize">
							<xsl:value-of select="$oldSeedlistSettingsPageSize"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="string-length(@pageSize)>0">
								<xsl:attribute name="pageSize">
								<xsl:value-of select="@pageSize"/>
								</xsl:attribute>
							</xsl:when>	
							<xsl:otherwise>
								<xsl:attribute name="pageSize">
									<xsl:value-of select="@pageSize"/>
								</xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>	
					</xsl:otherwise>
				</xsl:choose>				
			</xsl:element>
	</xsl:template>

	
	<xsl:template name="migrate-seedlist-services-not-activities" match="/tns:config/tns:seedlistSettings/tns:service[@name!='activities']">
		<seedlist xmlns="http://www.ibm.com/LotusConnectionsSearch">
			<xsl:attribute name="name">
				<xsl:value-of select="concat(@name,'SeedlistContentSource')"/>
			</xsl:attribute>
			<xsl:if test="string-length(@servletLocation)>0">
				<xsl:attribute name="seedlistLocation">
					<xsl:value-of select="@servletLocation"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length(@pageSize)>0">
				<xsl:attribute name="pageSize">
					<xsl:value-of select="@pageSize"/>
				</xsl:attribute>
			</xsl:if>		
		</seedlist>	
	</xsl:template>
	
	<xsl:template name="migrate-sand-tuning" match="tns:sandTuning">
		<sandTuning xmlns="http://www.ibm.com/LotusConnectionsSearch">
			<xsl:for-each select="tns:sandComponent">
				<xsl:variable name="sandComponentName" select="@name"/>
				<sandComponent>
					<xsl:choose>
						<xsl:when test="$sandComponentName='fastgraphbuilder'">
							<xsl:attribute name="name">graph</xsl:attribute>
						</xsl:when>
						<xsl:when test="$sandComponentName='tagsindexer'">
							<xsl:attribute name="name">tags</xsl:attribute>
						</xsl:when>							
					</xsl:choose>
					<xsl:if test="string-length(@iterations)>0">
							<xsl:attribute name="iterations">
								<xsl:value-of select="@iterations"/>
							</xsl:attribute>		
					</xsl:if>
				</sandComponent>
			</xsl:for-each>
		</sandTuning>	
	</xsl:template>
	
	
	<xsl:template name="migrate-attachment-handling-elements" match="tns:attachmentHandling">
		<attachmentHandling xmlns="http://www.ibm.com/LotusConnectionsSearch">
			<xsl:attribute name="enabled">
					<xsl:value-of select="@enabled" />
			</xsl:attribute>
			<xsl:attribute name="downloadFromSharedFS">true</xsl:attribute>
			<xsl:attribute name="maxAttachmentSize">
					<xsl:value-of select="@maxAttachmentSize"/>
			</xsl:attribute>
			<xsl:apply-templates select="tns:mimeSupport" />
			<xsl:copy-of select="tns:attachmentCache"/>
			<xsl:copy-of select="tns:attachmentConversion"/>
		</attachmentHandling>	
	</xsl:template>
	
	<xsl:template name="migrate-mime-support-elements" match="tns:mimeSupport">
		<xsl:copy>
			<xsl:apply-templates select="tns:mimeType" />	
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="migrate-mime-type-elements" match="tns:mimeType">
			<mimeType xmlns="http://www.ibm.com/LotusConnectionsSearch">
				<xsl:attribute name="name">
					<xsl:value-of select="@name" />
				</xsl:attribute>
				<xsl:attribute name="processor"/>
			</mimeType>																
	</xsl:template>
	
	
</xsl:stylesheet>
