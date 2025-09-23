AdminApp.update("Activities", "app",
    [ '-operation', 'update', '-contents', "/local/ci/activities/src/activities.impl/lwp/build/oa.ear/ear.prod/lib/oa.ear" ])
AdminConfig.save()
