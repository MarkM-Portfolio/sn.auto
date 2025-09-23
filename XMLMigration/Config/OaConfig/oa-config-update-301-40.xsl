<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2011, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xsi:noNamespaceSchemaLocation="oa-config.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/config">
		<xsl:comment>
			*****************************************************************

			Licensed Materials - Property of IBM

			5724-L21


			Copyright IBM Corp. 2006, 2011 All Rights Reserved.

			US Government Users Restricted Rights - Use, duplication or
			disclosure restricted by GSA ADP Schedule Contract with
			IBM Corp.

			*****************************************************************
		</xsl:comment>
		<config version="29" id="oa-config"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="oa-config.xsd">
			<xsl:comment>
				Activities Configuration
			</xsl:comment>

			<xsl:comment>
				The Event Broker is used to control which events cause notifications
				to be sent to users.
				Activities email notification is controlled by settings in the common
				notification-config.xml file.
			</xsl:comment>
			<eventBroker>
				<xsl:comment>
					Maintain this XML formatting for the subscriber element so it can
					be handled by the installer
					This provider sends email notifications to users
				</xsl:comment>
				<subscriber class="com.ibm.openactivities.notifications.email.ConnectionsEmailNotifier">
					<events>
						<xsl:copy-of select="eventBroker/subscriber/events/node()" />
						<event>com.ibm.openactivities.events.NewInviteEvent</event>
					</events>
				</subscriber>
				<xsl:comment>
					This provider sends Audit / News / Notification events to the
					HomePage when data changing events occur
				</xsl:comment>
				<subscriber class="com.ibm.openactivities.internal.service.core.AuditEventDispatcher">
					<events>
						<event>com.ibm.openactivities.events.ActivitiesPublishedEvent</event>
					</events>
				</subscriber>
			</eventBroker>

			<xsl:comment>
				The Object Store is used to store activity data objects such as
				attachments and large messages.
				Multiple object stores can be specified, but only one can be marked as
				"default". The default
				store is used for all new content. The "id" element must be a unique
				name, it does not need to specify
				a specific type or be a keyword like "filesystem".
			</xsl:comment>
			<xsl:copy-of select="objectStore" />

			<xsl:copy-of select="management" />

			<xsl:copy-of select="stats" />

			<xsl:comment>
				Define content that will be removed from activity data
			</xsl:comment>
			<xsl:copy-of select="activeContentFilter" />

			<xsl:copy-of select="PublishFile" />

			<scheduledTasks>
				<xsl:copy-of select="scheduledTasks/node()" />

				<xsl:comment>
					SyncGroupNames - every day @ 12 am
				</xsl:comment>
				<task name="SyncGroupNames" description="Activities Group Name Synchronization Service"
					interval="0 0 0 * * ?" startby="" enabled="true" scope="cluster"
					type="class" targetName="com.ibm.openactivities.jobs.SyncGroupNamesWS"
					mbeanMethodName="" serverName="unsupported">
				</task>

				<xsl:comment>
					DatabaseMaintenance - every day @ 1 AM
				</xsl:comment>
				<task name="DatabaseMaintenance" description="Activities Database Maintenance Service"
					interval="0 0 1 * * ?" startby=""  enabled="true" scope="cluster"
					type="class" targetName="com.ibm.openactivities.jobs.DatabaseMaintenanceWS"
					mbeanMethodName="" serverName="unsupported">
				</task>

			</scheduledTasks>

			<xsl:comment>
				Set noStore to true to make all private feeds have a cache-control:
				no-store header.
			</xsl:comment>
			<Cache noStore="false" />

			<xsl:comment>
				Autosave interval is in minute, default is 5 minute
			</xsl:comment>
			<Autosave interval="5" />

		</config>
	</xsl:template>
</xsl:stylesheet>
