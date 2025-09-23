<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2011, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xsi:noNamespaceSchemaLocation="notification-config.xsd">
	
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
		                                                                  
		*****************************************************************
		</xsl:comment>
		<config id="notification-config" version="4.0"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="notification-config.xsd" enabled="{@enabled}" buildlevel="LC4.0_20120605_1157">
			<xsl:call-template name="copy-properties"/>			
			<xsl:copy-of select="handlers" />
			<xsl:copy-of select="channelConfigs" />
			
			<xsl:call-template name="copy-defaultEmailPreferences"/>

			<templates>
				<xsl:comment>Example
				  &lt;source name="Activities" enabled="true" defaultFollowFrequency="DAILY"&gt;
					&lt;type name="notify"  notificationType="DIRECTED"&gt;
					  &lt;channel name="email" enabled="true"&gt;
						&lt;property name="sender"&gt;activities-admin@example.com&lt;/property&gt;
						&lt;property name="ftl"&gt;notify.ftl&lt;/property&gt;
					  &lt;/channel&gt;
					  &lt;channel name="event" enabled="true"&gt;
						&lt;property name="eventName"&gt;activities.notify&lt;/property&gt;
						&lt;property name="transformerClass"&gt;sample.ActivityNotifyTransformer&lt;/property&gt;
					  &lt;/channel&gt;
					&lt;/type&gt;
				  &lt;/source&gt;
				</xsl:comment>

				<xsl:for-each select="templates/source">
				    <xsl:choose>
				    	<!-- For Activities, transform to new format and may need to add 'invite' notification -->
						<xsl:when test="@name = 'Activities'">
							<xsl:call-template name="copy-source-activities"/>
						</xsl:when>
						<!-- For Blogs transform to new format and we have 3 new notifications -->
						<xsl:when test="@name = 'Blogs'">
							<xsl:call-template name="copy-source-blogs"/>
						</xsl:when>
						<!-- For Communities transform to new format -->
						<xsl:when test="@name = 'Communities'">
							<xsl:call-template name="copy-source-communities"/>
						</xsl:when>
						<!-- For Forums, transform to new format-->
						<xsl:when test="@name = 'Forums'">
							<xsl:call-template name="copy-source-forums"/>
						</xsl:when>	
						<!--  For News transform to new format and we have new notification to add -->
						<xsl:when test="@name = 'news'">
							<xsl:call-template name="copy-source-news"/>
						</xsl:when>
						<!--  For Bookmarks transform to new format -->
						<xsl:when test="@name = 'dogear'">
							<xsl:call-template name="copy-source-dogear"/>
						</xsl:when>						
						<!--  For Profiles transform to new format -->
						<xsl:when test="@name = 'Profiles'">
							<xsl:call-template name="copy-source-profiles"/>
						</xsl:when>
						<!--  For Wikis transform to new format -->
						<xsl:when test="@name = 'wikis'">
							<xsl:call-template name="copy-source-wikis"/>
						</xsl:when>
						<!--  For Files transform to new format -->
						<xsl:when test="@name = 'files'">
							<xsl:call-template name="copy-source-files"/>
						</xsl:when>
						<!--  For Moderation transform to new format -->
						<xsl:when test="@name = 'moderation'">
							<xsl:call-template name="copy-source-moderation"/>
						</xsl:when>												
					</xsl:choose>
				</xsl:for-each>
			</templates>
		</config>
	</xsl:template>
	
	<xsl:template name="copy-properties">
			<properties>
				<!--  Copy exiting preference defaults & their comments -->
				<xsl:for-each select="properties/*">
					<xsl:copy-of select="."/>
				</xsl:for-each>
				<xsl:comment> If true a link to Connections Mobile service will be included in 
			Notifications (where applicable)</xsl:comment>
				<property name="includeMobileLinksInNotifications">false</property>	
				<xsl:comment> If true no embedded experience mime parts will be included in notifications</xsl:comment>		
				<property name="disableEmbeddedAppsInNotifications">false</property>	
			</properties>
	</xsl:template>	
	
	<xsl:template name="copy-defaultEmailPreferences">
		<xsl:comment>Sets default email preferences for users who have not set their own
		     preferences.</xsl:comment>
			<defaultEmailPreferences>
				<!--  Copy exiting preference defaults & their comments -->
				<xsl:for-each select="defaultEmailPreferences/*">
					<xsl:copy-of select="preceding-sibling::comment()[1]"/>
					<xsl:copy-of select="."/>
				</xsl:for-each>
				
				<!-- New preference default & comment 
				     Note: Indentation for <xsl:comment> affects output. -->
				<xsl:comment> The default for whether replyTo is enabled. This defines if user
			 will have ability to reply to supported notifications to respond
			 (e.g. replying to a forum response notification to add a new response</xsl:comment>
				<replyToEnabled>true</replyToEnabled>					
				<xsl:comment> The default amount of stories to be included in every category
			in the Daily and Weekly Email Digest. Min is 5, Max is 25</xsl:comment>
				<digestItemsPerCategory>10</digestItemsPerCategory>				
								
			</defaultEmailPreferences>
	</xsl:template>
	
	<!-- ################################################ ACTIVIES SECTION ################################################ -->
	
	<!--  For Activities source, the Activity Invite notification may or may not exist -->
	<xsl:template name="copy-source-activities" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">
	
			<!-- Check to see if Activity Invite notification already exists -->
			<xsl:variable name="activityInviteExists" select="type[@name='invite']" />
	
			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-activities" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>
	
			<!-- Add the 'invite' notification if it does not already exist -->
			<xsl:choose>
				<xsl:when test="not($activityInviteExists)">
					<type name="invite" notificationType="DIRECTED">
 						<channel name="email" enabled="true">
							<property name="sender">activities-admin@example.com</property>
							<property name="ftl">inviteMemberMail.ftl</property>
						</channel>
						<channel name="event" enabled="true">
							<property name="eventName">activities.notification.invitemember</property>
							<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.ActivitiesNotificationEventTransformer</property>
						</channel>
					</type>
				</xsl:when>
			</xsl:choose>
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-activities">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'notify'">
					<property name="ftl">notifyMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'autocomplete'">
					<property name="ftl">autoCompleteActivityMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'create'">
					<property name="ftl">createMail.ftl</property>
				</xsl:when>		
				<xsl:when test="@name = 'addmember'">
					<property name="ftl">addMemberMail.ftl</property>
				</xsl:when>						
			</xsl:choose>
		</channel>
	</xsl:template>
	
	
	<!-- ################################################ BLOGS SECTION ################################################ -->
	
	<xsl:template name="copy-source-blogs" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">

			<!--  Blogs templates have been consolidated simply write out new types -->
			<type name="notify" notificationType="DIRECTED">
				<channel name="email" enabled="true">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notify.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.notify</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notify-idea" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notify.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">ideationblog.notification.notify-idea</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="ownermsg" notificationType="RESPONSE">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">ownermsg.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.ownermsg</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyreview" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyreview.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.notifyreview</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="approved" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">approved.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.approved</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="rejected" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">rejected.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.rejected</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyflagged" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyflagged.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.notifyflagged</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="confirmflagged" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">confirmflagged.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.confirmflagged</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="quarantined" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">quarantined.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.quarantined</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="returned" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">returned.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.returned</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyreposted" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyreposted.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">blogs.notification.notifyreposted</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="restored" notificationType="MODERATION">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">restored.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">blogs.notification.restored</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyassigned-owner" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyassigned.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">blogs.notification.notifyassigned-owner</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyassigned-author" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyassigned.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">blogs.notification.notifyassigned-author</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
			<type name="notifyassigned-draft" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">blogs-admin@example.com</property>
					<property name="ftl">notifyassigned.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">blogs.notification.notifyassigned-draft</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.BlogsNotificationEventTransformer</property>
				</channel>
			</type>
		</source>
	</xsl:template>	
	
	
	<!-- ################################################ COMMUNITIES SECTION ################################################ -->
	
	<xsl:template name="copy-source-communities" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-communities" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>
			
			<!-- Add the new notifications -->
			<type name="notifyEvent" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">communities-admin@example.com</property>
					<property name="ftl">notifyEvent.ftl</property>
				</channel>
				<channel enabled="true" name="event">
					<property name="eventName">communities.notification.notifyEvent</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.CommunitiesNotificationEventTransformer</property>
				</channel>
			</type>
			
		</source>
	</xsl:template>	
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-communities">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'broadcastMail'">
					<property name="ftl">broadcastMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'memberAdded'">
					<property name="ftl">memberAddedMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'memberRemoved'">
					<property name="ftl">memberRemovedMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'requestToJoin'">
					<property name="ftl">requestToJoinMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'invite'">
					<property name="ftl">invitedToJoinMail.ftl</property>
				</xsl:when>		
			</xsl:choose>
		</channel>
	</xsl:template>
	
	<!-- ################################################ FORUMS SECTION ################################################ -->
	
	<!-- Forums only include the source tag, transform from old format to this, leave out templates -->
	<xsl:template name="copy-source-forums" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}" defaultFollowFrequency="{@defaultFollowFrequency}" />
	</xsl:template>	
	
	<!-- ################################################ NEWS SECTION ################################################ -->
	
	<xsl:template name="copy-source-news" match="source">
		<!-- Insert the source tag, and it's attributes, news does not need defaultFollowFrequency -->
		<source name="{@name}" enabled="{@enabled}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-news" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>

			<!--  Add the new notification for ReplyTo errors -->
			<type name="replyToError" notificationType="DIRECTED">
				<channel enabled="true" name="email">
					<property name="sender">news-admin@example.com</property>
					<property name="ftl">replyToError.ftl</property>
				</channel>
			</type>	
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-news">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'followIndividual'">
					<property name="ftl">followIndividual.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'weeklyDigest'">
					<property name="ftl">weeklyDigest.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'dailyDigest'">
					<property name="ftl">dailyDigest.ftl</property>
				</xsl:when>						
			</xsl:choose>
		</channel>
	</xsl:template>
	
	<!-- ################################################ DOGEAR SECTION ################################################ -->
	
		<xsl:template name="copy-source-dogear" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="WEEKLY">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-dogear" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>		
			<type name="brokenurl" notificationType="DIRECTED">
				<channel name="email" enabled="true">
					<property name="sender">dogear-admin@example.com</property>
					<property name="ftl">notifytemplate.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">dogear.notification.brokenurl</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.DogearNotificationEventTransformer</property>
				</channel>
			</type>		
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-dogear">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'notify'">
					<property name="ftl">notifytemplate.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'notifyReplaceURL'">
					<property name="ftl">replaceurltemplate.ftl</property>
				</xsl:when>			
			</xsl:choose>
		</channel>
	</xsl:template>
	
	
	<!-- ################################################ PROFILES SECTION ################################################ -->
	
		<xsl:template name="copy-source-profiles" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-profiles" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>				
			</xsl:for-each>		
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-profiles">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'notify'">
					<property name="ftl">inviteColleagueMail.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'notifyBoardOwnerForEntry'">
					<property name="ftl">notifyBoardOwnerForEntry.ftl</property>
				</xsl:when>	
				<xsl:when test="@name = 'notifyBoardOwnerForComment'">
					<property name="ftl">notifyBoardOwnerForComment.ftl</property>
				</xsl:when>	
				<xsl:when test="@name = 'notifyEntryOwnerForComment'">
					<property name="ftl">notifyEntryOwnerForComment.ftl</property>
				</xsl:when>			
			</xsl:choose>
		</channel>
	</xsl:template>
	
	
	<!-- ################################################ WIKIS SECTION ################################################ -->
	
		<xsl:template name="copy-source-wikis" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-wikis" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>		
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-wikis" >
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'mediaEdit'">
					<property name="ftl">mediaUpdated.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'commentAdd'">
					<property name="ftl">commentAdded.ftl</property>
				</xsl:when>	
				<xsl:when test="@name = 'roleAdd'">
					<property name="ftl">libraryMemberUpdated.ftl</property>
				</xsl:when>		
			</xsl:choose>
		</channel>
	</xsl:template>
	
	<!-- ################################################ FILES SECTION ################################################ -->
	
		<xsl:template name="copy-source-files" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}"
			defaultFollowFrequency="{@defaultFollowFrequency}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-files" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>		
			
			<!-- Add new 'singleDownload' template -->
			<type name="singleDownload" notificationType="DIRECTED">
				<channel name="email" enabled="true">
					<property name="sender">files-admin@example.com</property>
					<property name="ftl">singleDownload.ftl</property>
				</channel>
				<channel name="event" enabled="true">
					<property name="eventName">files.notification.singleDownload</property>
					<property name="transformerClass">com.ibm.lotus.connections.core.notify.channels.event.FilesNotificationEventTransformer</property>
				</channel>
			</type>
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-files" >
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'mediaShare'">
					<property name="ftl">mediaShared.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'mediaEdit'">
					<property name="ftl">mediaUpdated.ftl</property>
				</xsl:when>	
				<xsl:when test="@name = 'commentAdd'">
					<property name="ftl">commentAdded.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'collectionMemberUpdate'">
					<property name="ftl">collectionMemberUpdated.ftl</property>
				</xsl:when>		
				<xsl:when test="@name = 'communityVisibilityUpdate'">
					<property name="ftl">communityVisibilityUpdated.ftl</property>
				</xsl:when>		
				<xsl:when test="@name = 'collectionMediaAdd'">
					<property name="ftl">collectionmediaadded.ftl</property>
				</xsl:when>				
			</xsl:choose>
		</channel>
	</xsl:template>
	
	<!-- ################################################ MODERATION SECTION ################################################ -->
	
		<xsl:template name="copy-source-moderation" match="source">
		<!-- Insert the source tag, and it's attributes -->
		<source name="{@name}" enabled="{@enabled}">

			<!-- Copy all existing types -->
			<xsl:for-each select="type">
				<type name="{@name}" notificationType="{@notificationType}">				
					<xsl:call-template name="copy-emailChannel-moderation" />
					<xsl:copy-of select="channel[@name='event']"/>
				</type>
			</xsl:for-each>		
		</source>
	</xsl:template>
	
	<!-- transform the channels to new format -->
	<xsl:template name="copy-emailChannel-moderation">
		<channel name="email" enabled="{channel[@name='email']/@enabled}">
			<xsl:copy-of select="channel[@name='email']/property[@name='sender']"/>
			<xsl:choose>
				<xsl:when test="@name = 'notifyReview'">
					<property name="ftl">notifyreview.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'notifyFlagged'">
					<property name="ftl">notifyflagged.ftl</property>
				</xsl:when>	
				<xsl:when test="@name = 'notifyPending'">
					<property name="ftl">notifypending.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'notifyApproved'">
					<property name="ftl">notifyapproved.ftl</property>
				</xsl:when>		
				<xsl:when test="@name = 'notifyRejected'">
					<property name="ftl">notifyrejected.ftl</property>
				</xsl:when>
				<xsl:when test="@name = 'notifyQuarantined'">
					<property name="ftl">notifyquarantined.ftl</property>
				</xsl:when>				
				<xsl:when test="@name = 'notifyRestored'">
					<property name="ftl">notifyrestored.ftl</property>
				</xsl:when>				
				<xsl:when test="@name = 'notifyDeleted'">
					<property name="ftl">notifydeleted.ftl</property>
				</xsl:when>				
				<xsl:when test="@name = 'notifyAutoQuarantined'">
					<property name="ftl">notifyautoquarantined.ftl</property>
				</xsl:when>				
								
			</xsl:choose>
		</channel>
	</xsl:template>
	
</xsl:stylesheet>
