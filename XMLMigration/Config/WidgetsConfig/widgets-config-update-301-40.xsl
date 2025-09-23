<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2011, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tns="http://www.ibm.com/widgets-config"
    xmlns="http://www.ibm.com/widgets-config"
    xsi:schemaLocation="http://www.ibm.com/widgets-config widgets-config.xsd">
    
	<xsl:output method="xml" omit-xml-declaration="no" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- Take the copyright from the xml file in this case. -->
	<xsl:template match="/tns:config" priority="1.0">

	<config id="widgets"
		xmlns="http://www.ibm.com/widgets-config"
		xmlns:tns="http://www.ibm.com/widgets-config"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.ibm.com/widgets-config widgets-config.xsd">

		<xsl:comment> 
		    Example widgets: not supported officially but can be used as examples.
		    
		    &lt;widgetDef defId="AdditionalInfo" modes="view edit" showInPalette="false" url="{webresourcesSvcRef}/web/lconn.comm/widgets/additionalInfo/additional-info.xml?version={version}" /&gt;
		    
		    &lt;widgetDef defId="FeedReader" modes="view edit" url="{webresourcesSvcRef}/web/lconn.comm/widgets/feedReader/feedreader.xml?version={version}" description="feedReaderDescription"/&gt;
		    
		    // a widget with a fullpage mode
		    &lt;widgetDef defId="HelloWorld" primaryWidget="false" modes="view fullpage edit search"  url="{webresourcesSvcRef}/web/lconn.comm/widgets/helloWorld/HelloWorld.xml?version={version}"/&gt;
		    
		    // a widget with an external jsp as a full page
		    &lt;widgetDef defId="HelloWorld" primaryWidget="false" modes="view edit search"  url="{webresourcesSvcRef}/web/lconn.comm/widgets/helloWorld/HelloWorld.xml?version={version}"
		        navBarLink="{communitiesSvcRef}/comm.widgets/helloWorld/HelloWorld.jsp?resourceId={resourceId}&amp;version={version}"/&gt;
		</xsl:comment>

		<xsl:apply-templates />
		
		<!-- Add sharebox configuration element -->
		<resource type="share">
			<widgets xmlns:tns="http://www.ibm.com/widgets-config">
				<definitions>
					<widgetDef defId="microbloggingWidget"
						url="{{webresourcesSvcRef}}/web/lconn.news.microblogging.sharebox/globalMicrobloggingForm.xml" inline="true"></widgetDef>
					<widgetDef defId="uploadFile" url="{{webresourcesSvcRef}}/web/com.ibm.social.sharebox/UploadFile.xml"></widgetDef>
				</definitions>
			</widgets>
		</resource> 
	<resource type="communityShare">
		<widgets xmlns:tns="http://www.ibm.com/widgets-config">
			<definitions>
				<widgetDef defId="commUploadFile" url="{{webresourcesSvcRef}}/web/com.ibm.social.sharebox/CommunityUploadFile.xml"></widgetDef>
			</definitions>
		</widgets>
	</resource>
		
	</config>
	</xsl:template>
	
	<!-- Remove the previous example comment -->
	<xsl:template match="/tns:config/comment()[contains(., 'not supported officially')]" priority="2.0"></xsl:template>
	
<!-- communities 01 start -->
	<xsl:template match="//tns:widgetDef/@url" priority="2.0">
	<xsl:choose>

	<xsl:when test="contains(. ,'{contextRoot}/widgets/widgets/')">
		<xsl:attribute name="url">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text">
					<xsl:call-template name="string-replace-all">
						<xsl:with-param name="text" select="." />
						<xsl:with-param name="replace" select="'version='" />
						<xsl:with-param name="with" select="'etag='" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="replace" select="'{contextRoot}/widgets/widgets/'" />
				<xsl:with-param name="with" select="'{webresourcesSvcRef}/web/lconn.comm/widgets/'" />
			</xsl:call-template>
		</xsl:attribute>

	</xsl:when>

	<xsl:when test="contains(. ,'{contextRoot}/activity/widgets/ActivityList/')">
		<xsl:attribute name="url">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text">
					<xsl:call-template name="string-replace-all">
						<xsl:with-param name="text" select="." />
						<xsl:with-param name="replace" select="'version='" />
						<xsl:with-param name="with" select="'etag='" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="replace" select="'{contextRoot}/activity/widgets/ActivityList/'" />
				<xsl:with-param name="with" select="'{webresourcesSvcRef}/web/lconn.communityactivities/'" />
			</xsl:call-template>
		</xsl:attribute>
					</xsl:when>

	<xsl:otherwise>
		<xsl:copy />
	</xsl:otherwise>

	</xsl:choose>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='CustomLibrary']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/quickr.lw/widgetDefs/LibraryWidget_QCS_Connections.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>

			<!--xsl:apply-templates /-->
        <itemSet>
            <item name="proxyUrl" value="{{contextRoot}}/commonProxy"/>
        </itemSet>
		</xsl:copy>
	</xsl:template>
