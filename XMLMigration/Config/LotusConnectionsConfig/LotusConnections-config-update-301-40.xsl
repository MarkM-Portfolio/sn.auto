<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2001, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
    xmlns="http://www.ibm.com/LotusConnections-config"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:sloc="http://www.ibm.com/service-location"
	xmlns:tns="http://www.ibm.com/LotusConnections-config"
	xmlns:tns1="http://www.ibm.com/uiextensions-config" 
	xsi:schemaLocation="http://www.ibm.com/LotusConnections-config LotusConnections-config.xsd">

<xsl:variable name="uiextensions" select="document('uiextensions-config.xml')"/>

  <xsl:attribute-set name="sametimeAttrs">
	<xsl:attribute name="isConnectClient"><xsl:value-of select="($uiextensions/tns1:config/tns1:extensions/tns1:extension[@name='lc.IMAwareness']/tns1:params/tns1:param[@name='isConnectClient']/@value)[1]"/></xsl:attribute>
	<xsl:attribute name="enabled"><xsl:value-of select="($uiextensions/tns1:config/tns1:extensions/tns1:extension[@name='lc.IMAwareness']/@enabled)[1]"/></xsl:attribute>
	<xsl:attribute name="ssl_enabled"><xsl:value-of select="($uiextensions/tns1:config/tns1:extensions/tns1:extension[@name='lc.IMAwareness']/@enabled)[1]"/></xsl:attribute>
	<xsl:attribute name="serviceName">sametimeProxy</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sametimeHrefs">
	<xsl:attribute name="href"><xsl:value-of select="($uiextensions/tns1:config/tns1:extensions/tns1:extension[@name='lc.IMAwareness']/tns1:params/tns1:param[@name='lconnProxySvcUrl']/@value)[1]"/></xsl:attribute>
	<xsl:attribute name="ssl_href"><xsl:value-of select="($uiextensions/tns1:config/tns1:extensions/tns1:extension[@name='lc.IMAwareness']/tns1:params/tns1:param[@name='lconnProxySvcUrlSSL']/@value)[1]"/></xsl:attribute>
  </xsl:attribute-set>

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:preserve-space elements="*"/>

	<xsl:template match="/tns:config" priority="1.0">
<xsl:comment>
			*****************************************************************

			Licensed Materials - Property of IBM
			5724_S68

			Copyright IBM Corp. 2001, 2011 All Rights Reserved.

			US Government Users Restricted Rights - Use, duplication or
			disclosure restricted by GSA ADP Schedule Contract with
			IBM Corp.

			*****************************************************************
</xsl:comment>
		<config xmlns="http://www.ibm.com/LotusConnections-config"
			xmlns:sloc="http://www.ibm.com/service-location"
			xmlns:tns="http://www.ibm.com/LotusConnections-config"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			id="LotusConnections"
			buildlevel="LC4.0_20120605_1154"
			xsi:schemaLocation="http://www.ibm.com/LotusConnections-config LotusConnections-config.xsd">
			
			<xsl:copy-of select="tns:deployment" />
			
			<xsl:for-each select="sloc:serviceReference">

				<xsl:choose>
<!-- add bookmarklet service after activities -->
					<xsl:when test="@serviceName='activities'">
					<xsl:copy>
						<xsl:call-template
							name="migrate-activities-component" />
					</xsl:copy>						
		<sloc:serviceReference serviceName="bookmarklet" 
			enabled="false" 
			ssl_enabled="false"> 
			<sloc:href>
				<sloc:hrefPathPrefix>/connections/bookmarklet</sloc:hrefPathPrefix>
				<sloc:static href="admin_replace" ssl_href="admin_replace"/>
				<sloc:interService href="admin_replace"/>
			</sloc:href>
		</sloc:serviceReference>
		
					</xsl:when>

