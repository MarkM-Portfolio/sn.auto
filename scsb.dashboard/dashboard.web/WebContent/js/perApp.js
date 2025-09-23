// load rave build layer
require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", "dojo/query","dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", "dojo/_base/event"], function(ready, template, parser, registry, lang, on, request,query) {
		var masterAppList = ["Activities",
		                     "Blogs",
		                     "BSS",
		                     "Communities",
		                     "Contacts",
		                     "Files",
		                     "Forums",
		                     "Homepage",
		                     "Meetings",
		                     "News",
		                     "Sametime",
		                     "Search",
		                     "Traveler",
		                     "Wikis",
		                     "Other"];
		var numApps=masterAppList.length;
		var data={};
		var appTabsString ='<li class="active"><a href="#'+masterAppList[0].toLowerCase()+'" data-toggle="tab">'+masterAppList[0]+'</a></li>';
		var tabContentString='<div class="tab-pane active" id="'.concat(masterAppList[0].toLowerCase(),
				'"><div class="form-group"><button class="btn btn-primary" onclick = "getApp(',
				'0',')">Generate ',masterAppList[0],' Graph</button></div></div>');
		for(var i=1;i<numApps;i++){
			var temp1 ='<li><a href="#'+masterAppList[i].toLowerCase()+'" data-toggle="tab">'+masterAppList[i]+'</a></li>';
			var temp2 = '<div class="tab-pane" id="'
				+masterAppList[i].toLowerCase()
				+'"><div class="form-group"><button class="btn btn-primary" onclick="getApp('
				+i+')">Generate '+masterAppList[i]+' Graph</button></div></div>';
			tabContentString=tabContentString.concat(temp2);
			appTabsString=appTabsString.concat(temp1);
		}
		document.getElementById("app-tabs").innerHTML=appTabsString;
		document.getElementById("app-tabs-content").innerHTML=tabContentString;
		getApp= function(app){
			var startTime = Date.UTC(2014,05,12)/1000;//months are 0 indexed
			var endTime = Date.UTC(2014,05,13)/1000;
			var requestInfo ={"startTime":startTime.toString()};
			requestInfo["endTime"]=endTime.toString();
			requestInfo["app"]=app;
			requestInfo["queryType"]="perApp";
			request("TestServlet",{
				method: "POST",
				data: requestInfo
			})
				.then(function(gotData){
					data[app]=JSON.parse(gotData);
					console.log(JSON.stringify(data[app]));
				});
		};
	});
});