<!-- communities 01 end -->

<!-- profiles 01 start -->
	<xsl:template match="//tns:widgetDef[@defId='backgroundInfo']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{contextRoot}/widget-catalog/profile-details.xml?version={version}</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>

			<!--xsl:apply-templates /-->
        <itemSet>
            <item name="section" value="associatedInformation"/>
        </itemSet>
		</xsl:copy>
            <widgetDef   helpLink="{{helpSvcRef}}/topic/com.ibm.lotus.connections.profiles.help/c_pers_profiles.html" modes="view fullpage" 
                 url="{{webresourcesSvcRef}}/web/com.ibm.social.as.lconn/widgets/profilesActivityStream.xml?version={{version}}" defId="Updates" />
	</xsl:template>
<!-- profiles 01 end -->
	
<!-- profiles 02 start -->
	<!--remove the Recent Posts tab since it is replaced by the Activity Stream. -->
	<xsl:template match="//tns:page[@pageId='profilesView']" priority="2.0">
<page pageId="profilesView">

<xsl:for-each select="tns:widgetInstance">
	<xsl:choose>

	<xsl:when test="@defIdRef='multiFeedReader'">
		<xsl:comment> 
			&lt;widgetInstance uiLocation="tabsWidget1" defIdRef="multiFeedReader"&gt;&lt;/widgetInstance&gt;
		</xsl:comment>
	</xsl:when>

	<xsl:when test="@defIdRef='board'">
	<widgetInstance>
		<xsl:attribute name="defIdRef">Updates</xsl:attribute>
		<xsl:attribute name="uiLocation"><xsl:value-of select="@uiLocation"/></xsl:attribute>
	</widgetInstance>
	</xsl:when>

	<xsl:otherwise>

	<xsl:copy-of select="." />

	</xsl:otherwise>

	</xsl:choose>

</xsl:for-each>

</page>
	</xsl:template>	

	<xsl:template match="//tns:widgetDef[@defId='contactInfo']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{contextRoot}/widget-catalog/profile-details.xml?version={version}</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>

        <itemSet>
            <item name="section" value="contactInformation"/>
        </itemSet>
		</xsl:copy>
	</xsl:template>
<!-- profiles 02 end -->

<!-- communities 03 start -->



	<!-- Add event type to Bookmarks -->
	<xsl:template match="//tns:widgetDef[@defId='Bookmarks']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.comm/widgets/feedReader/feedreader.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Bookmarks16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='Bookmarks']/tns:itemSet" priority="2.0">
		<xsl:copy>
			<xsl:apply-templates />
			<item value="bookmarks" name="widgetType" />
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Bookmarks -->
	<xsl:template match="//tns:widgetDef[@defId='Bookmarks']/tns:itemSet/tns:item[@name='feedUrl']" priority="2.0">
            <item name="feedUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/bookmarks?communityUuid={{resourceId}}&amp;ps=5" />
	</xsl:template>
	<xsl:template match="//tns:widgetDef[@defId='Bookmarks']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
            <item name="atomPubUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/bookmarks/service?communityUuid={{resourceId}}" />
	</xsl:template>
	<xsl:template match="//tns:widgetDef[@defId='Bookmarks']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
            <item name="searchUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/bookmarks?communityUuid={{resourceId}}&amp;search={{searchTerm}}" />
	</xsl:template>



	<!-- Add event type to Feeds -->
	<xsl:template match="//tns:widgetDef[@defId='Feeds']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.comm/widgets/feedReader/feedreader.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Other/Feed16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='Feeds']/tns:itemSet" priority="2.0">
		<xsl:copy>
			<xsl:apply-templates />
			<item value="Feeds" name="widgetType" />
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Feeds -->
	<xsl:template match="//tns:widgetDef[@defId='Feeds']/tns:itemSet/tns:item[@name='feedUrl']" priority="2.0">
            <item name="feedUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/feeds?communityUuid={{resourceId}}&amp;ps=5" />
	</xsl:template>
	<xsl:template match="//tns:widgetDef[@defId='Feeds']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
            <item name="atomPubUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/feeds/service?communityUuid={{resourceId}}" />
	</xsl:template>
	<xsl:template match="//tns:widgetDef[@defId='Feeds']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
            <item name="searchUrl" value="{{communitiesSvcRef}}/service/atom{{authSubpath}}/community/feeds?communityUuid={{resourceId}}&amp;search={{searchTerm}}" />
	</xsl:template>



	<!-- Add the widgetType item to the Feeds and Bookmarks widgets -->
	<xsl:template match="//tns:widgetDef[@defId='Feeds']/tns:itemSet" priority="2.0">
		<xsl:copy>
			<xsl:apply-templates />
			<item value="feeds" name="widgetType" />
		</xsl:copy>
	</xsl:template>