<!-- communities: change acf_config_file="acf-config.xml"   to 
            acf_config_file="acf-config-nf.xml" -->				
					<xsl:when test="@serviceName='communities'">
					<xsl:copy>
						<xsl:call-template
							name="migrate-communities-component" />
					</xsl:copy>
					</xsl:when>
		
<!-- forums: change acf_config_file="acf-config.xml"   to 
            acf_config_file="acf-config-flash.xml" -->				
					<xsl:when test="@serviceName='forums'">
					<xsl:copy>
						<xsl:call-template
							name="migrate-forums-component" />
					</xsl:copy>
					</xsl:when>
		
<!-- dogear: add acf_config_file="acf-config.xml"  
-->				
					<xsl:when test="@serviceName='dogear'">
					<xsl:copy>
						<xsl:call-template
							name="migrate-dogear-component" />
					</xsl:copy>
					</xsl:when>
		
<!-- add opensocial and cre services after news -->				
		<xsl:when test="@serviceName='news'">
			<xsl:copy-of select="." />

     <sloc:serviceReference serviceName="opensocial" 
            enabled="false"
            ssl_enabled="false"
            bootstrapHost="admin_replace"
            bootstrapPort="admin_replace"
            clusterName="">
        <sloc:href>
            <sloc:hrefPathPrefix>/connections/opensocial</sloc:hrefPathPrefix>
            <sloc:static href="admin_replace" ssl_href="admin_replace"/>
            <sloc:interService href="admin_replace"/>
        </sloc:href>
    </sloc:serviceReference>
      
		
		<sloc:serviceReference serviceName="cre" 
		    enabled="false"
		    ssl_enabled="false">
			<sloc:href>
				<sloc:hrefPathPrefix>/connections/cre</sloc:hrefPathPrefix>
				<sloc:static href="admin_replace" ssl_href="admin_replace"/>
				<sloc:interService href="admin_replace"/>
			</sloc:href>
		</sloc:serviceReference>  		 		

	<sloc:serviceReference serviceName="socialmail" 
		    enabled="false"
		    ssl_enabled="false">
			<sloc:href>
				<sloc:hrefPathPrefix>/socmail-client/gadgets</sloc:hrefPathPrefix>
				<sloc:static href="admin_replace" ssl_href="admin_replace"/>
				<sloc:interService href="admin_replace"/>
			</sloc:href>
	</sloc:serviceReference>      

					</xsl:when>

<!-- add metrics and cognos services after moderation -->				
					<xsl:when test="@serviceName='moderation'">
						<xsl:copy-of select="." />

		<sloc:serviceReference serviceName="metrics" 
			enabled="false" 
			ssl_enabled="false"
			bootstrapHost="admin_replace" 
			bootstrapPort="admin_replace" 
			clusterName=""> 
			<sloc:href>
				<sloc:hrefPathPrefix>/metrics</sloc:hrefPathPrefix>
				<sloc:static href="admin_replace" ssl_href="admin_replace"/>
				<sloc:interService href="admin_replace"/>
			</sloc:href>
		</sloc:serviceReference>
		
		<sloc:serviceReference serviceName="cognos" 
			enabled="false" 
			ssl_enabled="false"
			bootstrapHost="admin_replace" 
			bootstrapPort="admin_replace" 
			clusterName=""> 
			<sloc:href>
				<sloc:hrefPathPrefix>/cognos</sloc:hrefPathPrefix>
				<sloc:static href="admin_replace" ssl_href="admin_replace"/>
				<sloc:interService href="admin_replace"/>
			</sloc:href>
		</sloc:serviceReference>

					</xsl:when>


					<xsl:when test="@serviceName='sand'">
					<xsl:copy>
<!-- remove <sloc:href> from sand -->
						<xsl:call-template
							name="migrate-sand-component" />
					</xsl:copy>

