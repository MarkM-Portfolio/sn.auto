

# Make sure jar arrays are empty to start.
unset DIR_WITH_JARS_TO_INSTRUMENT
unset JARS_TO_INSTRUMENT 

# Be careful with quoting. If the jar list has wild card characters (* or ?),
# then use single quote. Otherwise, with double quotes, will have to escape those characters and I forget
# whether it will have to be multiply escaped so that the consumer of the arrays does the right thing.

index=0

case ${APP} in		
	
	Activities)
		EAR_FILE_NAME=oa.ear
		WAR_FILE_NAME=oawebui.war

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.atom.client.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.atom.common.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.atom.parser.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.atom.serializer.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.objectstore.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.objectstores.files.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.was.dynacache.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oaAPI.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oasvc.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oataglib.jar'
		index=`expr ${index} + 1`
				
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oawebui.jar'
		index=`expr ${index} + 1`
		;;
	
	Blogs)		
		EAR_FILE_NAME=blogs.ear
		WAR_FILE_NAME=blogs.war

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='blogs.app.core.jar'
		index=`expr ${index} + 1`
		;;

	Calendar)		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='calendar.app.core.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.seedlist.retriever.calendar.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.calendar.web.resources_*.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.dwa.web.resources_*.jar'
		index=`expr ${index} + 1`
		;;

	Communities)
		EAR_FILE_NAME=communities.ear
		WAR_FILE_NAME=comm.web.war
       	DIR_WEB_RESOURCES=communities.provision.web

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='comm.api.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='comm.eventlog.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='comm.svc.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='comm.web.jar'
		index=`expr ${index} + 1`

        DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
        JARS_TO_INSTRUMENT[${index}]='catalog.search.engine.jar'
        index=`expr ${index} + 1`
  
        DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
        JARS_TO_INSTRUMENT[${index}]='catalog.search.mdb.jar'
        index=`expr ${index} + 1`
  
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
        JARS_TO_INSTRUMENT[${index}]='catalog.utils.jar'
        index=`expr ${index} + 1`
  
        DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
        JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.communities.catalog.web.resources_*.jar'
        index=`expr ${index} + 1`
        ;;

	Dogear)
		EAR_FILE_NAME=dogear.ear
		WAR_FILE_NAME=dogear.webui.war

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='dogear.*.jar'
		index=`expr ${index} + 1`
		;;

	Dogear_Infra)
		EAR_FILE_NAME=connections.common.ear
		WAR_FILE_NAME=lc-bookmarklet.war
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='lc-bookmarklet.jar'
		index=`expr ${index} + 1`
		;;
	
	ExtensionsRegistry)
		EAR_FILE_NAME=scee.ear
		WAR_FILE_NAME=scee.war
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='scee.jar'
		index=`expr ${index} + 1`
		;;
	
	Files)
		EAR_FILE_NAME=files.ear

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='files.spi.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='files.web.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.core.files.spi.remote.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.filemanager.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.rest.api.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lotus.cmis.binding.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lotus.cmis.model.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.messages.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.platform.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.rest.api.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.services.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.services.search.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.services.widgetlifecycle.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.util.j2ee.jar'
		index=`expr ${index} + 1`
		;;
	
	Forums)
		EAR_FILE_NAME=forums.ear
		WAR_FILE_NAME=forum.web.war

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]="${DIR_WAR_EXTRACTED}/WEB-INF/lib"
		JARS_TO_INSTRUMENT[${index}]='forum*.jar'
		index=`expr ${index} + 1`
		;;

	Homepage)
		EAR_FILE_NAME=dboard.ear
		WAR_FILE_NAME=homepage.war
		DIR_WEB_RESOURCES=homepage.provision.web
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='homepage.*.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.homepage.web.resources_1.0.0.*.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.events.publish.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
		index=`expr ${index} + 1`
		;;

	News)
		EAR_FILE_NAME=news.ear
		WAR_FILE_NAME=news.web.war
		DIR_WEB_RESOURCES=news.provision.web

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='news.*.jar' 
		index=`expr ${index} + 1`
			
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lconn.scheduler.ejb.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='platformCommand.consumer.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.events.publish.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.shindig.service.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.topics.jms.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.events.async.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.search.core.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='shindig.oauth.service.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.following.ejb.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.mapping.service.jar' 
		index=`expr ${index} + 1`
				 
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.search.jfrost.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='shindig.people.service.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='activitystreams.search.mdb.jar' 
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='news.web.war.jar' 
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.news.digest.web.resources_*.jar' 
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.news.microblogging.sharebox.form_*.jar' 
		index=`expr ${index} + 1`
		;;

	News_Infra)
		DIR_WEB_RESOURCES=common.provision.web
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEB_RESOURCES_INSTRUMENTED}
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.activitystreams.search.admin.web.resources_*.jar' 
		index=`expr ${index} + 1`
		;;
		
	Profiles)
		EAR_FILE_NAME=profiles.ear
		WAR_FILE_NAME=lc.profiles.app.war
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]="${DIR_WAR_EXTRACTED}/WEB-INF/lib"
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.app.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]="${DIR_EAR_EXTRACTED}"
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.core.service.*.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]="${DIR_EAR_EXTRACTED}"
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.*.jar'
		index=`expr ${index} + 1`
		;;

	RichTextEditors)
		EAR_FILE_NAME=rte.ear
		WAR_FILE_NAME=rte-web.war
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='spring-social-connections.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='rte-web.jar'
		index=`expr ${index} + 1`
		;;	

	Search)
		EAR_FILE_NAME=search.ear
		WAR_FILE_NAME=search.war

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='dboard.search.ejb.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.search.seedlist.framework.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.search.terms.extraction.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='search.*.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_EXTRACTED}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='*.jar'
		index=`expr ${index} + 1`
		;;

	Wikis)
		EAR_FILE_NAME=wikis.ear

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='share.*.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='wikis.web.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.util.sitemap-4.0.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='org.wikimodel.wem.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_EXTRACTED}
		JARS_TO_INSTRUMENT[${index}]='lc.search.*.jar'
		index=`expr ${index} + 1`
		;;

	*) echo "Unknown component: ${APP}";;
esac

NUM_DIRS_WITH_JARS_TO_INSTRUMENT=${#DIR_WITH_JARS_TO_INSTRUMENT[*]}
echo "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	echo "DIR_WITH_JARS_TO_INSTRUMENT[${index}]: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	echo "JARS_TO_INSTRUMENT[${index}]: ${JARS_TO_INSTRUMENT[${index}]}"
	index=`expr ${index} + 1`
done
