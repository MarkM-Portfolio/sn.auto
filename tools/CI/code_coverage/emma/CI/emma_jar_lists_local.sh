PROPERTIES_FILE=${CI_HOME}/ci.properties

if [[ ! -f "${PROPERTIES_FILE}" ]]; then
    echo "ERROR - unable to find ci properties file ${PROPERTIES_FILE}."
    exit 1
fi

. ${PROPERTIES_FILE}
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load ci properties file ${PROPERTIES_FILE}."
    exit 1
fi

# Be careful with quoting. If the jar list has wild card characters (* or ?),
# then use single quote. Otherwise, with double quotes, will have to escape those characters and I forget
# whether it will have to be multiply escaped so that the consumer of the arrays does the right thing.

#####

index=0
DIR_BUILD=${BUILD_HOME}/${SRC}/$APPDIR/build
EAR_FILE_FULLPATH=${BUILD_HOME}/${SRC}/$APPDIR/${EAR_FILE}
EAR_FILE_NAME=`basename ${EAR_FILE_FULLPATH}`
COMPONENT=$APPLICATION_DIRNAME

case $APPLICATION_DIRNAME in
	activities*)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/oa.svc/core/service/lib
		JARS_TO_INSTRUMENT[${index}]='oasvc.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/oa.was/dynacache/lib
		JARS_TO_INSTRUMENT[${index}]='oa.was.dynacache.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/oa.objectstores/filesystem/lib
		JARS_TO_INSTRUMENT[${index}]='oa.objectstores.files.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/oa.web/coreui/tmp/war/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='oa.objectstore.jar'
		index=`expr ${index} + 1`;;

	blogs)
		WAR_FILENAME=blogs.war
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_WAR}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='blogs.app.core.jar'
		index=`expr ${index} + 1`;;

	bookmarks)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/dogear.test/ut/lib
		JARS_TO_INSTRUMENT[${index}]="dogear.api.jar \
		dogear.config.jar \
		dogear.svc.jar \
		dogear.taglib.jar \
		dogear.webui.jar"
		
		index=`expr ${index} + 1`;;
	
	communities*)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/comm.api/lib
		JARS_TO_INSTRUMENT[${index}]='comm.api.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/comm.svc/lib
		JARS_TO_INSTRUMENT[${index}]='comm.svc.jdbc.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/comm.web/lib
		JARS_TO_INSTRUMENT[${index}]='comm.web.jar'
		index=`expr ${index} + 1`;;

	files)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_EAR}
		JARS_TO_INSTRUMENT[${index}]="files.spi.jar \
		files.web.jar \
		lc.core.files.spi.remote.jar \
		lc.filemanager.jar \
		lc.rest.api.jar \
		lotus.cmis.binding.jar \
		lotus.cmis.model.jar \
		share.messages.jar \
		share.platform.jar \
		share.rest.api.jar \
		share.services.jar \
		share.services.search.jar \
		share.services.widgetlifecycle.jar \
		share.util.j2ee.jar"
		
		index=`expr ${index} + 1`;;

	forums)
		WAR_FILENAME=forum.web.war
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_WAR}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='forum.*.jar'
		index=`expr ${index} + 1`;;

	homepage)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/homepage.tests/junit_jars/classpath.homepage
		JARS_TO_INSTRUMENT[${index}]="homepage.dao.layer.jar \
		homepage.jar \
		homepage.model.jar \
		homepage.service.layer.jar \
		homepage.taglib.jar \
		homepage.utils.jar \
		homepage.web.jar"
		
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/homepage.tests/junit_jars/classpath.sn.infra/lib
		JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/homepage.tests/junit_jars/classpath.sn.infra/publish/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.publish.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/dboard.sn.install/stage3
		JARS_TO_INSTRUMENT[${index}]='homepage_cd.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/homepage.web.resources/eclipse/plugins
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.homepage.web.resources_*.jar'
		index=`expr ${index} + 1`;;
				
	infra)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/base/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.base-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/cache/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.cache-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/jndi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.jndi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/jse/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.jse.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/share/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.share.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/sitemap/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.sitemap-4.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.util/web/lib
		JARS_TO_INSTRUMENT[${index}]='lc.util.web-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.search/ejbInterface/lib
		JARS_TO_INSTRUMENT[${index}]='search.searchInterface.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.search/seo/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.seo.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.seedlist/api-connections/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.seedlistAPI.connections.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.seedlist/core-connections/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.seedlist.core.connections.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.seedlist/external-api/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.externalAPI.connections.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.seedlist/framework/lib
		JARS_TO_INSTRUMENT[${index}]='lc.search.seedlist.framework.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.admin/platformCommand/consumer/lib
		JARS_TO_INSTRUMENT[${index}]='platformCommand.consumer.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.admin/userLifeCycle/spi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.admin.userlifecycle.spi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/notify/client/lib
		JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.spi/spi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.spi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.config.svc/lib
		JARS_TO_INSTRUMENT[${index}]='lc.config.svc-1.1.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.mailin/internal/lib
		JARS_TO_INSTRUMENT[${index}]='lc.mailin.internal.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.mailin/subscribe/lib
		JARS_TO_INSTRUMENT[${index}]='lc.mailin.subscribe.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events.30/async/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.async.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events.30/internal/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.internal.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events.30/publish/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.publish.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events.30/spi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.spi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events.30/subscribe/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.subscribe.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.structtag.catalog/service/lib
		JARS_TO_INSTRUMENT[${index}]='lc.structtag.catalog.service-2.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.structtag.catalog/tdi/lib
		JARS_TO_INSTRUMENT[${index}]='structtag_tdi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/api/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.api.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/consumer/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.consumer.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/ll-impl/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.llimpl.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/ll-spi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.llspi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/newsEjbConsumer/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.newsEjbConsumer.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/producer/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.producer.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.events/spi/lib
		JARS_TO_INSTRUMENT[${index}]='lc.events.spi.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/atom/client/lib
		JARS_TO_INSTRUMENT[${index}]='atom.client.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.appext/dynattr/api/lib
		JARS_TO_INSTRUMENT[${index}]='lc.appext.dynattr.api-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.appext/dynattr/impl/lib
		JARS_TO_INSTRUMENT[${index}]='lc.appext.dynattr.impl-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.appext/core/api/lib
		JARS_TO_INSTRUMENT[${index}]='lc.appext.core.api-3.0.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/lc.appext/core/impl/lib
		JARS_TO_INSTRUMENT[${index}]='lc.appext.core.impl-3.0.jar'
		index=`expr ${index} + 1`;;
	
	metrics)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_EAR}
		JARS_TO_INSTRUMENT[${index}]="lc.metrics.config.jar \
		lc.metrics.datasync.jar \
		lc.metrics.persistence.jar \
		lc.metrics.reportgeneration.jar \
		lc.metrics.scheduler.jar \
		lc.metrics.ui.jar \
		lc.metrics.writer.jar \
		lc.metrics.writer.queue.jar"
		index=`expr ${index} + 1`;;
		
	news)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.test/junit_jars/classpath.news
		JARS_TO_INSTRUMENT[${index}]="news.ejb.jar \
		news.consumer.jar \
		news.cache.jar \
		news.core.service.jar \
		news.spring.context.jar \
		news.core.data.jar \
		news.core.service.microblogging.jar \
		activitystreams.shindig.service.jar \
		activitystreams.topics.jms.jar \
		news.common.jar \
		news.notify.service.jar \
		news.notify.consumer.jar \
		activitystreams.search.core.jar \
		shindig.oauth.service.jar \
		activitystreams.mapping.service.jar \
		news.mailin.publish.jar \
		news.mailin.consumer.jar \
		news.migrate.jar \
		activitystreams.search.jfrost.jar \
		news.web.common.jar \
		news.web.core.jar \
		news.analyzer.jar \
		shindig.people.service.jar \
		"
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.test/junit_jars/classpath.sn.infra/ejb/lib
		JARS_TO_INSTRUMENT[${index}]="lconn.scheduler.ejb.jar \
		lc.following.ejb.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.test/junit_jars/classpath.sn.infra/platformCommand/consumer/lib
		JARS_TO_INSTRUMENT[${index}]="platformCommand.consumer.jar \
		"
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.test/junit_jars/classpath.sn.infra/publish/lib
		JARS_TO_INSTRUMENT[${index}]="lc.events.publish.jar \
		"
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.sn.install/stage3
		JARS_TO_INSTRUMENT[${index}]="news_cd.jar \
		"
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.sn.install/stage2/news/news
		JARS_TO_INSTRUMENT[${index}]="news.jar \
		"
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.test/junit_jars/classpath.sn.infra/async/lib
		JARS_TO_INSTRUMENT[${index}]="lc.events.async.jar \
		"
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web.resources/eclipse/plugins
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.news.digest.web.resources_*.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web.resources/eclipse/plugins
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.news.microblogging.sharebox.form_*.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/activitystreams.search.admin.web.resources/eclipse/plugins
		JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.activitystreams.search.admin.web.resources_*.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.mailin/install/lib
		JARS_TO_INSTRUMENT[${index}]="mailin.install.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web/sharebox/lib
		JARS_TO_INSTRUMENT[${index}]="news.web.sharebox.jar \
		"
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web/activitystreamsearch/lib
		JARS_TO_INSTRUMENT[${index}]="news.web.activitystreamsearch.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web/taglib/lib
		JARS_TO_INSTRUMENT[${index}]="news.taglib.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web/war/lib
		JARS_TO_INSTRUMENT[${index}]="news.web.war.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/news.web/ee/lib
		JARS_TO_INSTRUMENT[${index}]="news.web.ee.jar \
		"
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/activitystreams.search.mdb/lib
		JARS_TO_INSTRUMENT[${index}]="activitystreams.search.mdb.jar \
		"
		index=`expr ${index} + 1`;;
		
	profiles)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/profiles.core/service.api/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.core.service.api.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/profiles.core/service.impl/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.core.service.impl.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/profiles.web/core/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.core.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/profiles.web/rpfilter/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.rpfilter.jar'
		index=`expr ${index} + 1`

		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/profiles.web/app/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.app.jar'
		index=`expr ${index} + 1`;;		

#	search)
#		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.tests/junit_jars/classpath.search
#		JARS_TO_INSTRUMENT[${index}]='search*.jar'
#		index=`expr ${index} + 1`;;		
	
	search)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/seedlist/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/files/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`		
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/admin/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/data/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/index/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/service/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
	
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.base/common/lib
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.sn.install/stage3
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`	
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_BUILD}/search.sn.install/stage2/search/search
		JARS_TO_INSTRUMENT[${index}]='search*.jar'
		index=`expr ${index} + 1`;;	
	
	wikis)
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_EAR}
		JARS_TO_INSTRUMENT[${index}]='share.*.jar 
		wikis.web.jar 
		lc.util.sitemap-4.0.jar 
		org.wikimodel.wem.jar 
		lc.search.*.jar'
		
		index=`expr ${index} + 1`;;

	*) echo "Unknown application: $APPLICATION_DIRNAME";;
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
