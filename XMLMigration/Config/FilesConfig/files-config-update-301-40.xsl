<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2012  All Rights Reserved.                    -->
<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:tns="http://www.ibm.com/connections/files/files-config/1.0"
	exclude-result-prefixes="tns xsi"
	xsi:schemaLocation="http://www.ibm.com/connections/files/files-config/1.0 files-config.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/tns:config">
<xsl:comment>*****************************************************************</xsl:comment>
<xsl:comment>                                                                 </xsl:comment>
<xsl:comment>Licensed Materials - Property of IBM                             </xsl:comment>
<xsl:comment>                                                                 </xsl:comment>
<xsl:comment>5724-S68                                                         </xsl:comment>
<xsl:comment>                                                                 </xsl:comment>
<xsl:comment>Copyright IBM Corp. 2009, 2010  All Rights Reserved.             </xsl:comment>
<xsl:comment>                                                                 </xsl:comment>
<xsl:comment>US Government Users Restricted Rights - Use, duplication or      </xsl:comment>
<xsl:comment>disclosure restricted by GSA ADP Schedule Contract with          </xsl:comment>
<xsl:comment>IBM Corp.                                                        </xsl:comment>
<xsl:comment>                                                                 </xsl:comment>
<xsl:comment>*****************************************************************</xsl:comment>
<config id="files" xmlns="http://www.ibm.com/connections/files/files-config/1.0"
		xmlns:tns="http://www.ibm.com/connections/files/files-config/1.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.ibm.com/connections/files/files-config/1.0 files-config.xsd">


	<security reauthenticateAndSaveSupported="true">
		<logout href="{tns:security/tns:logout/@href}" />
		<inlineDownload enabled="false" />
	</security>

	<activeContentFilter enabled="{tns:activeContentFilter/@enabled}">
   		<mimeTypes>
    	<xsl:for-each select="tns:activeContentFilter/tns:mimeTypes/tns:mimeType">
			<mimeType><xsl:apply-templates/></mimeType>
    	</xsl:for-each>
   		</mimeTypes>
   	</activeContentFilter>	

	<cache>
		<user timeout="{tns:cache/tns:user/@timeout}" />
		<http publicContentMaxAgeInSecs="{tns:cache/tns:http/@publicContentMaxAgeInSecs}" publicFeedMaxAgeInSecs="{tns:cache/tns:http/@publicFeedMaxAgeInSecs}" />
	</cache>

	<db dialect="admin_replace" />

	<search>
		<seedlist maximumIncrementalQuerySpanInDays="{tns:search/tns:seedlist/@maximumIncrementalQuerySpanInDays}"
			maximumPageSize="{tns:search/tns:seedlist/@maximumPageSize}" />
	</search>

	<emailNotification>
		<addOnMediaDownload enabled="{tns:emailNotification/tns:addOnMediaDownload/@enabled}" />
	</emailNotification>
	
	<download>
		<modIBMLocalRedirect enabled="{tns:download/tns:modIBMLocalRedirect/@enabled}"
			hrefPathPrefix="{tns:download/tns:modIBMLocalRedirect/@hrefPathPrefix}" />
		<stats>
			<logging enabled="{tns:download/tns:stats/tns:logging/@enabled}" />
		</stats>
	</download>

	<file>
		<versioning enabled="{tns:file/tns:versioning/@enabled}" />
		<storage rootDirectory="{tns:file/tns:storage/@rootDirectory}" allowOperatingSystemVirusScan="false" />
		<page maximumSizeInKb="{tns:file/tns:page/@maximumSizeInKb}" />
		<attachment maximumSizeInKb="{tns:file/tns:attachment/@maximumSizeInKb}" />
		<media maximumSizeInKb="{tns:file/tns:media/@maximumSizeInKb}" />
		<restrictions enabled="{tns:file/tns:restrictions/@enabled}" mode="{tns:file/tns:restrictions/@mode}">
	   	  <extensions>
		   	  <extension></extension>
	   	  </extensions>
	   	</restrictions>
	   	<renditions enabled="true">
   			<!-- JPG or PNG supported -PNG is required for transparency support -->
   			<small format="JPG" width="100" height="100"/>
   			<medium format="JPG" width="250" height="250"/>
   			<large format="JPG" width="500" height="500"/>
	   	</renditions>
	</file>

	<directory>
		<typeaheadSearch maximumResults="{tns:directory/tns:typeaheadSearch/@maximumResults}" />
		
		<group>
			<membershipCache maximumAgeOnLoginInSeconds="{tns:directory/tns:group/tns:membershipCache/@maximumAgeOnLoginInSeconds}" maximumAgeOnRequestInSeconds="{tns:directory/tns:group/tns:membershipCache/@maximumAgeOnRequestInSeconds}" />
		</group>

		<community>
			<membershipCache maximumAgeOnLoginInSeconds="{tns:directory/tns:community/tns:membershipCache/@maximumAgeOnLoginInSeconds}" maximumAgeOnRequestInSeconds="{tns:directory/tns:community/tns:membershipCache/@maximumAgeOnRequestInSeconds}" />
		</community>
	</directory>
	
	<api>
	  <indent enabled="{tns:api/tns:indent/@enabled}" />
	</api>
	
	<publicMedia maximumResults="{tns:publicMedia/@maximumResults}" />
	
	<scheduledTasks>
		<task name="SearchClearDeletionHistory" interval="{tns:scheduledTasks/tns:task[@name='SearchClearDeletionHistory']/@interval}" enabled="{tns:scheduledTasks/tns:task[@name='SearchClearDeletionHistory']/@enabled}" type="{tns:scheduledTasks/tns:task[@name='SearchClearDeletionHistory']/@type}"></task>
		<task name="RenditionDailyGeneration" interval="0 0 0 * * ?" enabled="true" type="internal"></task>
		<task name="MetricsDailyCollection" interval="{tns:scheduledTasks/tns:task[@name='MetricsDailyCollection']/@interval}" enabled="{tns:scheduledTasks/tns:task[@name='MetricsDailyCollection']/@enabled}" type="{tns:scheduledTasks/tns:task[@name='MetricsDailyCollection']/@type}"></task>
		<task name="TagUpdateFrequency" interval="{tns:scheduledTasks/tns:task[@name='TagUpdateFrequency']/@interval}" enabled="{tns:scheduledTasks/tns:task[@name='TagUpdateFrequency']/@enabled}" type="{tns:scheduledTasks/tns:task[@name='TagUpdateFrequency']/@type}"></task>
		<task name="DirectoryGroupSynch" interval="{tns:scheduledTasks/tns:task[@name='DirectoryGroupSynch']/@interval}" enabled="{tns:scheduledTasks/tns:task[@name='DirectoryGroupSynch']/@enabled}" type="{tns:scheduledTasks/tns:task[@name='DirectoryGroupSynch']/@type}">
		   <args>
				<property name="maximumDataAgeInHours"><xsl:value-of select="tns:scheduledTasks/tns:task[@name='DirectoryGroupSynch']/tns:args/tns:property[@name='maximumDataAgeInHours']"/></property>
				<property name="pauseInMillis"><xsl:value-of select="tns:scheduledTasks/tns:task[@name='DirectoryGroupSynch']/tns:args/tns:property[@name='pauseInMillis']"/></property>
		   </args>
		</task>
		<task name="FileActuallyDelete" interval="{tns:scheduledTasks/tns:task[@name='FileActuallyDelete']/@interval}" enabled="{tns:scheduledTasks/tns:task[@name='FileActuallyDelete']/@enabled}" type="{tns:scheduledTasks/tns:task[@name='FileActuallyDelete']/@type}">
			<args>
                <property name="softDeleteMinimumPendingTimeInMins"><xsl:value-of select="tns:scheduledTasks/tns:task[@name='FileActuallyDelete']/tns:args/tns:property[@name='softDeleteMinimumPendingTimeInMins']"/></property>			
			</args>
		</task>
	</scheduledTasks>

</config>
	</xsl:template>
</xsl:stylesheet>
