from cStringIO import StringIO

'''
Expects a list of entries to be added into the div. An entry is
some text and or html which will be included in the div
'''
def buildDiv(entries):
    div = StringIO()
    div.write('<div>')
    for entry in entries:
        div.write(entry)
    div.write('</div>')
    return div.getvalue()
    
'''
Expects a list of entries which will be used a items in the html list
generated. A second boolean parameter can optionally be passed to
determine is the list is ordered or not. By default it is unordered.
'''
def buildList(entries, ordered = False):
    list = StringIO()
    if ordered:
        list.write('<ol>')
    else:
        list.write('<ul>')
    
    for entry in entries:
        list.write('<li>')
        list.write(entry)
        list.write('</li>')
    
    if ordered:
        list.write('</ol>')
    else:
        list.write('</ul>')
    return list.getvalue()
    
'''
Used specifically to build table for create request
'''
def buildTable(entries, ordered = False):

    list = StringIO()

    for entry in entries:
        list.write('<tr><td><input type="checkbox" name="tests" value="'+entry+'"></td><td class="test">')
        list.write(entry)
        list.write('</td></tr>\n\t\t\t\t\t\t')
   
    return list.getvalue()


def buildLink(url, text):
    return '<a href="' + url + '">' + text + '</a>'