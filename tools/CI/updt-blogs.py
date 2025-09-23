import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

lcapp.set_wsadmin_refs((AdminConfig, AdminControl, AdminTask, AdminApp))


def set_was_variables(vars):
    cell = lcapp.Cell()
    for k,v in vars.items():
        wv = lcapp.WebSphereVariable(k, v)
        wv.parent = cell
        wv.set()

set_was_variables({'ENABLE_BLOGS_UNITTEST': 'true'})

app = lcapp.Application("Blogs")

app.update(applicationDirectory + "/blogs.ear")

app.addFile(applicationDirectory + "/blogs.test.jar",
    "blogs.war/WEB-INF/lib/blogs.test.jar")

AdminConfig.save()

app.restart()