<!-- add webresources and oauth after sand -->				

    <sloc:serviceReference serviceName="webresources"
        enabled="false" 
        ssl_enabled="false">
        <sloc:href>
            <sloc:hrefPathPrefix>/connections/resources</sloc:hrefPathPrefix>
            <sloc:static href="admin_replace" ssl_href="admin_replace"/>
            <sloc:interService href="admin_replace"/>
        </sloc:href>
    </sloc:serviceReference>

    <sloc:serviceReference serviceName="oauth" 
            enabled="false"
            ssl_enabled="false">
            <sloc:href>
                <sloc:hrefPathPrefix>/connections/oauth</sloc:hrefPathPrefix>
                <sloc:static href="admin_replace" ssl_href="admin_replace"/>
                <sloc:interService href="admin_replace"/>
            </sloc:href>
    </sloc:serviceReference>

					</xsl:when>

<!-- add bss and microblogging service after mediaGallery -->				
					<xsl:when test="@serviceName='mediaGallery'">
						<xsl:copy-of select="." />

	<sloc:serviceReference serviceName="bss" 
		enabled="false" 
		ssl_enabled="false" 
		bootstrapHost="admin_replace"
		bootstrapPort="admin_replace"
		clusterName="">
		<sloc:href>
			<sloc:hrefPathPrefix>/manage</sloc:hrefPathPrefix>
			<sloc:static href="admin_replace" ssl_href="admin_replace"/>
			<sloc:interService href="admin_replace"/>
		</sloc:href>		
	</sloc:serviceReference>
	
	<sloc:serviceReference serviceName="microblogging"
		enabled="true"  
		ssl_enabled="true"
		bootstrapHost="admin_replace"
		bootstrapPort="admin_replace"
		clusterName="">		
		<sloc:href>
            <sloc:hrefPathPrefix>/connections/opensocial</sloc:hrefPathPrefix>
            <sloc:static href="admin_replace" ssl_href="admin_replace"/>
            <sloc:interService href="admin_replace"/>
        </sloc:href>
    </sloc:serviceReference>

	</xsl:when>


	<xsl:when test="@serviceName='sametimeLinks'">
	<sloc:serviceReference
		xsl:use-attribute-sets="sametimeAttrs">
            <sloc:href>
                <sloc:hrefPathPrefix></sloc:hrefPathPrefix>
                <sloc:static xsl:use-attribute-sets="sametimeHrefs">
                </sloc:static>
                <sloc:interService href="admin_replace"/>
            </sloc:href>
    </sloc:serviceReference>

	</xsl:when>

	
					<xsl:otherwise>
						<xsl:copy-of select="." />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>	
				
		<xsl:copy-of select="tns:customAuthenticator" />

		<xsl:copy-of select="tns:allowedContent" />
		
<xsl:comment>
     To enable virus scanning, first delete the empty avFilter element below.
     Then, uncomment the avFilter in the comment and replace hostname as appropriate
     Replace myScannerService with the appropriate name for your scanner (eg
     AVSCAN for Symantec, RESPMOD for McAfee)
</xsl:comment>				


<xsl:copy-of select="tns:avFilter" />

	<xsl:choose>
		<xsl:when test="tns:avFilter[@class]"/>
	
		<xsl:otherwise>
<xsl:comment>
    &lt;avFilter class="AVScannerICAP"&gt;
    &lt;property&gt;av.scanner.servers=myscanner.host.com&lt;/property&gt;
    &lt;property&gt;exception.on.virus=yes&lt;/property&gt;
    &lt;property&gt;av.scanner.service=myScannerService&lt;/property&gt;
    &lt;/avFilter&gt;
</xsl:comment>
		</xsl:otherwise>
	</xsl:choose>

		<xsl:copy-of select="tns:forceConfidentialCommunications" />

		<xsl:copy-of select="tns:exposeEmail" />

