<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2009, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:comm="http://www.ibm.com/connections/communities/communities-config/1.0"
	xsi:schemaLocation="http://www.ibm.com/connections/communities/communities-config/1.0 communities-config.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/comm:config">

		<xsl:comment>		
     *****************************************************************
	                                                                  
      Licensed Materials - Property of IBM                            
                                                                      
      5724-S68
	                                                                  
      Copyright IBM Corp. 2001, 2012  All Rights Reserved.            
                                                                      
      US Government Users Restricted Rights - Use, duplication or     
      disclosure restricted by GSA ADP Schedule Contract with         
      IBM Corp.                                                       
                                                                      
     *****************************************************************
		</xsl:comment>
				
		
		<comm:config id="communities"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://www.ibm.com/connections/communities/communities-config/1.0 communities-config.xsd"
				xmlns:comm="http://www.ibm.com/connections/communities/communities-config/1.0">
		
			<xsl:comment> START COMMUNITY SECURITY CONFIG </xsl:comment>
			<xsl:copy-of select="comm:security"/>
			<xsl:comment> END COMMUNITY SECURITY CONFIG </xsl:comment>
		
			<xsl:comment> START COMMUNITY PAGING CONFIG </xsl:comment>
			<xsl:copy-of select="comm:pagingSupport"/>
			<xsl:comment> END COMMUNITY PAGING CONFIG </xsl:comment>
		
			<xsl:copy-of select="comm:activeContentFilter"/>		
			<xsl:copy-of select="comm:descriptionSummary"/>
			<xsl:copy-of select="comm:showStartCommunityToUnauthenticated"/>
		
			<xsl:comment> START COMMUNITY TYPE CONFIG </xsl:comment>
			<xsl:copy-of select="comm:communityTypes"/>
			<xsl:comment> END COMMUNITY TYPE CONFIG </xsl:comment>
		
			<xsl:comment> START THEMES CONFIG </xsl:comment>
    

		<xsl:copy-of select="comm:themes" />

		<xsl:copy-of select="comm:scheduledTasks" />

		<xsl:copy-of select="comm:eventLogCleanupTask" />
			
		<xsl:copy-of select="comm:tagCloud" />

		<xsl:copy-of select="comm:communityHandle" />

			<comm:group enabled="true">
				<comm:membershipCache  maximumAgeOnLoginInSeconds="120" maximumAgeOnRequestInSeconds="120"/>
			</comm:group>

			<comm:explicitMembershipEntityLimit>100000</comm:explicitMembershipEntityLimit>

		</comm:config>
					
	</xsl:template>	
</xsl:stylesheet>
