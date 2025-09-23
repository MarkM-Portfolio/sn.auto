# Be careful with quoting. If the jar list has wild card characters (* or ?),
# then use single quote. Otherwise, with double quotes, will have to escape those characters and I forget
# whether it will have to be multiply escaped so that the consumer of the arrays does the right thing.

# Common EAR dir
DIR_EAR_COMMON=${DIR_EARS}/Common.ear

# Web Resources dir
DIR_WEBRESOURCES=${REMOTE_LC_DATA_DIR}/provision/webresources

# Activities dirs
DIR_EAR_ACTIVITIES=${DIR_EARS}/Activities.ear
DIR_WAR_ACTIVITIES=${DIR_EAR_ACTIVITIES}/oawebui.war/WEB-INF/lib

# Blogs dirs
DIR_EAR_BLOGS=${DIR_EARS}/Blogs.ear
DIR_WAR_BLOGS=${DIR_EAR_BLOGS}/blogs.war/WEB-INF/lib

# Communities dirs
DIR_EAR_COMMUNITIES=${DIR_EARS}/Communities.ear
DIR_WAR_COMMUNITIES=${DIR_EAR_COMMUNITIES}/comm.web.war/WEB-INF/lib

# Dogear dirs
DIR_EAR_DOGEAR=${DIR_EARS}/Dogear.ear
DIR_WAR_DOGEAR=${DIR_EAR_DOGEAR}/dogear.webui.war/WEB-INF/lib
DIR_WAR_BOOKMARKLET=${DIR_EAR_COMMON}/lc-bookmarklet.war/WEB-INF/lib

# Files dirs
DIR_EAR_FILES=${DIR_EARS}/Files.ear

# Forums dirs
DIR_EAR_FORUMS=${DIR_EARS}/Forums.ear
DIR_WAR_FORUMS=${DIR_EAR_FORUMS}/forum.web.war/WEB-INF/lib

# Homepage dirs
DIR_EAR_HOMEPAGE=${DIR_EARS}/Homepage.ear
DIR_WAR_HOMEPAGE=${DIR_EAR_HOMEPAGE}/homepage.war/WEB-INF/lib

# Metrics dirs
DIR_EAR_METRICS=${DIR_EARS}/Metrics.ear

# Mobile dirs
DIR_EAR_MOBILE=${DIR_EARS}/Mobile.ear
DIR_WAR_MOBILE=${DIR_EAR_MOBILE}/mobile.web.war/WEB-INF/lib

# Moderation dirs
DIR_EAR_MODERATION=${DIR_EARS}/Moderation.ear

# News dirs
DIR_EAR_NEWS=${DIR_EARS}/News.ear
DIR_WAR_NEWS=${DIR_EAR_NEWS}/news.web.war/WEB-INF/lib

# Profiles dirs
DIR_EAR_PROFILES=${DIR_EARS}/Profiles.ear
DIR_WAR_PROFILES=${DIR_EAR_PROFILES}/lc.profiles.app.war/WEB-INF/lib

# Search dirs
DIR_EAR_SEARCH=${DIR_EARS}/Search.ear
DIR_WAR_SEARCH=${DIR_EAR_SEARCH}/search.war/WEB-INF/lib

# WidgetContainer dirs
DIR_EAR_WIDGETCONTAINER=${DIR_EARS}/WidgetContainer.ear
DIR_WAR_WIDGETCONTAINER=${DIR_EAR_WIDGETCONTAINER}/lc.shindig.serverapi.war/WEB-INF/lib

# Wikis dirs
DIR_EAR_WIKIS=${DIR_EARS}/Wikis.ear

#####
index=0
# Activities jars
COMPONENT[${index}]='Activities'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_ACTIVITIES}
JARS_TO_INSTRUMENT[${index}]="oa.atom.client.jar \
oa.atom.common.jar \
oa.atom.parser.jar \
oa.atom.serializer.jar \
oa.objectstore.jar \
oa.objectstores.files.jar \
oa.was.dynacache.jar \
oaAPI.jar \
oasvc.jar \
oataglib.jar \
oawebui.jar"

index=`expr ${index} + 1`