<xsl:comment>
    When enabling the language selector by setting enabled to true, there are
    options to consider:

    You can use the defaultLanguage attribute to define a fallback language in which to 
    display the user interface if the language specified by the browser is not included 
    in the language elements list. It there are no language elements specified, 
    the language specified in this attribute is the only language in which Lotus 
    Connections is displayed.

	Set the cookieName attribute if you want to use a specific, defined name for
	the cookie used to track the selected language. Default is lcLang.

	Set the cookieDomain if you use multiple host names for Connections features
	and want to ensure this setting works across all features. For example if your
	servers are profiles.acme.com and dogear.acme.com set the cookie domain to ".acme.com"
	(Note the leading period!)

	Set usePermanentCookie to true if you wish to have the user language choice persist
	across multiple web sessions.

	If you wish to enforce a single language for all users then set the 
	defaultLanguage attribute and do not specify any child language elements, or
	just the defaultLanguage.

	Specify a language element for each language you want in the selector dropdown. 
	Chose the lang= value from the list below - do not use any other values. 
	As you can see, there is a value for each language supported by Connections. 
	The element value is the name of the language. The name of the language will be displayed 
	in the UI and non-latin characters must be specified in javascript escaped unicode format
	(as shown below).  At the end of each language element either delete the English name of the 
	language and the surrounding characters, or replace a single dash with a double dash in all instances.

		&lt;language lang="en"&gt;English&lt;/language&gt;                                           lt;!-English-gt;
		&lt;language lang="zh"&gt;\u4e2d\u6587 (\u200f\u7b80\u4f53)&lt;/language&gt;                 lt;!-Chinese, Simplified-gt;
		&lt;language lang="zh_tw"&gt;\u4e2d\u6587 (\u200f\u7e41\u9ad4)&lt;/language&gt;              lt;!-Chinese, Traditional-gt;
		&lt;language lang="ja"&gt;\u65e5\u672c\u8a9e&lt;/language&gt;                                lt;!-Japanese-gt;
		&lt;language lang="ko"&gt;\ud55c\uad6d\uc5b4&lt;/language&gt;                                lt;!-Korean-gt;
		&lt;language lang="fr"&gt;Fran\u00e7ais&lt;/language&gt;                                     lt;!-French-gt;
		&lt;language lang="de"&gt;Deutsch&lt;/language&gt;                                           lt;!-German-gt;
		&lt;language lang="it"&gt;Italiano&lt;/language&gt;                                          lt;!-Italian-gt;
		&lt;language lang="es"&gt;Espa\u00f1ol&lt;/language&gt;                                      lt;!-Spanish-gt;
		&lt;language lang="pt_br"&gt;Portugu\u00eas (\u200fBrasil)&lt;/language&gt;                  lt;!-Portuguese, Brazilian-gt;
		&lt;language lang="cs"&gt;\u010ce\u0161tina&lt;/language&gt;                                 lt;!-Czech-gt;
		&lt;language lang="da"&gt;Dansk&lt;/language&gt;                                             lt;!-Danish-gt;
		&lt;language lang="nl"&gt;Nederlands&lt;/language&gt;                                        lt;!-Dutch-gt;
		&lt;language lang="fi"&gt;suomi&lt;/language&gt;                                             lt;!-Finnish-gt;
		&lt;language lang="el"&gt;\u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ac&lt;/language&gt;  lt;!-Greek-gt;
		&lt;language lang="hu"&gt;Magyar&lt;/language&gt;                                            lt;!-Hungarian-gt;
		&lt;language lang="no"&gt;Norsk (\u200fBokm\u00e5l)&lt;/language&gt;                         lt;!-Norewgian-gt;
		&lt;language lang="pl"&gt;polski&lt;/language&gt;                                            lt;!-Polish-gt;
		&lt;language lang="pt"&gt;Portugu\u00eas (Portugal)&lt;/language&gt;                         lt;!-Portuguese-gt;
		&lt;language lang="ru"&gt;\u0420\u0443\u0441\u0441\u043a\u0438\u0439&lt;/language&gt;        lt;!-Russian-gt;
		&lt;language lang="sv"&gt;Svenska&lt;/language&gt;                                           lt;!-Swedish-gt;
		&lt;language lang="sl"&gt;sloven\u0161\u010dina&lt;/language&gt;                             lt;!-Slovenian-gt;
		&lt;language lang="tr"&gt;T\u00fcrk\u00e7e&lt;/language&gt;                                  lt;!-Turkish-gt;
		&lt;language lang="iw"&gt;\u05e2\u05d1\u05e8\u05d9\u05ea&lt;/language&gt;                    lt;!-Hebrew-gt;
		&lt;language lang="ar"&gt;\u200f\u0627\u0644\u0639\u0631\u0628\u064a\u0629\u200f&lt;/language&gt;  lt;!-Arabic-gt;
		&lt;language lang="ca"&gt;Catal\u00e0&lt;/language&gt;                                       lt;!-Catalan-gt;
		&lt;language lang="kk"&gt;\u049a\u0430\u0437\u0430\u049b\u0448\u0430&lt;/language&gt;        lt;!-Kazakh-gt;
		&lt;language lang="th"&gt;\u0e44\u0e17\u0e22&lt;/language&gt;                                lt;!-Thai-gt;
    </xsl:comment>

		<xsl:copy-of select="tns:languageSelector" />

		<xsl:copy-of select="tns:languageSensitive" />
		<xsl:copy-of select="tns:ignorePunctuation" />
		<xsl:copy-of select="tns:transactionSetting" />

	<seedlistSettings allowUnsecuredTransfer="false">
		<attribute key="maximumPageSize" value="1000"/>
		<attribute key="maximumIncrementalQuerySpanInDays" value="30"/>
	</seedlistSettings>

		<xsl:copy-of select="tns:useRichTextEditorInBookmarklet" />