<!-- Update the feed URLs for the feed reader widgets
	<xsl:template match="//tns:widgetDef[@defId='Bookmarks' or @defId='Feeds' or @defId='FeedReader']/tns:itemSet/tns:item[contains(@value,'{communitiesSvcRef}/service/atom/community')]/@value" priority="2.0">
		<xsl:attribute name="value">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text" select="." />
				<xsl:with-param name="replace" select="'{communitiesSvcRef}/service/atom/community'" />
				<xsl:with-param name="with" select="'{communitiesSvcRef}/service/atom/forms/community'" />
			</xsl:call-template>
		</xsl:attribute>
	</xsl:template>
-->
<!-- communities 03 end -->

<!-- profiles 03 start -->
	<!-- The SaND widgets are now located in the web resources WAR -->
	<xsl:template match="//tns:widgetDef[@prerequisite='sand' and contains(@url, '{sandSvcRef}')]/@url" priority="2.0">
		<xsl:attribute name="url">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text" select="." />
				<xsl:with-param name="replace" select="'{sandSvcRef}/static/{version}/'" />
				<xsl:with-param name="with" select="'{webresourcesSvcRef}/web/lconn.sand/'" />
			</xsl:call-template>
			<!-- Append the etag stamp to the end of the URL -->
			<xsl:text>?etag={version}</xsl:text>
		</xsl:attribute>
	</xsl:template>
<!-- profiles 03 end -->

<!-- both 01 start -->
	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='sandUIRoot']/@value" priority="2.0">
		<xsl:attribute name="value">{webresourcesSvcRef}/web/lconn.sand/</xsl:attribute>
	</xsl:template>

	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='ApiUrl']/@value" priority="2.0">
		<xsl:attribute name="value">{searchSvcRef}/atom/social/recommend</xsl:attribute>
	</xsl:template>
<!-- both 01 end -->

<!-- profiles 04 start -->
	<!-- Update URL to forums for recent posts widget -->
	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='forumsFeedUrl']/@value" priority="2.0">
		<xsl:attribute name="value">{searchSvcRef}/atom/search/results?component=forums&amp;person={userid}&amp;sortkey=date&amp;sortorder=desc&amp;page=1&amp;ps=5</xsl:attribute>
	</xsl:template>
<!-- profiles 04 end -->
	
<!-- communities 04 start -->
	<!-- Update modes for Forums widget -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']/@modes" priority="2.0">
		<xsl:attribute name="modes">view edit fullpage search</xsl:attribute>
	</xsl:template>
	
	<!-- Update URL to topicFeedPath for Forums widget -->
	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='topicFeedPath']/@value" priority="2.0">
		<xsl:attribute name="value">{forumsSvcRef}/atom/forms/topics?overview=true&amp;communityUuid={resourceId}</xsl:attribute>
	</xsl:template>
<!-- communities 04 end -->
	
<!-- profiles 05 start -->
	<!-- Update URL to bookmarks for recent posts widget -->
	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='dogearFeedUrl']/@value" priority="2.0">
		<xsl:attribute name="value">{dogearSvcRef}/atom?userid={userid}&amp;access=public&amp;sort=date&amp;sortOrder=desc&amp;ps=5&amp;showFavIcon=true{appLangParam}</xsl:attribute>
	</xsl:template>
<!-- profiles 05 end -->

<!-- communities 05 start -->
	<!-- Update widgetDef fullpagePageSize [defId="Forum"] -->
	<xsl:template match="//tns:widgetDef/tns:itemSet/tns:item[@name='fullpagePageSize']/@value" priority="2.0">
		<xsl:attribute name="value">25</xsl:attribute>
	</xsl:template>
<!-- communities 05 start -->

<!-- profiles 06 start -->
	<!-- Update widgetDef defId="commonTags" helpLink -->
	<xsl:template match="//tns:widgetDef[@defId='commonTags']/@helpLink" priority="2.0">
		<xsl:attribute name="helpLink">{helpSvcRef}/topic/com.ibm.lotus.connections.profiles.help/t_pers_using_org_tag_widget.html</xsl:attribute>
	</xsl:template>
<!-- profiles 06 start -->

