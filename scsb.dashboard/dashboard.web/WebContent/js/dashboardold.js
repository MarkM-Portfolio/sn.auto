//load rave build layer
require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", "dojo/query", "dojo/dom","dojo/dom-construct", "dojo/dom-class","dojo/dom-attr","dijit/WidgetSet","dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", "dojo/_base/event"], function(ready, template, parser, registry, lang, on, request, query, dom, domConstruct, domClass,domAttr, WidgetSet) {
		//ready function is needed to ensure the rave system is properly loaded
		//master variables
		masterAppList = [["Activities",
		                 "Blogs",
		                 "BSS",
		                 "Communities",
		                 "Contacts",
		                 "Docs",
		                 "Files",
		                 "Forums",
		                 "Help",
		                 "Homepage",
		                 "Meetings",
		                 "News",
		                 "Sametime",
		                 "Search",
		                 "Surveys",
		                 "Traveler",
		                 "Wikis",
		                 "Other"],[]];
		masterImageList=[["images/ActivitiesWhite32.png",
		                  "images/BlogsWhite32.png",
		                  null,
		                  "images/CommunitiesWhite32.png",
		                  "images/ProfilesWhite32.png",
		                  "images/DocsWhite32.png",
		                  "images/FilesWhite32.png",
		                  "images/ForumsWhite32.png",
		                  null,
		                  "images/HomeWhite32.png",
		                  null,
		                  "images/NewsFeedWhite32.png",
		                  null,
		                  null,
		                  null,
		                  null,
		                  "images/WikisWhite32.png",
		                  null],
		                  ["images/ActivitiesWhite24.png",
		                   "images/BlogsWhite24.png",
		                   null,
		                   "images/CommunitiesWhite24.png",
		                   "images/ProfilesWhite24.png",
		                   "images/DocsWhite24.png",
		                   "images/FilesWhite24.png",
		                   "images/ForumsWhite24.png",
		                   null,
		                   "images/HomeWhite24.png",
		                   null,
		                   "images/NewsFeedWhite24.png",
		                   null,
		                   null,
		                   null,
		                   null,
		                   "images/WikisWhite24.png",
		                   null]];
		refererList =["Activities",
		              "Blogs",
		              "BSS",
		              "Communities",
		              "Contacts",
		              "Docs",
		              "Files",
		              "Forums",
		              "Help",
		              "Homepage",
		              "Meetings",
		              "News",
		              "Sametime",
		              "Search",
		              "Surveys",
		              "Traveler",
		              "Wikis",
		              "Other",
		              "External"];
		numApps = masterAppList[0].length;
		palette =["#82D1F5",
		          "#008ABF",
		          "#00648D",
		          "#007670",
		          "#17AF4B",
		          "#8CC63F",
		          "#A5A215",
		          "#594F13",
		          "#FFE14F",
		          "#FFCF01",
		          "#F19027",
		          "#B8461B",
		          "#F04E37",
		          "#A91024",
		          "#F389AF",
		          "#EE3D96",
		          "#AB1A86",
		          "#3B0256"];
		
		//visJSON variables
		//Dashboard Graphs
		var visDashArea={};
		var visDashSpeed={};
		var visDashPie={};
		var visDashNested={};
		//General Graphs
		var visArea={};
		var visLine={};
		var visPie={};
		//Other Graphs
		var visAgents={};
		var visChord={};
		var visNestedAgents={};
		var visNestedOS={};
		var visFancyPie={};
		var visApp = new Array(numApps);
		var visScatter ={};
		var visSpeed={};
		//NodeList of rave doms
		var visDoms;
		//WidgetSet of rave widgets
		var visWidgets = new WidgetSet();

		var linkHolder = new Array(numApps);
		var topLinks = query("#top-menu > li");
		//internal variables
		var range = false;
		var received = false;
		var visLoaded = 0;
		var active="dashboard";
		//parameters
		var tooltips = true;
		var unit="hour";
		var startTime=0; //seconds since epoch
		var endTime=0; //seconds since epoch
		var visEndTime=0; //Used for end of scale
		var graphType = "area"; //general graph type
		var unique = false; //unique vs non-unique
		var apps = new Array(numApps); //array for which apps are being shown, stores the indices, used for general graphs only
		for(var i=0;i<numApps;i++){
			apps[i]=i;
		}
		var order = new Array(numApps);//to be added to app field on area graph
		for(var i=0;i<numApps;i++){
			order[i]=numApps-i-1;
		}

		//data variables
		//dashboardData
		var dashAreaData=[];
		var timeList=[];
		var dashSpeedData=[];
		var dashNestedAgents=[];
		var dashNestedParents=[];
		var dashNestedData=[];
		var dashPieData=[];

		//generalData
		var pieRows=[]; //data rows for general pie chart
		var data=[]; //holds data taken from general query
		var rows=[]; //data rows for line and area
		var chordData=[]; //data rows for chord
		var agents=[];
		var agentData=[];
		var nestedAgents=[];
		var nestedAgentParents=[];
		var nestedAgentData=[];
		var nestedOS=[];
		var nestedOSParents=[];
		var nestedOSData=[];
		var perAppData= new Array(numApps);
		var perAppCats = new Array(numApps);
		var scatterData=[];
		var speedData=[];

		var graphRadios=query('[name="graphOption"]');
		//initializing items:
		$('.btn').button();
		//datepicker defaults
		$.fn.datepicker.defaults.format = "mm/dd/yyyy";
		$.fn.datepicker.defaults.endDate = "today";
		$.fn.datepicker.defaults.autoclose = "true";
		$.fn.datepicker.defaults.weekStart = "1";

		//load hour as default
		createDatePicker();
		$("#dayPicker").datepicker("update","-1d");//yesterday as default




		//load visJSON files
		request("json/sampleVitalsVisJSON.json")
		.then(function(data){
			visLoaded++;
			visArea=JSON.parse(data);
			visArea.data[0].fields[0].categories=masterAppList[0];
			visArea.data[0].fields[0].order=order;
			visArea.grammar[0].elements[0].color[0].palette=palette;
			visDashArea=JSON.parse(data);
			visDashArea.data[0].fields[0].categories=masterAppList[0];
			visDashArea.data[0].fields[0].order=order;
			visDashArea.grammar[0].elements[0].color[0].palette=palette;
			//console.log(visArea);
			changeVisUnit();
		});
		request("json/vitalsPie.json")
		.then(function(data){
			visPie=JSON.parse(data);
			visPie.data[0].fields[0].categories=masterAppList[0];
			visPie.grammar[0].elements[0].color[0].palette=palette;
			visDashPie=JSON.parse(data);
			visDashPie.data[0].fields[0].categories=masterAppList[0];
			visDashPie.grammar[0].elements[0].color[0].palette=palette;
		});
		request("json/vitalsAppBubble.json")
		.then(function(data){
			visAgents=JSON.parse(data);
			visAgents.grammar[0].elements[0].color[0].palette=palette;
			for(var i=0;i<numApps;i++){
				if(i!=6){
					visApp[i]=JSON.parse(data);
					visApp[i].grammar[0].elements[0].color[0].palette=palette;
				}
			}
		});
		request("json/vitalsLayeredPie.json")
		.then(function(data){
			visNestedAgents=JSON.parse(data);
			//console.log(visNestedAgents);
			visNestedOS=JSON.parse(data);
			visNestedAgents.grammar[0].elements[0].color[0].palette=palette;
			visNestedOS.grammar[0].elements[0].color[0].palette=palette;
			visDashNested=JSON.parse(data);
			visDashNested.grammar[0].elements[0].color[0].palette=palette;
			visApp[6]=JSON.parse(data);
			visApp[6].grammar[0].elements[0].color[0].palette=palette;
		});
		request("json/vitalsLine.json")
		.then(function(data){
			visLoaded++;
			visLine=JSON.parse(data);
			visLine.data[0].fields[0].categories=masterAppList[0];
			visLine.grammar[0].elements[0].color[0].palette=palette;
			changeVisUnit();
		});
		request("json/vitalsChord.json")
		.then(function(data){
			visChord=JSON.parse(data);
			visChord.data[0].fields[0].categories=refererList;
			visChord.data[0].fields[1].categories=masterAppList[0];
			visChord.grammar[0].elements[0].color[0].palette=palette;
		});
		request("json/vitalsFancyTotalPie.json")
		.then(function(data){
			visFancyPie=JSON.parse(data);
			visFancyPie.data[0].fields[0].categories=masterAppList[0];
			visFancyPie.grammar[0].elements[0].color[0].palette=palette;
		});
		request("json/vitalsScatter.json")
		.then(function(data){
			visScatter=JSON.parse(data);
		});
		request("json/vitalsSpeed.json")
		.then(function(data){
			visSpeed=JSON.parse(data);
			visDashSpeed=JSON.parse(data);
			changeVisUnit();
		});
		//loading appboxes
		var appBoxRow=domConstruct.create("div",{class:"row"},"appBoxes");
		var col1 = domConstruct.create("div",{class:"col-lg-6"},appBoxRow);//first column
		var col2 = domConstruct.create("div",{class:"col-lg-6"},appBoxRow);//second column
		for(var i=0;i<numApps/2;i++){
			var tempDiv=domConstruct.create("div",{class:"checkbox myDiv"},col1);
			var tempLabel=domConstruct.create("label",{id:"app"+i,style:{background:palette[i],color:"white"},
				class:"panel myBox",innerHTML:masterAppList[0][i]},tempDiv);
			domConstruct.create("input",{name:"box",type:"checkbox",checked:true,
				onclick:"updateApps()"},tempLabel,"first");
		}
		for(var i=Math.ceil(numApps/2);i<numApps;i++){
			var tempDiv=domConstruct.create("div",{class:"checkbox myDiv"},col2);
			var tempLabel=domConstruct.create("label",{id:"app"+i,style:{background:palette[i],color:"white"},
				class:"panel myBox",innerHTML:masterAppList[0][i]},tempDiv);
			domConstruct.create("input",{name:"box",type:"checkbox",checked:true,
				onclick:"updateApps()"},tempLabel,"first");
		}
		appBoxes=query("[name=box]");

		loadAppTabs = function(){
			for(var i=0; i<numApps; i++){
				var tempLi =domConstruct.create("li",null,"app-menu");
				var tempTab =domConstruct.create("a",{href:"#"+masterAppList[0][i].toLowerCase()},tempLi);
				if(masterImageList[1][i]!=null)
					domConstruct.create("img",{src:masterImageList[1][i]},tempTab);
				else
					tempTab.innerHTML=masterAppList[0][i];
				domAttr.set(tempTab,"data-toggle","pill");
				var tempPane = domConstruct.create("div",{class:"tab-pane",id:masterAppList[0][i].toLowerCase()},"app-tabs-content");
				var tempRow = domConstruct.create("div",{class:"row"},tempPane);
				var tempCol = domConstruct.create("div",{class:"col-lg-12"},tempRow);
				//var tempDiv = domConstruct.create("div",{class:"page-header"},tempCol);
				domConstruct.create("h1",{innerHTML:masterAppList[0][i]},tempCol);
				tempRow = domConstruct.create("div",{class:"row"},tempPane);
				tempCol = domConstruct.create("div",{class:"col-lg-6"},tempRow);
				domConstruct.create("div",{"data-dojo-type":"com.ibm.vis.widget.VisControl",
					id:"visControlApp"+i,style:{width:"100%",height:"600px"}},tempCol);
				//tempP = domConstruct.create("p",{innerHTML:"View "+masterAppList[1][i]+": "},tempCol);
				//linkHolders[i]=domConstruct.create("a")
			}
		};
		loadAppTabs();
		var sideLinks = query("#app-menu > li");
		visDoms = query("[data-dojo-type=com.ibm.vis.widget.VisControl]");
		readyVis = function(id){
			ready(function(){
				parser.parse();
				var widget=registry.byId(id);
				widget.resizeToWindow=true;
				on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
				on( widget, "click", lang.hitch(this,"visOnClick"));
				visWidgets.add(widget);
				if(id.indexOf("visDash")>=0)
					widget.set("active",true);
			});

		};
		visDoms.forEach(function(node){
			readyVis(node.id);		
		});
		$('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
			active=e.target.href.substring(e.target.href.indexOf("#")+1);
			setActiveTab();
			setActiveVis();
			visWidgets.forEach(function(widget){
				widget.resize(); 
			});
		});
		setActiveTab=function(){
			if(active=="general"||active=="dashboard"||active=="more"){
				sideLinks.forEach(function(node){
					domClass.remove(node,"active");
				});	
			}
			else{
				topLinks.forEach(function(node){
					domClass.remove(node,"active");
				});
			}
		};
		setActiveVis=function(){
			visWidgets.forEach(function(widget){
				widget.set("active",false);
			});
			if(active=="general"){
				var widget=registry.byId("visControlGeneral1");
				widget.set("active",true);
			}
			else if(active=="more"){
				var widget=registry.byId("visControlChord");
				widget.set("active",true);
				widget=registry.byId("visControlAgent");
				widget.set("active",true);
				widget=registry.byId("visControlFancy");
				widget.set("active",true);
				widget=registry.byId("visControlNestedOS");
				widget.set("active",true);
				widget=registry.byId("visControlScatter");
				widget.set("active",true);
			}
			else if(active=="dashboard"){

			}
			else{
				var curApp;
				if(active=="bss"){
					curApp = masterAppList[0].indexOf("BSS");
				}
				else{
					var temp = active.charAt(0).toUpperCase();
					temp = temp.concat(active.substring(1));
					curApp = masterAppList[0].indexOf(temp);
				}
				console.log(curApp);
				var widget=registry.byId("visControlApp"+curApp);
				widget.set("active",true);
			}


		};
		//console.log(visWidgets);
		//functions for interaction with the graph
		visOnClick = function(dojoEvent) {
			var visControl=registry.byId("visControlGeneral1");
			var interaction = visControl.getInteractivity();
			//var item = interaction.getMetaItem(dojoEvent.clientX,dojoEvent.clientY);
			//meta doesn't work when using order array
			var item = interaction.getTooltipItem(dojoEvent.pageX,dojoEvent.pageY);
			if(item!=null){
				if(apps.length==1)
					selectAll();
				else{
					//var curApp = item.meta().substring(7);
					var curApp = item.tooltipText();
					//console.log(curApp);
					var appId = masterAppList[0].indexOf(curApp);
					selectApp(appId);
				}
			}
		};
		selectApp=function(appId){
			for(var i=0; i<appBoxes.length; i++){
				if(i==appId)
					appBoxes[i].checked=true;
				else 
					appBoxes[i].checked=false;
			}
			updateApps();
		};
		visOnMouseMove = function(dojoEvent) {
			var found=false;
			visWidgets.forEach(function(widget){
				var interaction = widget.getInteractivity();
				var item = interaction.getTooltipItem(dojoEvent.pageX, dojoEvent.pageY);
				if(item!=null&&widget.get("active")){
					var string = item.tooltipText();
					found=true;
					showFloatingTooltip(dojoEvent,string);
					return;
				}
			});
			if(!found)
				hideFloatingTooltip();
		};
		//used code from http://vottrave.ottawa.ibm.com/documentation/rave/raveJSGuide.xml#ravejs_tooltips
		showFloatingTooltip = function(event, txt){
			if(tooltips){
				if(!this.tooltipDiv)
					makeToolTipDiv();

				this.tooltipDiv.style.top = (event.pageY + 20)+"px";
				this.tooltipDiv.style.left = (event.pageX)+"px";
				this.tooltipDiv.style.display = "block";

				this.tooltipDiv.innerHTML = txt;
			}
		};
		hideFloatingTooltip = function() {
			if (this.tooltipDiv)
				this.tooltipDiv.style.display = "none";
		};
		toggleTooltips = function() {
			tooltips = !tooltips;
			if(tooltips)
				document.getElementById("tooltipbutton").innerHTML = "Hide Tooltips";
			else {
				hideFloatingTooltip();
				document.getElementById("tooltipbutton").innerHTML = "Show Tooltips";
			}
		};
		makeToolTipDiv = function() {
			this.tooltipDiv = domConstruct.create("div",null,document.body);
			this.tooltipDiv.style.position = "absolute";
			this.tooltipDiv.style.backgroundColor= "white";
			this.tooltipDiv.style.display = "none";
			this.tooltipDiv.style.zIndex = 1000;
		};
		//Date functions
		function createDatePicker(){
			if(unit=="hour"){
				document.getElementById("datepicker").innerHTML='<input id="dayPicker" data-provide="datepicker" type="text" style="width:200px;display:inline-block" class="form-control">';
				range= false;
			}
			else {
				document.getElementById("datepicker").innerHTML='<div class="input-daterange input-group" id="daterange"><input type="text" class="form-control" id="start" /><span class="input-group-addon">to</span><input type="text" class="form-control" id="end" /></div>';
				if(unit=="day"){
					$("#datepicker .input-daterange").datepicker({daysOfWeekDisabled: []});
				}
				if(unit=="week"){
					$("#datepicker .input-daterange").datepicker();
					$("#start").datepicker("setDaysOfWeekDisabled",[0,2,3,4,5,6]);
					$("#end").datepicker("setDaysOfWeekDisabled",[1,2,3,4,5,6]);
					var tempDate = new Date();
					if(tempDate.getUTCDay()!=0){
						var day =tempDate.getUTCDay();
						tempDate.setUTCDate(tempDate.getUTCDate()+7-day);
					}
					$("#end").datepicker("setEndDate", tempDate);

				}
				if(unit=="month"){
					$("#datepicker .input-daterange").datepicker({minViewMode: 1});
				}
				if(unit=="year"){
					$("#datepicker .input-daterange").datepicker({minViewMode:2});
				}
				range=true;
			}
			document.getElementById("button-holder").innerHTML='<button class="btn" id="submitDate" onclick="submitDates()">Submit</button>';
		}
		submitDates = function(){
			received=false;
			var startDate;
			var endDate;
			if(range){
				startDate=$("#start").datepicker("getUTCDate");
				endDate = $("#end").datepicker("getUTCDate");
			}
			else {
				startDate=$("#dayPicker").datepicker("getUTCDate");
				endDate=$("#dayPicker").datepicker("getUTCDate");
			}
			if((unit=="hour"||unit=="day")||unit=="week")
				endDate.setUTCDate(endDate.getUTCDate()+1);	
			else if(unit=="month")
				endDate.setUTCMonth(endDate.getUTCMonth()+1);
			else if(unit=="year")
				endDate.setUTCFullYear(endDate.getUTCFullYear()+1);
			else
				return;
			startTime=startDate.getTime()/1000;
			endTime=endDate.getTime()/1000;
			getChords(startTime,endTime);
			//getAllAgents(startTime,endTime);
			getNestedAgents(startTime,endTime);
			getPerApp(startTime,endTime,0);
			getPerApp(startTime,endTime,1);
			getPerApp(startTime,endTime,3);
			getPerApp(startTime,endTime,4);
			getPerApp(startTime,endTime,7);
			//getPerApp(startTime,endTime,6);
			getPerApp(startTime,endTime,16);
			getNestedOS(startTime,endTime);
			getScatter(startTime,endTime);
			getSpeedGraph(startTime,endTime);
			timeList = [];
			var times = {"time0":startTime.toString()};
			var tempDate = new Date(startDate.getTime());
			//console.log(times);
			var i;
			if(unit=="hour"){
				tempDate.setUTCHours(tempDate.getUTCHours()+1);
				for(i=1;tempDate<=endDate;i++){
					var temp = "time"+i;
					times[temp]=(tempDate.getTime()/1000).toString();
					tempDate.setUTCHours(tempDate.getUTCHours()+1);
				}
			}
			else if(unit=="day"){
				tempDate.setUTCDate(tempDate.getUTCDate()+1);
				for(i=1;tempDate<=endDate;i++){
					var temp="time"+i;
					times[temp]=(tempDate.getTime()/1000).toString();
					tempDate.setUTCDate(tempDate.getUTCDate()+1);
				}
			}
			else if(unit=="week"){
				tempDate.setUTCDate(tempDate.getUTCDate()+7);
				for(i=1;tempDate<=endDate;i++){
					var temp="time"+i;
					times[temp]=(tempDate.getTime()/1000).toString();
					tempDate.setUTCDate(tempDate.getUTCDate()+7);
				}
			}
			else if(unit=="month"){
				tempDate.setUTCMonth(tempDate.getUTCMonth()+1);
				for(i=1;tempDate<=endDate;i++){
					var temp="time"+i;
					times[temp]=(tempDate.getTime()/1000).toString();
					tempDate.setUTCMonth(tempDate.getUTCMonth()+1);
				}
			}
			else if(unit=="year"){
				tempDate.setUTCFullYear(tempDate.getUTCFullYear()+1);
				for(i=1;tempDate<=endDate;i++){
					var temp="time"+i;
					times[temp]=(tempDate.getTime()/1000).toString();
					tempDate.setUTCFullYear(tempDate.getUTCFullYear()+1);
				}
			}
			else
				return;
			times.numSegments=(i-1).toString();
			for(var j=0;j<i-1;j++){
				timeList.push(times["time"+j]);
			}
			//visArea.data[0].fields[1].categories=timeList;
			//visDashArea.data[0].fields[1].categories=timeList;
			//visLine.data[0].fields[1].categories=timeList;
			times["queryType"]="over-time";
			//console.log(times);
			visEndTime=Number(times["time"+(i-2)]);
			updateSpan();
			request("TestServlet",{
				method: "POST",
				data: times
			})
			.then(function(gotData){

				//console.log(gotData);
				data = JSON.parse(gotData);

				received=true;
				//console.log(data);
				loadDashPie();
				loadDashArea();
				updateVisJSON();
				loadFancyPie();
			});
		};
		//parameter update functions
		selectAll = function(){
			apps = new Array(appBoxes.length);
			for(var i=0;i<appBoxes.length;i++){
				appBoxes[i].checked=true;
				apps[i]=i;
			}
			updateVisJSON();
		};
		deselectAll = function(){
			for(var i=0;i<appBoxes.length;i++){
				appBoxes[i].checked=false;
			}
			apps=[];
			updateVisJSON();
		};
		updateApps = function(){
			apps =[];
			for(var i=0;i<appBoxes.length;i++){
				if(appBoxes[i].checked){
					apps.push(i);
				}
			}
			updateVisJSON();
		};
		changeUnit = function(){
			if(unit==dom.byId("timeUnit").value)
				return;
			unit = dom.byId("timeUnit").value;
			//console.log(unit);
			createDatePicker();
			changeVisUnit();
		};
		toggleUnique = function(){
			unique=!unique;
			if(unique){
				visArea.grammar[0].elements[0].position[0].field.$ref="uhits";
				visLine.grammar[0].elements[0].position[0].field.$ref="uhits";
				visPie.grammar[0].elements[0].position[0].field.$ref="fuhits";
				visPie.grammar[0].elements[0].tooltip[0].content[2].$ref="fuhits";
				for(var i=0; i<visApp.length;i++){
					if(i!=6){
						visApp[i].grammar[0].elements[0].size[0].field.$ref="fusize";
						visApp[i].grammar[0].elements[0].tooltip[0].content[0].$ref="percentuhits";
						visApp[i].grammar[0].elements[0].tooltip[0].content[2].$ref="fuhits";
					}
				}
				visAgents.grammar[0].elements[0].size[0].field.$ref="uhits";
				visNestedAgents.grammar[0].elements[0].positioning.size.$ref="uhits";
				visNestedOS.grammar[0].elements[0].positioning.size.$ref="uhits";
				visChord.grammar[0].elements[0].positioning.size.$ref="uhits";
			}
			else{
				visArea.grammar[0].elements[0].position[0].field.$ref="hits";
				visLine.grammar[0].elements[0].position[0].field.$ref="hits";
				visPie.grammar[0].elements[0].position[0].field.$ref="fhits";
				visPie.grammar[0].elements[0].tooltip[0].content[2].$ref="fhits";
				for(var i=0; i<visApp.length;i++){
					if(i!=6){
						visApp[i].grammar[0].elements[0].size[0].field.$ref="fsize";
						visApp[i].grammar[0].elements[0].tooltip[0].content[0].$ref="percenthits";
						visApp[i].grammar[0].elements[0].tooltip[0].content[2].$ref="fhits";
					}
				}
				visAgents.grammar[0].elements[0].size[0].field.$ref="hits";
				visNestedAgents.grammar[0].elements[0].positioning.size.$ref="hits";
				visNestedOS.grammar[0].elements[0].positioning.size.$ref="hits";
				visChord.grammar[0].elements[0].positioning.size.$ref="hits";
			}
			updateVisJSON();
			loadChord();
			loadNestedAgents();
			loadNestedOS();
			for(var i=0;i<visApp.length;i++){
				loadPerApp(i);
			}
			//loadAgents();
		};
		switchGraph = function(){
			for(var i=0;i<graphRadios.length;i++){
				if(graphRadios[i].checked)
					graphType=graphRadios[i].value;
			}
			loadVisJSON();
		};

		//visJSON update and load functions
		loadVisJSON = function(){
			var visControl=registry.byId("visControlGeneral1");

			if(graphType=="pie"){
				//console.log(JSON.stringify(visPie));
				visControl.setSpecification(visPie);
			}
			else if(graphType=="area"){
				//console.log(JSON.stringify(visArea));
				visControl.setSpecification(visArea);
			}
			else{
				//console.log(JSON.stringify(visLine));
				visControl.setSpecification(visLine);
			}

		};
		updateVisJSON=function(){
			if(!received)
				return;
			updateRows();
			visArea.data[0].rows=rows;
			visLine.data[0].rows=rows;
			visPie.data[0].rows=pieRows;
			loadVisJSON();
		};
		updateSpan=function(){
			if(visLoaded<2)
				return;
			var scale = {"spans":[{"min":startTime,"max":visEndTime}]};
			visArea.grammar[0].coordinates.dimensions[1].scale=scale;
			visDashArea.grammar[0].coordinates.dimensions[1].scale=scale;
			visDashSpeed.grammar[0].coordinates.dimensions[1].scale=scale;
			visLine.grammar[0].coordinates.dimensions[1].scale=scale;
		};
		changeVisUnit=function(){
			if(visLoaded<2)
				return;
			var datePattern;
			if(unit=="hour"){
				datePattern = "HH:mm";
			}
			else if(unit=="day"){
				datePattern="MMM d";
			}
			else if(unit=="month")
				datePattern="YYYY MMM";
			else if(unit=="week")
				datePattern="MMM d";
			else
				datePattern="YYYY";
			visArea.data[0].fields[1].format={"datePattern":datePattern};
			visDashArea.data[0].fields[1].format={"datePattern":datePattern};
			visSpeed.data[0].fields[2].format={"datePattern":datePattern};
			visDashSpeed.data[0].fields[2].format={"datePattern":datePattern};
			visLine.data[0].fields[1].format={"datePattern":datePattern};
		};
		updateRows=function(){
			rows=data[apps[0]];
			pieRows=new Array(apps.length);
			pieRows[0]=data[numApps][apps[0]];
			for(var i=1;i<apps.length;i++){
				var temp = rows.concat(data[apps[i]]);
				rows=temp;
				pieRows[i]=data[numApps][apps[i]];
			}
		};
		loadChord =function(){
			updateChord();
			var visControl = registry.byId("visControlChord");
			visControl.setSpecification(visChord);
		};
		updateChord = function(){
			visChord.data[0].rows=chordData;
		};
		loadAgents = function(){
			updateAgents();
			var visControl=registry.byId("visControlAgent");
			visControl.setSpecification(visAgents);
		};
		updateAgents = function(){
			visAgents.data[0].rows=agentData;
			visAgents.data[0].fields[0].categories=agents;
		};
		loadNestedAgents=function(){
			updateNested();
			var visControl=registry.byId("visControlAgent");
			visControl.setSpecification(visNestedAgents);
		};
		updateNested=function(){
			visNestedAgents.data[0].rows=nestedAgentData;
			visNestedAgents.data[0].fields[0].categories=nestedAgents;
			visNestedAgents.data[0].fields[1].categories=nestedAgentParents;
		};
		loadPerApp=function(app){
			updatePerApp(app);
			var visControl=registry.byId("visControlApp"+app);
			//console.log(visControl);
			visControl.setSpecification(visApp[app]);
			//console.log(visControl);
		};
		updatePerApp = function(app){
			visApp[app].data[0].rows=perAppData[app];
			visApp[app].data[0].fields[0].categories=perAppCats[app];
		};
		loadFancyPie = function(){
			updateFancyPie();
			var visControl=registry.byId("visControlFancy");
			visControl.setSpecification(visFancyPie);
		};
		updateFancyPie=function(){
			visFancyPie.data[0].rows=data[numApps];
		};
		loadNestedOS=function(){
			updateNestedOS();
			var visControl=registry.byId("visControlNestedOS");
			visControl.setSpecification(visNestedOS);
		};
		updateNestedOS=function(){
			visNestedOS.data[0].rows=nestedOSData;
			visNestedOS.data[0].fields[0].categories=nestedOS;
			visNestedOS.data[0].fields[1].categories=nestedOSParents;
		};
		loadScatter=function(){
			updateScatter();
			var visControl=registry.byId("visControlScatter");
			visControl.setSpecification(visScatter);
		};
		updateScatter=function(){
			visScatter.data[0].rows=scatterData;
		};
		loadSpeed=function(){
			updateSpeed();
			var visControl=registry.byId("visControlSpeed");
			visControl.setSpecification(visSpeed);
		};
		updateSpeed=function(){
			visSpeed.data[0].rows=speedData;
		};
		loadDashArea=function(){
			dashAreaData=data[0];
			for(var i=1;i<numApps;i++){
				temp=data[i];
				dashAreaData=dashAreaData.concat(temp);
			}
			visDashArea.data[0].rows=dashAreaData;
			console.log(dashAreaData);
			var visControl=registry.byId("visDashboardArea");
			visControl.setSpecification(visDashArea);
			console.log(visDashArea);
		};
		loadDashPie=function(){
			dashPieData=data[numApps];
			visDashPie.data[0].rows=dashPieData;
			visDashPie.grammar[0].elements[0].position[0].field.$ref="fuhits";
			visDashPie.grammar[0].elements[0].tooltip[0].content[2].$ref="fuhits";
			var visControl=registry.byId("visDashboardPie");
			visControl.setSpecification(visDashPie);
		};
		loadDashSpeed=function(){
			visDashSpeed.data[0].rows=dashSpeedData;
			var visControl=registry.byId("visDashboardSpeed");
			visControl.setSpecification(visDashSpeed);
		};
		loadDashNested=function(){
			visDashNested.data[0].rows=dashNestedData;
			visDashNested.data[0].fields[0].categories=dashNestedAgents;
			visDashNested.data[0].fields[1].categories=dashNestedParents;
			var visControl=registry.byId("visDashboardNested");
			visControl.setSpecification(visDashNested);
		};
		//HTTP functions
		//function to get data for chords
		getChords = function(start,end){
			var requestData = {"startTime":start.toString()};
			requestData["endTime"]=end.toString();
			requestData["queryType"]="chord";
			request("TestServlet",{
				method: "POST",
				data: requestData
			})
			.then(function(data){
				//console.log(data);
				chordData=JSON.parse(data);
				loadChord();
			});
		};
		getAllAgents = function(start,end){
			var requestData={"startTime":start.toString()};
			requestData["endTime"]=end.toString();
			requestData["queryType"]="agent";
			request("TestServlet",{
				method:"POST",
				data: requestData
			})
			.then(function(data){
				//console.log(data);
				var temp=JSON.parse(data);
				agentData=temp.data;
				agents=temp.categories;
				loadAgents();
			});
		};
		getNestedAgents = function(start,end){
			var requestData={"startTime":start.toString(),"endTime":end.toString(),"queryType":"agentNest"};
			request("TestServlet",{
				method:"POST",
				data: requestData
			})
			.then(function(data){
				//console.log("nested: "+data);
				var temp = JSON.parse(data);
				nestedAgents=temp.categories;
				nestedAgentParents=temp.categoriesParent;
				nestedAgentData=temp.data;
				loadNestedAgents();
				dashNestedAgents=temp.categories;
				dashNestedParents=temp.categoriesParent;
				dashNestedData=temp.data;
				loadDashNested();
			});
		};
		getPerApp=function(start,end,app){
			var requestData={"startTime":start.toString(),"endTime":end.toString(),"queryType":"perApp","app":app.toString()};
			request("TestServlet",{
				method:"POST",
				data: requestData
			})
			.then(function(data){
				var temp = JSON.parse(data);
				if(app==6)
					console.log(temp);
				perAppCats[app]=temp.categories;
				perAppData[app]=temp.data;
				//console.log(perAppCats[app]);
				if(app!=6)
					loadPerApp(app);
			});
		};
		getNestedOS = function(start,end){
			var requestData={"startTime":start.toString(),"endTime":end.toString(),"queryType":"OSNest"};
			request("TestServlet",{
				method:"POST",
				data:requestData
			})
			.then(function(data){
				var temp = JSON.parse(data);
				//console.log("nested: "+data);
				nestedOS=temp.categories;
				nestedOSParents=temp.categoriesParent;
				nestedOSData=temp.data;
				loadNestedOS();
			});
		};
		getScatter = function(start,end){
			var requestData={"startTime":start.toString(),"endTime":end.toString(),"queryType":"scatter"};
			var numSegments=(end-start)/600;//every 10 minutes
			requestData["numSegments"]=numSegments.toString();
			request("TestServlet",{
				method:"POST",
				data:requestData
			})
			.then(function(data){
				scatterData=JSON.parse(data);
				loadScatter();
			});
		};
		getSpeedGraph = function(start,end){
			var requestData={"startTime":start.toString(),"endTime":end.toString(),"queryType":"scatter"};
			var numSegments=(end-start)/3600;//every hour
			requestData["numSegments"]=numSegments.toString();
			request("TestServlet",{
				method:"POST",
				data:requestData
			})
			.then(function(data){
				speedData=JSON.parse(data);
				loadSpeed();
				dashSpeedData=JSON.parse(data);
				loadDashSpeed();
			});
		};
		//testing functions
		logVisJSONs=function(){
			console.log("visArea: "+JSON.stringify(visArea));
			console.log("visLine: "+JSON.stringify(visLine));
			console.log("visPie: "+JSON.stringify(visPie));
			console.log("visAgents: "+JSON.stringify(visAgents));
			console.log("visChord: "+JSON.stringify(visChord));
			console.log("visNestedAgents: "+JSON.stringify(visNestedAgents));
			console.log("visNestedOS: "+JSON.stringify(visNestedOS));
			console.log("visFancyPie: "+JSON.stringify(visFancyPie));
			console.log("visScatter: "+JSON.stringify(visScatter));
			console.log("visSpeed: "+JSON.stringify(visSpeed));
			for(var i=0; i<numApps;i++){
				console.log("visApp"+i+": "+JSON.stringify(visApp[i]));
			}
		};

		updateDesignDocs=function(){
			var startDate;
			var endDate;
			if(range){
				startDate=$("#start").datepicker("getUTCDate");
				endDate = $("#end").datepicker("getUTCDate");
			}
			else {
				startDate=$("#dayPicker").datepicker("getUTCDate");
				endDate=$("#dayPicker").datepicker("getUTCDate");
			}
			if((unit=="hour"||unit=="day")||unit=="week")
				endDate.setUTCDate(endDate.getUTCDate()+1);	
			else if(unit=="month")
				endDate.setUTCMonth(endDate.getUTCMonth()+1);
			else if(unit=="year")
				endDate.setUTCFullYear(endDate.getUTCFullYear()+1);
			else
				return;
			start=startDate.getTime()/1000;
			end=endDate.getTime()/1000;
			var tempStartTime=new Date(start*1000);
			var tempEndTime=new Date(start*1000);
			tempEndTime.setUTCHours(tempEndTime.getUTCHours()+1);
			while(tempEndTime.getTime()<=(end*1000)){
				request("http://scsbmetrics.swg.usma.ibm.com:5984/vitals_tests3/_design/"+(tempStartTime.getTime()/1000)+(tempEndTime.getTime()/1000)+"/_view/AppIdCount");
				tempStartTime.setUTCHours(tempStartTime.getUTCHours()+1);
				tempEndTime.setUTCHours(tempEndTime.getUTCHours()+1);
			}
			tempStartTime=new Date(start*1000);
			tempEndTime=new Date(start*1000);
			tempEndTime.setUTCDate(tempEndTime.getUTCDate()+1);
			while(tempEndTime.getTime()<=(end*1000)){
				request("http://scsbmetrics.swg.usma.ibm.com:5984/vitals_tests3/_design/"+(tempStartTime.getTime()/1000)+(tempEndTime.getTime()/1000)+"/_view/AppIdCount");
				tempStartTime.setUTCDate(tempStartTime.getUTCDate()+1);
				tempEndTime.setUTCDate(tempEndTime.getUTCDate()+1);
			}
			request("http://scsbmetrics.swg.usma.ibm.com:5984/vitals_tests3/_design/"+start+end+"/_view/AppIdCount");

		};
	});
});