<xsl:comment>
    Support for 1 to 2 mapping. This is a search config setting that controls composition of unicode characters
    from decomposed forms like the base character and its accents or other modifiers.
</xsl:comment>

		<xsl:copy-of select="tns:oneToTwoMapping" />

		<xsl:copy-of select="tns:dynamicHosts" />

		<xsl:copy-of select="tns:resources" />

		<xsl:copy-of select="tns:versionStamp" />

		</config>
	</xsl:template>
	
<xsl:template
		name="migrate-activities-component">
		<xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='acf_config_file'">
                        <xsl:attribute name="acf_config_file">acf-config-nf.xml</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		</xsl:for-each>

		<xsl:copy-of  select="sloc:anonymousLogin"/>

		<xsl:copy-of  select="sloc:href"/>

</xsl:template>

<xsl:template
		name="migrate-communities-component">
		<xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='acf_config_file'">
                        <xsl:attribute name="acf_config_file">acf-config-nf.xml</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		</xsl:for-each>

		<xsl:copy-of  select="sloc:anonymousLogin"/>

		<xsl:copy-of  select="sloc:href"/>

</xsl:template>

<xsl:template
		name="migrate-forums-component">
		<xsl:for-each
			select="@*">
				<xsl:choose>
					<xsl:when test="name()='acf_config_file'">
                        <xsl:attribute name="acf_config_file">acf-config-nf-flash.xml</xsl:attribute>
					</xsl:when>

					<xsl:otherwise>
			            <xsl:copy />
					</xsl:otherwise>
				</xsl:choose>
		</xsl:for-each>

		<xsl:copy-of  select="sloc:anonymousLogin"/>

		<xsl:copy-of  select="sloc:href"/>

</xsl:template>

<xsl:template
		name="migrate-dogear-component">
		<xsl:for-each
			select="@*">
			            <xsl:copy />
		</xsl:for-each>

		<xsl:attribute name="acf_config_file">acf-config.xml</xsl:attribute>

		<xsl:copy-of  select="sloc:anonymousLogin"/>

		<xsl:copy-of  select="sloc:href"/>

</xsl:template>

<xsl:template
		name="migrate-sand-component">
		<xsl:for-each
			select="@*">
			<xsl:copy />
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