# ActivityStreamUI jars
COMPONENT[${index}]='ActivityStreamUI'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='lc.services.gadgets.osapiclient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='ActivityStreamUI'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='lc.services.gadgets.osapiclient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='ActivityStreamUI'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMUNITIES}
JARS_TO_INSTRUMENT[${index}]='lc.services.gadgets.osapiclient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='ActivityStreamUI'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMON}/connections.web.resources.war/WEB-INF/eclipse/plugins
JARS_TO_INSTRUMENT[${index}]='com.ibm.social.as.lconn.web.resources_1.0.0.*.jar com.ibm.social.as.web.resources_1.0.0.*.jar'
index=`expr ${index} + 1`

# Blogs jars
COMPONENT[${index}]='Blogs'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_BLOGS}
JARS_TO_INSTRUMENT[${index}]='blogs.app.core.jar'
index=`expr ${index} + 1`

# Communities jars
COMPONENT[${index}]='Communities'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_COMMUNITIES}
JARS_TO_INSTRUMENT[${index}]='comm.api.jar comm.eventlog.jar comm.svc.jar comm.web.jar'
index=`expr ${index} + 1`

# Dogear jars
COMPONENT[${index}]='Dogear'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_DOGEAR}
JARS_TO_INSTRUMENT[${index}]='dogear.*.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Dogear'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMON}/lc-bookmarklet.war/WEB-INF/lib
JARS_TO_INSTRUMENT[${index}]='lc-bookmarklet.jar'
index=`expr ${index} + 1`

# Files jars
COMPONENT[${index}]='Files'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FILES}
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

index=`expr ${index} + 1`

# Forums jars
COMPONENT[${index}]='Forums'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_FORUMS}
JARS_TO_INSTRUMENT[${index}]='forum.*.jar'
index=`expr ${index} + 1`

# Homepage jars
COMPONENT[${index}]='Homepage'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='homepage.*.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Homepage'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEBRESOURCES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.homepage.web.resources_1.0.0.*.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Homepage'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='lc.events.publish.jar 
notify.client.jar'
index=`expr ${index} + 1`

# Linked Library/Media Gallery jars
COMPONENT[${index}]='LinkedLibrary-MediaGallery'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEBRESOURCES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.librarywidget.web.resources_*.jar'
index=`expr ${index} + 1`

# Mail-In jars
COMPONENT[${index}]='Mail-In'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FORUMS}
JARS_TO_INSTRUMENT[${index}]='lc.mailin.subscribe.jar lc.mailin.internal.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Mail-In'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='news.mailin.publish.jar lc.mailin.internal.jar news.mailin.consumer.jar'
index=`expr ${index} + 1`

# Metrics jars
COMPONENT[${index}]='Metrics'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_METRICS}
JARS_TO_INSTRUMENT[${index}]="lc.metrics.common.jar \
lc.metrics.config.jar \
lc.metrics.datasync.jar \
lc.metrics.persistence.jar \
lc.metrics.reportgeneration.jar \
lc.metrics.scheduler.jar \
lc.metrics.ui.jar \
lc.metrics.writer.jar \
lc.metrics.writer.queue.jar"

index=`expr ${index} + 1`

#Mobile jars
COMPONENT[${index}]='Mobile'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_MOBILE}
JARS_TO_INSTRUMENT[${index}]='mobile*.jar'
index=`expr ${index} + 1`

# News (ActivityStream and Microblogging) jars
COMPONENT[${index}]='News'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='news.*.jar 
lconn.scheduler.ejb.jar 
platformCommand.consumer.jar 
lc.events.publish.jar 
activitystreams.shindig.service.jar 
activitystreams.topics.jms.jar 
lc.events.async.jar 
activitystreams.search.core.jar 
shindig.oauth.service.jar 
lc.following.ejb.jar 
activitystreams.mapping.service.jar 
activitystreams.search.jfrost.jar 
shindig.people.service.jar 
activitystreams.search.mdb.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='News'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='news.web.war.jar' 
index=`expr ${index} + 1`

COMPONENT[${index}]='News'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WEBRESOURCES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.lconn.news.digest.web.resources_*.jar 
com.ibm.lconn.news.microblogging.sharebox.form_*.jar 
com.ibm.lconn.activitystreams.search.admin.web.resources_*.jar'
index=`expr ${index} + 1`

# Notifications jars
COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_DOGEAR}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FORUMS}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_MODERATION}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_ACTIVITIES}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_BLOGS}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FILES}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMUNITIES}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_SEARCH}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_WIKIS}
JARS_TO_INSTRUMENT[${index}]='notify.client.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Notifications'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='news.notify.service.jar news.notify.consumer.jar'
index=`expr ${index} + 1`

