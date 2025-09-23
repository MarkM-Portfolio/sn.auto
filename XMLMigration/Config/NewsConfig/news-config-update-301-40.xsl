<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2011, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="news-config.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/config">
		<xsl:comment>
*****************************************************************
                                                                  
 Licensed Materials - Property of IBM                             
                                                                  
 5724-S68                                                         
                                                                  
 Copyright IBM Corp. 2008, 2011  All Rights Reserved.                   
                                                                  
 US Government Users Restricted Rights - Use, duplication or      
 disclosure restricted by GSA ADP Schedule Contract with          
 IBM Corp.                                                        
                                                                  
*****************************************************************</xsl:comment>
		<xsl:text>
		</xsl:text>
			
		<config id="news" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="news-config.xsd" version="4.0">
			
			<xsl:call-template name="copyScheduledTasks"/>
			
			<xsl:call-template name="copyDatabaseCleanup"/>
			
			<xsl:call-template name="copyDataSynchronization"/>
			
			<xsl:comment>
	These settings control the Mail-In feature, which provides support
	for replying to certain notifications to add responses / comments.</xsl:comment>

			<mailin enabled="false">
			    <replyto enabled="false">
				    <xsl:comment>
			A special ReplyTo address is added to notifications where
			the user can reply to the notification to respond/comment.
			The domain may be a dedicated domain for connections bound
			mails. Or it could be existing domain, in which case a prefix
			of suffix should be provided also.</xsl:comment>
					<replytoAddressFormat>
					    <domain>connections.example.com</domain>
					    
					    <xsl:comment>
				A prefix OR suffix (not both) may also be provided. 
				This is necessary if an existing domain (with other 
				email addresses) is being used.
				There is a 28 character limit for the affix.</xsl:comment>
					    <xsl:comment>
		    	&lt;affix type="suffix"&gt;_lcreplyto&lt;/affix&gt;
		    	&lt;affix type="prefix"&gt;lcreplyto_&lt;/affix&gt;</xsl:comment>
						</replytoAddressFormat>
				</replyto>
			</mailin>

			<xsl:comment>Support vulcan deployment</xsl:comment>
			<vulcan enabled="false"/>

			<xsl:comment>Support use of the Dynacache</xsl:comment>
			<dynacache enabled="true"/>

			<xsl:call-template name="copyMicrobloggingSettings"/>

		</config>
	</xsl:template>
			
	<xsl:template name="copyScheduledTasks" match="scheduledTasks">
		<scheduledTasks>
			<!--  Copy existing preference defaults & their comments -->
			<xsl:for-each select="scheduledTasks/node()">
				<xsl:copy-of select="."/>
			</xsl:for-each>
			
			<!--  New task for MailIn -->
			<xsl:text>	</xsl:text><xsl:comment> 
		A task is performed periodically to poll the replyto mailbox for unread mails. </xsl:comment>
			<xsl:text>&#xa;</xsl:text>			
            <xsl:text>    	</xsl:text><task serverName="unsupported"
				  startby="" 
				  mbeanMethodName=""
				  targetName="ScheduledTaskService"
				  type="internal"
				  scope="cluster"
				  enabled="true"
				  interval="0 0/5 * * * ?"
				  description="Job to retrieve mail for replyto feature"
				  name="ReplyToMailRetrieval"  >
			</task>
			
			<xsl:comment> 
		This task run periodically to purge the system of expired ReplyTo Id records </xsl:comment>			
            <task serverName="unsupported"
				startby=""
				mbeanMethodName=""
				targetName="ScheduledTaskService"
				type="internal"
				scope="cluster"
				enabled="true"
				interval="0 0 4 ? * SAT"
				description="Job to cleanup Expired ReplyTo Id records"
				name="ReplyToIdCleanup" >
			</task>	
			
			<xsl:comment> 
		This task runs periodically to remove any replyTo attachments that were not properly removed from the shared data store </xsl:comment>
			<task 	serverName="unsupported"
				startby=""
				mbeanMethodName=""
				targetName="ScheduledTaskService"
				type="internal"
				scope="cluster"
				enabled="true"
				interval="0 0 4 ? * SUN"
				description="Job to cleanup Expired ReplyTo Attachment Files"
				name="ReplyToAttachmentCleanup" >
			</task>					
		</scheduledTasks>
	</xsl:template>

	<xsl:template name="copyDatabaseCleanup" match="databaseCleanup">	 	
		<!--  Copy existing value & move comment for story cleanup inside databaseCleanup -->
		<databaseCleanup>
			<xsl:copy-of select="databaseCleanup/preceding-sibling::comment()[1]"/>
			<xsl:for-each select="databaseCleanup/node()">			
				<xsl:copy-of select="."/>
			</xsl:for-each>
			
			<xsl:text>	</xsl:text><xsl:comment> 
		Notifications eligible for a mailed in reply are periodically 
		removed from the database. The following setting defines the number of days 
		from date of notification sent before that a mailed in reply is no longer permitted. </xsl:comment>	
			<xsl:text>&#xa;</xsl:text>		
			<xsl:text>		</xsl:text><replyToIdLifetimeInDays>365</replyToIdLifetimeInDays>
			
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>		</xsl:text><xsl:comment> 
		The number of days before the the system will remove any replyTo attachments 
		that were not properly removed from the shared data store. </xsl:comment>	
			<xsl:text>&#xa;</xsl:text>		
			<xsl:text>		</xsl:text><replyToAttachmentLifetimeInDays>7</replyToAttachmentLifetimeInDays>	
		</databaseCleanup>
	</xsl:template>
	
	<xsl:template name="copyDataSynchronization" match="dataSynchronization">
		<xsl:copy-of select="dataSynchronization/preceding-sibling::comment()[1]"/>
		<xsl:copy-of select="dataSynchronization/."/>
	</xsl:template>

	<xsl:template name="copyMicrobloggingSettings" match="microblogging-settings">
	    <xsl:comment>microblogging settings</xsl:comment>
		<microblogging-settings>
	    	<xsl:comment>Maximum number of characters (default 1000) allowed in a microblog entry</xsl:comment>
	    	<microblogEntryMaxChars>1000</microblogEntryMaxChars>    
	    	<xsl:comment>Maximum number of characters (default 1000) allowed in a microblog comment</xsl:comment>
	    	<microblogCommentMaxChars>1000</microblogCommentMaxChars>   
		</microblogging-settings>
	</xsl:template>
		
</xsl:stylesheet>
