<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright IBM Corp. 2009, 2012  All Rights Reserved.              -->

<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:comm="http://www.ibm.com/connections/communities/communities-policy/1.0"
	xsi:schemaLocation="http://www.ibm.com/connections/communities/communities-policy/1.0 communities-policy.xsd">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/comm:config/comm:policy">

		<xsl:comment>		
     ***************************************************************** 
	                                                                  
      Licensed Materials - Property of IBM                            
                                                                      
      5724-S68
	                                                                  
      Copyright IBM Corp. 2001, 2010  All Rights Reserved.            
                                                                      
      US Government Users Restricted Rights - Use, duplication or     
      disclosure restricted by GSA ADP Schedule Contract with         
      IBM Corp.                                                       
                                                                      
     ***************************************************************** 
		</xsl:comment>
				
		<comm:config id="communities-policy"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.ibm.com/connections/communities/communities-policy/1.0 communities-policy.xsd"
			xmlns:comm="http://www.ibm.com/connections/communities/communities-policy/1.0">

				<comm:policy>

    <xsl:comment> reader </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='reader']/.."/>

    <xsl:comment> community-creator </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='community-creator']/.."/>
					
    <xsl:comment> person </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='person']/.."/>
					
    <xsl:comment> personNonmember </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='personNonmember']/.."/>
    
    <xsl:comment> Community member </xsl:comment>
    <comm:grant>
      <comm:principal class="com.ibm.tango.auth.principal.Role"
                      name="member" />
	  <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='member']/../comm:permission[@action!='remove.self']"/>
      <comm:permission class="com.ibm.tango.auth.permission.CommunityMembershipPermission"
                       communityType="public"
                       action="add.self" />         
      <comm:permission class="com.ibm.tango.auth.permission.CommunityMembershipPermission"
                       communityType="publicInviteOnly"
                       action="requestToJoin" />                                     
    </comm:grant>
    	 
    <xsl:comment> Community explicit-member </xsl:comment>
    <comm:grant>
      <comm:principal class="com.ibm.tango.auth.principal.Role"
                      name="explicit-member" />
	  <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='member']/../comm:permission"/>
    </comm:grant>    

    <xsl:comment> Reference creator </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='reference.creator']/.."/>
    
    <xsl:comment> Community owner </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='owner']/.."/>
	  
	<xsl:comment> Invitee </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='invitee']/.."/>
				
	<xsl:comment> System administrator </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='system.administrator']/.."/>
    
	<xsl:comment> DSX administrator </xsl:comment>
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='dsx-admin']/.."/>

	<xsl:comment> Global Moderators </xsl:comment>
    <comm:grant>
      <comm:principal class="com.ibm.tango.auth.principal.Role"
                      name="global-moderator" />
      <comm:permission class="com.ibm.tango.auth.permission.CommunityAccessPermission"
                       communityType="*"
                       action="passive" />
    </comm:grant>
    
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@class='com.ibm.ws.security.common.auth.WSPrincipalImpl']/.."/>

    <xsl:comment> Privileges granted to anyone in the 'admin' J2EE role on the Communities application. </xsl:comment>
<xsl:if test="/comm:config/comm:policy/comm:grant/comm:principal[@name='admin']">
    <xsl:copy-of select="/comm:config/comm:policy/comm:grant/comm:principal[@name='admin']/.."/>
</xsl:if>
<xsl:if test="not(/comm:config/comm:policy/comm:grant/comm:principal[@name='admin'])">
    <comm:grant>
           <comm:principal class="com.ibm.tango.auth.principal.Role" name="admin" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityManagementPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityMembershipPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityAccessPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityReferencePermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityBroadcastPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityInvitePermission" communityType="*" action="*" />
    </comm:grant>
</xsl:if>
		
	<xsl:comment> Example of how to grant all permissions to anyone in the 'admin' J2EE role on the Communities application.
	<![CDATA[
    <comm:grant>
           <comm:principal class="com.ibm.tango.auth.principal.Role" name="admin" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityManagementPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityMembershipPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityAccessPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityReferencePermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityBroadcastPermission" communityType="*" action="*" />
           <comm:permission class="com.ibm.tango.auth.permission.CommunityInvitePermission" communityType="*" action="*" />
    </comm:grant> 
	]]> 
	</xsl:comment>
		
				</comm:policy>
		</comm:config>


	</xsl:template>	
</xsl:stylesheet>