<!-- communities 06 start -->
	<!-- Update widgetDef defId="linkRoll" helpLink -->
	<xsl:template match="//tns:widgetDef[@defId='linkRoll']/@helpLink" priority="2.0">
		<xsl:attribute name="helpLink">{helpSvcRef}/topic/com.ibm.lotus.connections.profiles.help/t_add_external_links.html</xsl:attribute>
	</xsl:template>

	<!-- Update widgetDef defId="socialTags" helpLink -->
	<xsl:template match="//tns:widgetDef[@defId='socialTags']/@helpLink" priority="2.0">
		<xsl:attribute name="helpLink">{helpSvcRef}/topic/com.ibm.lotus.connections.profiles.help/t_pers_tag_profiles.html</xsl:attribute>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='Members']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.comm/communityMembers/communityMembers.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>

                <itemSet>
                    <item name="membersPerPage" value="12" />
                    <item name="membersPerPageFullPage" value="16" />
                </itemSet>
		</xsl:copy>
	</xsl:template>


	<xsl:template match="//tns:widgetDef[@defId='Blog']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{blogsSvcRef}/static/{version}!{locale}/iwidgets/blog/blogsWidget.jsp</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Blogs16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>

	</xsl:template>

	<!-- finish up itemSet for Blog -->
	<xsl:template match="//tns:widgetDef[@defId='Blog']/tns:itemSet" priority="2.0">
		<xsl:copy>
			<xsl:apply-templates />
			<item value="{{blogsSvcRef}}/static/{{version}}!{{locale}}" name="staticLanguageRoot" />
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Blog -->
	<xsl:template match="//tns:widgetDef[@defId='Blog']/tns:itemSet/tns:item[@name='atomFeedUrl']" priority="2.0">
			<item value="{{blogsSvcRef}}/atom_form/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" name="atomFeedUrl"/>
			<item value="{{blogsSvcRef}}/atom/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" name="atomFeedUrl.basic"/>
			<item value="{{blogsSvcRef}}/oauth/atom/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" name="atomFeedUrl.oauth"/>
	</xsl:template>

	<!-- finish up items for Blog -->
	<xsl:template match="//tns:widgetDef[@defId='Blog']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
			<item name="atomPubUrl" value="{{blogsSvcRef}}/api_form/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" />
			<item name="atomPubUrl.basic" value="{{blogsSvcRef}}/api/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" />
			<item name="atomPubUrl.oauth" value="{{blogsSvcRef}}/oauth/api/blogs?commUuid={{resourceId}}&amp;blogType=communityblog" />
	</xsl:template>

	<!-- finish up items for Blog -->
	<xsl:template match="//tns:widgetDef[@defId='Blog']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
			<item name="searchUrl" value="{{blogsSvcRef}}/atom_form?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=entry&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
			<item name="searchUrl.basic" value="{{blogsSvcRef}}/atom?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=entry&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
            <item name="searchUrl.oauth" value="{{blogsSvcRef}}/oauth/atom?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=entry&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
	</xsl:template>


	<xsl:template match="//tns:widgetDef[@defId='IdeationBlog']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{blogsSvcRef}/static/{version}!{locale}/iwidgets/ideationblog/blogsWidget.jsp</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Blogs16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>

	</xsl:template>

	<!-- finish up itemSet for IdeationBlog -->
	<xsl:template match="//tns:widgetDef[@defId='IdeationBlog']/tns:itemSet" priority="2.0">
		<xsl:copy>
			<xsl:apply-templates />
			<item value="{{blogsSvcRef}}/static/{{version}}!{{locale}}" name="staticLanguageRoot" />
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for IdeationBlog -->
	<xsl:template match="//tns:widgetDef[@defId='IdeationBlog']/tns:itemSet/tns:item[@name='atomFeedUrl']" priority="2.0">
			<item name="atomFeedUrl" value="{{blogsSvcRef}}/atom_form/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />
			<item name="atomFeedUrl.basic" value="{{blogsSvcRef}}/atom/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />
			<item name="atomFeedUrl.oauth" value="{{blogsSvcRef}}/oauth/atom/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />					
	</xsl:template>

	<!-- finish up items for IdeationBlog -->
	<xsl:template match="//tns:widgetDef[@defId='IdeationBlog']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
			<item name="atomPubUrl" value="{{blogsSvcRef}}/api_form/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />
			<item name="atomPubUrl.basic" value="{{blogsSvcRef}}/api/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />
			<item name="atomPubUrl.oauth" value="{{blogsSvcRef}}/oauth/api/blogs?commUuid={{resourceId}}&amp;blogType=communityideationblog" />
	</xsl:template>

	<!-- finish up items for IdeationBlog -->
	<xsl:template match="//tns:widgetDef[@defId='IdeationBlog']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
			<item name="searchUrl" value="{{blogsSvcRef}}/atom_form?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=idea&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
			<item name="searchUrl.basic" value="{{blogsSvcRef}}/atom?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=idea&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
			<item name="searchUrl.oauth" value="{{blogsSvcRef}}/oauth/atom?search={{searchTerm}}&amp;commUuid={{resourceId}}&amp;t=idea&amp;ps=5&amp;page=0&amp;sortby=0&amp;order=desc&amp;lang=en" />
	</xsl:template>



	<!-- Add event type to Activities -->
	<xsl:template match="//tns:widgetDef[@defId='Activities']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.communityactivities/ActivityList.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Activities16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- finish up itemSet for Activities -->
	<xsl:template match="//tns:widgetDef[@defId='Activities']/tns:lifecycle" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
			     <xsl:copy />
		  </xsl:for-each>
			<xsl:apply-templates />
            <event doesNotRequireWidget="true">community.org.changed</event>
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Activities -->
	<xsl:template match="//tns:widgetDef[@defId='Activities']/tns:itemSet/tns:item[@name='atomFeedUrl']" priority="2.0">
            <item name="atomFeedUrl" value="{{activitiesSvcRef}}/service/atom2/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no" />
            <item name="atomFeedUrl.forms" value="{{activitiesSvcRef}}/service/atom2/forms/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no" />
            <item name="atomFeedUrl.oauth" value="{{activitiesSvcRef}}/oauth/atom2/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no" />
	</xsl:template>

	<!-- finish up items for Activities -->
	<xsl:template match="//tns:widgetDef[@defId='Activities']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
            <item name="atomPubUrl" value="{{activitiesSvcRef}}/service/atom2/service?commUuid={{resourceId}}" /> 
            <item name="atomPubUrl.forms" value="{{activitiesSvcRef}}/service/atom2/forms/service?commUuid={{resourceId}}" /> 
            <item name="atomPubUrl.oauth" value="{{activitiesSvcRef}}/oauth/atom2/service?commUuid={{resourceId}}" /> 
	</xsl:template>

	<!-- finish up items for Activities -->
	<xsl:template match="//tns:widgetDef[@defId='Activities']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
            <item name="searchUrl" value="{{activitiesSvcRef}}/service/atom2/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no&amp;search={{searchTerm}}" />
            <item name="searchUrl.forms" value="{{activitiesSvcRef}}/service/atom2/forms/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no&amp;search={{searchTerm}}" />
            <item name="searchUrl.oauth" value="{{activitiesSvcRef}}/oauth/atom2/activities?commUuid={{resourceId}}&amp;public=yes&amp;authenticate=no&amp;search={{searchTerm}}" />
	</xsl:template>



	<!-- Add event type to Wiki -->
	<xsl:template match="//tns:widgetDef[@defId='Wiki']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Wikis16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Wiki -->
	<xsl:template match="//tns:widgetDef[@defId='Wiki']/tns:itemSet/tns:item[@name='atomFeedUrl']" priority="2.0">
			<item name="atomFeedUrl" value="{{wikisSvcRef}}/form/api/communitywiki/{{resourceId}}/feed"/>
			<item name="atomFeedUrl.basic" value="{{wikisSvcRef}}/basic/api/communitywiki/{{resourceId}}/feed"/>
			<item name="atomFeedUrl.oauth" value="{{wikisSvcRef}}/oauth/api/communitywiki/{{resourceId}}/feed"/>
	</xsl:template>

	<!-- finish up items for Wiki -->
	<xsl:template match="//tns:widgetDef[@defId='Wiki']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
			<item name="atomPubUrl" value="{{wikisSvcRef}}/form/api/community/{{resourceId}}/introspection"/>
			<item name="atomPubUrl.basic" value="{{wikisSvcRef}}/basic/api/community/{{resourceId}}/introspection"/>
			<item name="atomPubUrl.oauth" value="{{wikisSvcRef}}/oauth/api/community/{{resourceId}}/introspection"/>
	</xsl:template>

	<!-- finish up items for Wiki -->
	<xsl:template match="//tns:widgetDef[@defId='Wiki']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
			<item name="searchUrl" value="{{wikisSvcRef}}/form/api/communitywiki/{{resourceId}}/feed?search={{searchTerm}}"/>
			<item name="searchUrl.basic" value="{{wikisSvcRef}}/basic/api/communitywiki/{{resourceId}}/feed?search={{searchTerm}}"/>
            <item name="searchUrl.oauth" value="{{wikisSvcRef}}/oauth/api/communitywiki/{{resourceId}}/feed?search={{searchTerm}}"/>
	</xsl:template>


	<!-- Add event type to Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.forums/widgets/topicList.xml?version={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='modes'">
                        <xsl:attribute name="modes">view edit fullpage search</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- finish up itemSet for Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:lifecycle" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
			   <xsl:copy />
		  </xsl:for-each>
			<xsl:apply-templates />
            <event>community.org.changed</event>
		</xsl:copy>
	</xsl:template>

	<!-- finish up items for Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:itemSet/tns:item[@name='atomFeedUrl']" priority="2.0">
			<item name="atomFeedUrl" value="{{forumsSvcRef}}/atom/forms/topics?communityUuid={{resourceId}}"/>
			<item name="atomFeedUrl.basic" value="{{forumsSvcRef}}/atom/topics?communityUuid={{resourceId}}"/>
			<item name="atomFeedUrl.oauth" value="{{forumsSvcRef}}/oauth/atom/topics?communityUuid={{resourceId}}"/>
	</xsl:template>

	<!-- finish up items for Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:itemSet/tns:item[@name='atomPubUrl']" priority="2.0">
			<item name="atomPubUrl" value="{{forumsSvcRef}}/atom/forms/service?communityUuid={{resourceId}}" />
			<item name="atomPubUrl.basic" value="{{forumsSvcRef}}/atom/service?communityUuid={{resourceId}}" />
			<item name="atomPubUrl.oauth" value="{{forumsSvcRef}}/oauth/atom/service?communityUuid={{resourceId}}" />
	</xsl:template>

	<!-- finish up items for Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:itemSet/tns:item[@name='searchUrl']" priority="2.0">
			<item name="searchUrl" value="{{forumsSvcRef}}/atom/forms/topics?communityUuid={{resourceId}}&amp;search={{searchTerm}}"/>
			<item name="searchUrl.basic" value="{{forumsSvcRef}}/atom/topics?communityUuid={{resourceId}}&amp;search={{searchTerm}}"/>
			<item name="searchUrl.oauth" value="{{forumsSvcRef}}/oauth/atom/topics?communityUuid={{resourceId}}&amp;search={{searchTerm}}"/>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:itemSet/tns:item[@name='forumsModulePath']" priority="2.0">
	</xsl:template>
	<xsl:template match="//tns:widgetDef[@defId='Forum']/tns:itemSet/tns:item[@name='jsBundle']" priority="2.0">
	</xsl:template>



	<xsl:template match="//tns:widgetDef[@defId='SubcommunityNav']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/lconn.comm/widgets/subcommunityNav/subcommunitynav.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='description'">
                        <xsl:attribute name="description">subcommunityDescription</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Places16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='SubcommunityNav']/tns:itemSet/tns:item[@name='subcommunitiesFeedUrl']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='value'">
                        <xsl:attribute name="value">{communitiesSvcRef}/service/atom{authSubpath}/community/subcommunities?communityUuid={resourceId}&amp;ps=200</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>		
	</xsl:template>



	<!-- Add event type to Files -->
	<xsl:template match="//tns:widgetDef[@defId='Files']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{filesSvcRef}/static/{version}!{locale}/iwidgets/CommunityReferentialWidget/widget.xml</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Files16.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="//tns:widgetDef[@defId='Files']/tns:itemSet" priority="2.0">
		<xsl:copy>
            <item name="filesRoot" value="{{filesSvcRef}}" />
            <item name="filesJsRoot" value="{{filesSvcRef}}/static/js/{{version}}!{{locale}}" />
            <item name="contextRoot" value="{{contextRoot}}"/>
		    <item name="staticLanguageRoot" value="{{filesSvcRef}}/static/{{version}}!{{locale}}/" />
			<item name="atomPubUrl" value="{{filesSvcRef}}/form/api/community/{{resourceId}}/introspection"/>
			<item name="atomPubUrl.basic" value="{{filesSvcRef}}/basic/api/community/{{resourceId}}/introspection"/>
			<item name="atomPubUrl.oauth" value="{{filesSvcRef}}/oauth/api/community/{{resourceId}}/introspection"/>
			<item name="atomFeedUrl" value="{{filesSvcRef}}/form/api/communitycollection/{{resourceId}}/feed"/>
			<item name="atomFeedUrl.basic" value="{{filesSvcRef}}/basic/api/communitycollection/{{resourceId}}/feed"/>
			<item name="atomFeedUrl.oauth" value="{{filesSvcRef}}/oauth/api/communitycollection/{{resourceId}}/feed"/>
			<item name="searchUrl" value="{{filesSvcRef}}/form/api/communitylibrary/{{resourceId}}/feed?search={{searchTerm}}"/>
			<item name="searchUrl.basic" value="{{filesSvcRef}}/basic/api/communitylibrary/{{resourceId}}/feed?search={{searchTerm}}"/>
			<item name="searchUrl.oauth" value="{{filesSvcRef}}/oauth/api/communitylibrary/{{resourceId}}/feed?search={{searchTerm}}"/>
		</xsl:copy>
	</xsl:template>

	<!-- Add event type to Forum -->
	<xsl:template match="//tns:widgetDef[@defId='Files']/tns:lifecycle" priority="2.0">
		<xsl:copy>

		<xsl:for-each
			select="@*">
			            <xsl:copy />
		</xsl:for-each>

			<xsl:apply-templates />
                 <event doesNotRequireWidget="true">community.org.changed</event>
		</xsl:copy>
	</xsl:template>



	<xsl:template match="//tns:widgetDef[@defId='MediaGallery']" priority="2.0">
		<xsl:copy>
		  <xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='url'">
                        <xsl:attribute name="url">{webresourcesSvcRef}/web/quickr.lw/widgetDefs/MediaGalleryWidget_CMIS.xml?etag={version}</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='description'">
                        <xsl:attribute name="description">MediaGallery.description</xsl:attribute>
					</xsl:when>

					<xsl:when test="name()='iconUrl'">
                        <xsl:attribute name="iconUrl">{webresourcesSvcRef}/web/quickr.lw/images/mediaGalleryIcon.png</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
		<xsl:apply-templates />
		</xsl:copy>

            <widgetDef primaryWidget="false" showInPalette="true" description="calendarDescription" modes="view edit fullpage search" uniqueInstance="true" url="{{communitiesSvcRef}}/calendar/Calendar.xml?version={{version}}" 
                        iconUrl="{{contextRoot}}/nav/common/images/iconCalendar16.png" helpLink="{{helpSvcRef}}/topic/com.ibm.lotus.connections.communities.help/community_events_frame.html" defId="Calendar" >
 		       <itemSet>
 		         <item name="calendarRoot" value="{{communitiesSvcRef}}/calendar" />
 		         <item name="communitiesBaseUrl" value="{{communitiesSvcRef}}" />
 		         <item name="defaultView" value="list" />
 		 		 <item name="version" value="{{version}}" />
 		 		 <item name="profilesBaseUrl" value="{{profilesSvcRef}}" />
				 <item name="atomPubUrl" value="{{communitiesSvcRef}}/calendar/atom_form/calendar/service?calendarUuid={{resourceId}}" />
				 <item name="atomPubUrl.basic" value="{{communitiesSvcRef}}/calendar/atom/calendar/service?calendarUuid={{resourceId}}" />
				 <item name="atomPubUrl.oauth" value="{{communitiesSvcRef}}/calendar/oauth/atom/calendar/service?calendarUuid={{resourceId}}" />
				 <item name="atomFeedUrl" value="{{communitiesSvcRef}}/calendar/atom_form/calendar/event?type=event&amp;calendarUuid={{resourceId}}"/>
				 <item name="atomFeedUrl.basic" value="{{communitiesSvcRef}}/calendar/atom/calendar/event?type=event&amp;calendarUuid={{resourceId}}"/>
				 <item name="atomFeedUrl.oauth" value="{{communitiesSvcRef}}/calendar/oauth/atom/calendar/event?type=event&amp;calendarUuid={{resourceId}}"/>
				 <item name="searchUrl" value="{{communitiesSvcRef}}/calendar/atom_form/search?search={{searchTerm}}&amp;calendarUuid={{resourceId}}"/>
				 <item name="searchUrl.basic" value="{{communitiesSvcRef}}/calendar/atom/search?search={{searchTerm}}&amp;calendarUuid={{resourceId}}"/>
				 <item name="searchUrl.oauth" value="{{communitiesSvcRef}}/calendar/oauth/atom/search?search={{searchTerm}}&amp;calendarUuid={{resourceId}}"/>
 		       </itemSet>
 		       <lifecycle remoteHandlerURL="{{communitiesInterSvcRef}}/calendar/handleEvent" remoteHandlerAuthenticationAlias="connectionsAdmin">
 		         <event>remote.app.added</event>
 		         <event>remote.app.removed</event>
 		         <event>community.prepare.delete</event>
 		         <event>community.visibility.changed</event>
 		         <event>community.updated</event>
 		       </lifecycle>
            </widgetDef>

            <widgetDef modes="view" uniqueInstance="true" showInPalette="false" description="updatesDescription"
                url="{{webresourcesSvcRef}}/web/com.ibm.social.as.lconn/widgets/communitiesActivityStream.xml?etag={{version}}"                
                navBarLink="{{contextRoot}}/service/html/community/updates?communityUuid={{resourceId}}&amp;filter=all"                
                helpLink="{{helpSvcRef}}/topic/com.ibm.lotus.connections.communities.help/t_com_updates.html"
                fixedPosition="true"  defId="RecentUpdates">
                 <itemSet>
                    <item name="atomFeedUrl" value="{{opensocialSvcRef}}/rest/ublog/c!{{resourceId}}/@all?format=atom" />
					<item name="atomFeedUrl.basic" value="{{opensocialSvcRef}}/basic/rest/ublog/c!{{resourceId}}/@all?format=atom" />
					<item name="atomFeedUrl.oauth" value="{{opensocialSvcRef}}/oauth/rest/ublog/c!{{resourceId}}/@all?format=atom" />
                 </itemSet>                   
            </widgetDef> 

            <widgetDef modes="view edit search" prerequisite="microblogging" uniqueInstance="true" showInPalette="true"
                description="updatesDescription"
                url="{{webresourcesSvcRef}}/web/com.ibm.social.as.lconn/widgets/communitiesActivityStream.xml?etag={{version}}"                
                navBarLink="{{contextRoot}}/service/html/community/updates?communityUuid={{resourceId}}&amp;filter=status"                
                helpLink="{{helpSvcRef}}/topic/com.ibm.lotus.connections.communities.help/t_com_updates.html"
                iconUrl="{{webresourcesSvcRef}}/web/com.ibm.oneui3.styles/imageLibrary/Icons/ComponentsGray/StatusUpdateGray16.png"
                fixedPosition="true" defId="StatusUpdates">
                 <itemSet>
					<item name="atomFeedUrl" value="{{opensocialSvcRef}}/rest/ublog/urn:lsid:lconn.ibm.com:communities.community:{{resourceId}}/@all?format=atom" />
					<item name="atomFeedUrl.basic" value="{{opensocialSvcRef}}/basic/rest/ublog/urn:lsid:lconn.ibm.com:communities.community:{{resourceId}}/@all?format=atom" />
					<item name="atomFeedUrl.oauth" value="{{opensocialSvcRef}}/oauth/rest/ublog/urn:lsid:lconn.ibm.com:communities.community:{{resourceId}}/@all?format=atom" />
					<item name="searchUrl" value="{{searchSvcRef}}/atomfba/mysearch/results?scope=status_updates&amp;social=%7B%22type%22%3A%22community%22%2C%22id%22%3A%22{{resourceId}}%22%7D&amp;query={{searchTerm}}"/>
					<item name="searchUrl.basic" value="{{searchSvcRef}}/atom/mysearch/results?scope=status_updates&amp;social=%7B%22type%22%3A%22community%22%2C%22id%22%3A%22{{resourceId}}%22%7D&amp;query={{searchTerm}}" />
					<item name="searchUrl.oauth" value="{{searchSvcRef}}/oauth/atom/mysearch/results?scope=status_updates&amp;social=%7B%22type%22%3A%22community%22%2C%22id%22%3A%22{{resourceId}}%22%7D&amp;query={{searchTerm}}"/>
                 </itemSet>
                 <lifecycle remoteHandlerURL="{{newsInterSvcRef}}/widget/communityHandler.do" 
                    remoteHandlerAuthenticationAlias="connectionsAdmin">
                    <event>remote.app.added</event> 
                    <event>remote.app.removed</event> 
                    <event>widget.added</event>
                    <event>widget.removed</event>
                    <event>remote.app.transfer</event>
                    <event>community.visibility.changed</event> 
                    <event>community.updated</event> 
                    <event>community.org.changed</event>
                    <event>community.prepare.delete</event>
                </lifecycle>  
            </widgetDef>

            <widgetDef primaryWidget="false" showInPalette="true" description="relatedCommunitiesDescription" modes="view fullpage" uniqueInstance="true"
                url="{{communitiesSvcRef}}/recomm/Recomm.xml?version={{version}}"
                iconUrl="{{webresourcesSvcRef}}/web/com.ibm.oneui3.styles/imageLibrary/Icons/Components/Places16.png"
                defId="RelatedCommunities">
                <itemSet>
                  <item name="recommRoot" value="{{communitiesSvcRef}}/recomm" />
                  <item name="communitiesBaseUrl" value="{{communitiesSvcRef}}" />
                  <item name="version" value="{{version}}" />
				  <item name="atomPubUrl" value="{{communitiesSvcRef}}/recomm/atom_form/service?communityUuid={{resourceId}}"/>
				  <item name="atomPubUrl.basic" value="{{communitiesSvcRef}}/recomm/atom/service?communityUuid={{resourceId}}"/>
				  <item name="atomPubUrl.oauth" value="{{communitiesSvcRef}}/recomm/oauth/atom/service?communityUuid={{resourceId}}"/>
				  <item name="feedUrl" value="{{communitiesSvcRef}}/recomm/atom_form/relatedCommunities?communityUuid={{resourceId}}&amp;ps=10"/> 
				  <item name="feedUrl.basic" value="{{communitiesSvcRef}}/recomm/atom/relatedCommunities?communityUuid={{resourceId}}&amp;ps=10"/> 
				  <item name="feedUrl.oauth" value="{{communitiesSvcRef}}/recomm/oauth/atom/relatedCommunities?communityUuid={{resourceId}}&amp;ps=10"/> 
                </itemSet>
                <lifecycle remoteHandlerURL="{{communitiesInterSvcRef}}/recomm/handleEvent" remoteHandlerAuthenticationAlias="connectionsAdmin">
                  <event>remote.app.added</event>
                  <event>remote.app.removed</event>
                  <event>community.prepare.delete</event>
                  <event>community.visibility.changed</event>
                  <event>community.updated</event>
                </lifecycle>
            </widgetDef>            
                                       
	</xsl:template>


	<xsl:template match="//tns:templates/tns:template" priority="2.0">
		<xsl:copy>
				<xsl:attribute name="id">default</xsl:attribute>
			<xsl:apply-templates />
                <widgetInstance uiLocation="col2statusposts" defIdRef="StatusUpdates" instanceId="StatusUpdates1"/>
		</xsl:copy>
	</xsl:template>


	<xsl:template match="//tns:layout/tns:page[@pageId='communityOverview']" priority="2.0">
		<xsl:copy>
            <xsl:attribute name="pageId">communityOverview</xsl:attribute>
                <widgetInstance uiLocation="col2recentposts" defIdRef="RecentUpdates" /> 
                <widgetInstance uiLocation="col3" defIdRef="Members"/>
		</xsl:copy>
	</xsl:template>
<!-- communities 06 end -->

<!-- all 01 start -->
	<!-- For all other nodes in the document, replace them exactly -->
	<xsl:template match="node() | @*" priority="0.0">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>
<!-- all 01 end -->



	<xsl:template name="string-replace-all">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="with" />
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$with" />
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="with" select="$with" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