# Profiles jars
COMPONENT[${index}]='Profiles'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='lc.profiles.core.service.*.jar lc.profiles.web.*.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Profiles'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.app.jar'
index=`expr ${index} + 1`

# Search jars
COMPONENT[${index}]='Search'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_SEARCH}
JARS_TO_INSTRUMENT[${index}]="dboard.search.ejb.jar \
lc.search.seedlist.framework.jar \
lc.search.terms.extraction.jar \
search.admin.jar \
search.common.jar \
search.config.jar \
search.crawler.jar \
search.data.jar \
search.files.jar \
search.indexhandler.jar \
search.index.jar \
search.jfrost.jar \
search.lucene.jar \
search.process.jar \
search.registries.jar \
search.sand.backend.jar \
search.sand.frontend.jar \
search.sand.search.jar \
search.searchInterface.jar \
search.search.jar \
search.seedlistiterator.jar \
search.seedlist.jar \
search.service.jar \
search.task.jar"

index=`expr ${index} + 1`

COMPONENT[${index}]='Search'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_SEARCH}
JARS_TO_INSTRUMENT[${index}]='*.jar'
index=`expr ${index} + 1`

# Sonata (S2S SSO/AuthN) jars
COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_MODERATION}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_MOBILE}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMUNITIES}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_WIKIS}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_BLOGS}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FILES}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_METRICS}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_SEARCH}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_ACTIVITIES}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FORUMS}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_DOGEAR}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Sonata'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMON}
JARS_TO_INSTRUMENT[${index}]='lc.customAuthClient.jar'
index=`expr ${index} + 1`

# Waltz (Directory Services) jars
COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_MODERATION}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_MOBILE}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMUNITIES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_WIKIS}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_BLOGS}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FILES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_METRICS}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_HOMEPAGE}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_SEARCH}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_PROFILES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_NEWS}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_ACTIVITIES}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_FORUMS}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

COMPONENT[${index}]='Waltz'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_DOGEAR}
JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
index=`expr ${index} + 1`

#COMPONENT[${index}]='Waltz'
#DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMON}/connections.web.resources.war/WEB-INF/eclipse/plugins/com.ibm.lconn.core.web_3.0.0.20120308-2020/lib
#JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
#index=`expr ${index} + 1`

#COMPONENT[${index}]='Waltz'
#DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_COMMON}/lc.oauth.provider.web.war/WEB-INF/lib
#JARS_TO_INSTRUMENT[${index}]='com.ibm.connections.directory.services.jar'
#index=`expr ${index} + 1`

# WidgetContainer jars
COMPONENT[${index}]='WidgetContainer'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_WAR_WIDGETCONTAINER}
JARS_TO_INSTRUMENT[${index}]='com.ibm.cre.features_*.jar 
com.ibm.cre.proxy.framework_*.jar 
com.ibm.cre.proxy.framework.crypto_*.jar 
com.ibm.cre.server.dynacache_*.jar 
com.ibm.cre.server.framework_*.jar 
com.ibm.cre.server.proxy_*.jar 
com.ibm.cre.server.simplecache_*.jar 
com.ibm.cre.shindig.extension_*.jar 
com.ibm.lconn.cre.jsfeatures.jar 
com.ibm.cre.shindig.extension_*.jar 
lc.shindig.serverapi.core.jar 
lc.shindig.serverapi.extension.jar 
lc.shindig.serverapi.remote.jar 
lc.shindig.serverapi.war.repackage.jar 
shindig-common-*.jar 
shindig-extras-*.jar 
shindig-features-*.jar 
shindig-gadgets-*.jar 
shindig-sample-container-*.jar 
shindig-social-api-*.jar'

index=`expr ${index} + 1`

# Wikis jars
COMPONENT[${index}]='Wikis'
DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EAR_WIKIS}
JARS_TO_INSTRUMENT[${index}]='share.*.jar 
wikis.web.jar 
lc.util.sitemap-4.0.jar 
org.wikimodel.wem.jar 
lc.search.*.jar'

index=`expr ${index} + 1`

NUM_DIRS_WITH_JARS_TO_INSTRUMENT=${#DIR_WITH_JARS_TO_INSTRUMENT[*]}
echo "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	echo "COMPONENT[${index}]: ${COMPONENT[${index}]}"
	echo "DIR_WITH_JARS_TO_INSTRUMENT[${index}]: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	echo "JARS_TO_INSTRUMENT[${index}]: ${JARS_TO_INSTRUMENT[${index}]}"
	index=`expr ${index} + 1`
done
