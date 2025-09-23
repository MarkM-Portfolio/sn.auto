AdminApp.update("Metrics", "app",
    [ '-operation', 'update', '-contents', "/local/ci/metrics/src/sn.metrics/lwp/build/lc.metrics.ear/lib/metrics.ear" ])
AdminConfig.save()
