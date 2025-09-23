//load rave build layer
require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", "dojo/query", "dojo/dom","dojo/dom-construct", "dojo/dom-class","dojo/dom-attr","dijit/WidgetSet","dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", "dojo/_base/event"], function(ready, template, parser, registry, lang, on, request, query, dom, domConstruct, domClass,domAttr, WidgetSet) {
		//ready function is needed to ensure the rave system is properly loaded
		//master variables
		var appList = [];
		var appNames = [];
		var masterImageList;
		var numApps = 0;
		var envList = [];
		var numEnvs = 0;
		var palette =["black","blue","red",
		          "#82D1F5",
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
		var homeGraphs={areaEnv:{vis:{},loaded:false},areaApp:{vis:{},loaded:false},OS:{vis:{},loaded:false},Client:{vis:{},loaded:false}};
		var envGraphs = {};
		var appGraphs = {};
		var popoutGraph = {};
		//NodeList of rave doms
		var visDoms = query("[data-dojo-type=com.ibm.vis.widget.VisControl]");
		//WidgetSet of rave widgets
		var visWidgets = new WidgetSet();
		
		//var linkHolder = new Array(numApps);
		//internal variables
		var visLoaded = 0;
		var active="visHome";
		var currentEnv = null;
		var currentApp = null;
		//parameters
		var tooltips = true;
		var unit="hour";
		var startTime=0; //seconds since epoch
		var endTime=0; //seconds since epoch
		var visEndTime=0; //Used for end of scale
		var graphType = "area"; //general graph type
		var unique = true; //unique vs non-unique
		//var apps = []; //array for which apps are being shown, stores the indices, used for general graphs only

		//var order = [];//to be added to app field on area graph
		var appBoxes = [];
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
		$("#picker").datepicker("update","-1d");//yesterday as default
		var date = $("#picker").datepicker("getUTCDate");

		request("Servlet")
		.then(function(data){
			appList = JSON.parse(data).apps;
			envList = JSON.parse(data).envs;
			numApps = appList.length;
			numEnvs = envList.length;
			appNames = new Array(numApps);
			for(var i=0; i<numApps; i++){
				appNames[i] = appList[i].name;
				//console.log(appNames[i]);
				appGraphs[appNames[i]]={appGraph:{loaded:false},Line:{loaded:false},OS:{loaded:false},Client:{loaded:false}};
			}
			loadDropdowns();
			for(var i=0; i<numEnvs;i++){
				envGraphs[envList[i]]={areaApp:{loaded:false},Line:{loaded:false},OS:{loaded:false},Client:{loaded:false},apps:{}};
				for(var j=0;j<numApps; j++){
					envGraphs[envList[i]].apps[appNames[j]]={appGraph:{loaded:false},Line:{loaded:false},OS:{loaded:false},Client:{loaded:false}};
				}
			}
			/*order = new Array(numApps);//to be added to app field on area graph
			for(var i=0;i<numApps;i++){
				order[i]=numApps-i-1;
			}*/
			loadJSONFiles();
		});



		//load visJSON files
		loadJSONFiles = function(){
			request("json/vitalsArea.json")
			.then(function(data){
				var vis = JSON.parse(data);
				homeGraphs.areaApp.vis = vis;
				homeGraphs.areaEnv.vis = vis;
				homeGraphs.areaApp.vis.data[0].fields[0].categories=appNames;
				homeGraphs.areaApp.vis.grammar[0].elements[0].color[0].palette=palette;
				homeGraphs.areaEnv.vis.data[0].fields[0].categories=envList;
				homeGraphs.areaEnv.vis.grammar[0].elements[0].color[0].palette=palette;
				for(var i=0; i<numEnvs;i++){
					envGraphs[envList[i]].areaApp.vis = vis;
					envGraphs[envList[i]].areaApp.vis.data[0].fields[0].categories=appNames;
					envGraphs[envList[i]].areaApp.vis.grammar[0].elements[0].color[0].palette=palette;
				}
				//console.log(visArea);

				visLoaded++;
				changeVisUnit();
			});
			request("json/vitalsAppDataPie.json")
			.then(function(data){
				var vis = JSON.parse(data);
				for(var i=0; i<numApps; i++){
					if(appList[i].graphTypes.indexOf("Pie")>=0){
						appGraphs[appNames[i]].appGraph.vis = vis;
						appGraphs[appNames[i]].appGraph.vis.grammar[0].elements[0].color[0].palette = palette;
						for(var j=0;j<numEnvs;j++){
							envGraphs[envList[j]].apps[appNames[i]].appGraph.vis = vis;
							envGraphs[envList[j]].apps[appNames[i]].appGraph.vis.grammar[0].elements[0].color[0].palette = palette;
						}
					}
				}
			});
			request("json/vitalsAppBubble.json")
			.then(function(data){
				var vis = JSON.parse(data);
				for(var i=0;i<numApps;i++){
					if(appList[i].graphTypes.indexOf("Bubble")>=0){
						appGraphs[appNames[i]].appGraphs.vis=vis;
						appGraphs[appNames[i]].appGraphs.vis.bubble.grammar[0].elements[0].color[0].palette=palette;
						for(var j=0;j<numEnvs;j++){
							envGraphs[envList[j]].apps[appNames[i]].appGraph.vis = vis;
							envGraphs[envList[j]].apps[appNames[i]].appGraph.vis.grammar[0].elements[0].color[0].palette = palette;
						}
					}
				}
			});
			request("json/vitalsLayeredPie.json")
			.then(function(data){
				var vis = JSON.parse(data);
				homeGraphs.OS.vis=vis;
				homeGraphs.Client.vis = vis;
				homeGraphs.OS.vis.grammar[0].elements[0].color[0].palette=palette;
				homeGraphs.Client.vis.grammar[0].elements[0].color[0].palette=palette;
				for(var i=0;i<numEnv;i++){
					envGraphs[envList[i]].OS.vis = vis;
					envGraphs[envList[i]].Client.vis = vis;
					envGraphs[envList[i]].OS.vis.grammar[0].elements[0].color[0].palette=palette; 
					envGraphs[envList[i]].Client.vis.grammar[0].elements[0].color[0].palette=palette;
					for(var j=0;j<numApps;j++){
						if(i==0){
							appGraphs[appNames[j]].OS.vis = vis;
							appGraphs[appNames[j]].Client.vis = vis;
							appGraphs[appNames[j]].OS.vis.grammar[0].elements[0].color[0].palette=palette; 
							appGraphs[appNames[j]].Client.vis.grammar[0].elements[0].color[0].palette=palette;
						}
						envGraphs[envList[i]].apps[appNames[j]].OS.vis = vis;
						envGraphs[envList[i]].apps[appNames[j]].Client.vis = vis;
						envGraphs[envList[i]].apps[appNames[j]].OS.vis.grammar[0].elements[0].color[0].palette=palette; 
						envGraphs[envList[i]].apps[appNames[j]].Client.vis.grammar[0].elements[0].color[0].palette=palette;
					}
				}
				//visAppGraphs[6]=JSON.parse(data);
				//visAppGraphs[6].grammar[0].elements[0].color[0].palette=palette;
			});
			request("json/vitalsLine.json")
			.then(function(data){
				var vis = JSON.parse(data);
				for(var i=0;i<numEnvs;i++){
					envGraphs[envList[i]].Line.vis = vis;
					envGraphs[envList[i]].Line.vis.data[0].fields[0].categories = envList;
					envGraphs[envList[i]].Line.vis.grammar[0].elements[0].color[0].palette=palette;
					for(var j=0;j<numApps;j++){
						if(i==0){
							appGraphs[appNames[j]].Line.vis = vis;
							appGraphs[appNames[j]].Line.vis.grammar[0].elements[0].color[0].palette=palette;
							appGraphs[appNames[j]].Line.vis.data[0].fields[0].categories = appNames;
						}
						envGraphs[envList[i]].apps[appNames[j]].Line.vis = vis;
						envGraphs[envList[i]].apps[appNames[j]].Line.vis.grammar[0].elements[0].color[0].palette=palette;
						envGraphs[envList[i]].apps[appNames[j]].Line.vis.data[0].fields[0].categories = appNames;
					}
				}
				visLoaded++;
				changeVisUnit();
			});
		};
		readyVis = function(id){
			ready(function(){
				parser.parse();
				var widget=registry.byId(id);
				widget.resizeToWindow=true;
				on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
				on( widget, "click", lang.hitch(widget,visOnClick));
				visWidgets.add(widget);
				if(id.indexOf("visHome")==0)
					widget.set("active",true);
				if(id.indexOf("Apps")>=0){
					widget.set("type","areaApp");
				}
				else if(id == "VisHomeEnv"){
					widget.set("type","areaEnv");
				}
				else if(id.indexOf("OS")>0){
					widget.set("type","nestedOS");
					
				}
				else if(id.indexOf("Client")>0){
					widget.set("type","nestedClient");
				}
				else if(id.indexOf("Line")>0){
					widget.set("type","line");
				}
				else{
					widget.set("type","app");
				}
				//console.log(widget);
			});
		};
		visDoms.forEach(function(node){
			readyVis(node.id);
		});
		
		loadDropdowns = function(){
			//console.log("loading dropdowns");
			for(var i=0;i<numApps;i++){
				//console.log("loading "+i+"th item");
				var tempLi = domConstruct.create("li",null,"app-menu");
				domConstruct.create("a",{href:"#app", "data-toggle":"pill",onclick:"switchTab(null,"+i+")",innerHTML:appNames[i]},tempLi);
			}
			for(var i=0;i<numEnvs;i++){
				var envLi = domConstruct.create("li",{class:"dropdown"},"top-menu");
				var envA = domConstruct.create("a",{href:"#",class:"dropdown-toggle","data-toggle":"dropdown",innerHTML:envList[i]},envLi);
				domConstruct.create("span",{class:"caret"},envA);
				var envAppMenu = domConstruct.create("ul",{class:"dropdown-menu",role:"menu"},envLi);
				var tempLi = domConstruct.create("li",null,envAppMenu);
				domConstruct.create("a",{href:"#env",innerHTML:"General","data-toggle":"pill",onclick:"switchTab("+i+",null)"},tempLi);
				domConstruct.create("li",{class:"divider"},envAppMenu);
				for(var j=0;j<numApps;j++){
					tempLi = domConstruct.create("li",null,envAppMenu);
					domConstruct.create("a",{href:"#envApp","data-toggle":"pill",innerHTML:appNames[j],onclick:"switchTab("+i+","+j+")"},tempLi);
				}
			}
		};
		showTab = function(env,app){
			$("[onclick='switchTab("+env+","+app+")']").tab("show");
			switchTab(env,app);
		};
		switchTab = function(env,app){
			currentEnv = env;
			currentApp = app;
			console.log(currentEnv);
			console.log(currentApp);
			
			if(currentEnv!=null){
				query(".cur-env").forEach(function(node){
					node.innerHTML = envList[currentEnv];
				});
			}
			if(currentApp!=null){
				query(".cur-app").forEach(function(node){
					node.innerHTML = appNames[currentApp];
				});
			}
			if(currentEnv==null&&currentApp==null){
				active="visHome";
			}
			else if(currentEnv==null){
				active = "visApp";
			}
			else if(currentApp==null){
				active= "visEnv";
			}
			else{
				active = "visEnvApp";
			}
			setActiveTab();
			setActiveVis();
			visWidgets.forEach(function(widget){
				widget.resize(); 
			});
			//getGraphs();
		};
		setActiveTab=function(){
			query("#top-menu .active").forEach(function(node){
				domClass.remove(node,"active");
			});
			if(active=="visEnvApp"){
				query("#top-menu:nth-child("+(currentEnv+2)+")>ul>li>a").forEach(function(node){
					if(node.innerHTML==appNames[currentApp]){
						domClass.add(node,"active");
						console.log(node);
					}
				});
			}
			else if(active=="visEnv"){
				query("#top-menu:nth-child("+(currentEnv+2)+")>ul>li>a").forEach(function(node){
					if(node.innerHTML=="General"){
						domClass.add(node,"active");
						console.log(node);
					}
				});
			}
			else if(active=="visApp"){
				query("#app-menu>li>a").forEach(function(node){
					if(node.innerHTML==appNames[currentApp]){
						domClass.add(node,"active");
						console.log(node);
					}
				});
			}
		};
		setActiveVis=function(){
			visWidgets.forEach(function(widget){
				console.log(widget.id);
				if(widget.id.indexOf(active)==0){
					widget.set("active",true);
				}
				else{
					widget.set("active",false);
				}
			});
		};
		//console.log(visWidgets);
		//functions for interaction with the graph
		visOnClick = function(dojoEvent) {
			//var visControl=registry.byId("visControlGeneral1");
			var interaction = this.getInteractivity();
			var item = interaction.getMetaItem(dojoEvent.pageX,dojoEvent.pageY);
			console.log(item);
			//meta doesn't work when using order array
			if(item!=null&&this.active){
				console.log(item.meta());
				var meta = item.meta();
				var current;
				var parent = null;
				if(meta.indexOf("parent: ")<0){
					current = meta.substr(meta.indexOf("cat")+5);
				}
				else{
					current = meta.substring(meta.indexOf("cat: ")+5,meta.indexOf(", parent: "));
					parent = meta.substr(meta.indexOf("parent: ")+8);
				}
				console.log(current);
				console.log(parent);
				if(this.type=="areaApp"){
					showTab(currentEnv,appNames.indexOf(current));
				}
				else if(this.type=="areaEnv"){
					showTab(envList.indexOf(current),currentApp);
				}
				else if(this.type.indexOf("nested")==0){
					var newAgent;
					if(parent==null){
						newAgent=current;
					}
					else{
						newAgent = parent;
					}
					if(currentApp==null&&currentEnv==null){
						var subType = this.type.substr(6);
						console.log(subType);
						if(homeGraphs[subType].vis.data[0].rows.length<homeGraphs[subType].vis.data[0].fields[0].categories.length){
							homeGraphs[subType].vis.data[0].rows = homeGraphs[subType].data.data;
							this.setSpecification(homeGraphs[subType].vis);
						}
						else{
							var agentIndex = homeGraphs[subType].data.categoriesParent.indexOf(newAgent);
							var newData;
							var startIndex = homeGraphs[subType].data.indices[agentIndex];
							if(agentIndex<homeGraphs[subType].data.categoriesParent.length-1){
								var endIndex = homeGraphs[subType].data.indices[agentIndex+1];
								newData = homeGraphs[subType].data.data.slice(startIndex,endIndex);
							}
							else{
								newData = homeGraphs[subType].data.data.slice(startIndex);
							}
							homeGraphs[subType].vis.data[0].rows = newData;
							this.setSpecification(homeGraphs[subType].vis);
						}
					}
				}
				
			}
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
			domConstruct.create("input",{id:"picker","data-provide":"datepicker", 
				type:"text",style:{width:"200px",display:"inline-block"},class:"form-control"},"datepicker","only");
			if(unit=="hour"){
				$("#picker").datepicker({minViewMode: 0});
			}
			if(unit=="day"){
				$("#picker").datepicker({minViewMode: 1});
			}
			if(unit=="month"){
				$("#picker").datepicker({minViewMode: 2});
			}
			if(unit=="year"){
				$("#picker").datepicker({minViewMode:3});
			}
			if(unit=="All Time"){
				dom.byId("datepicker").innerHTML = null;
			}
			domConstruct.create("button",{class:"btn", id:"submitDate",
				onclick:"submitDate()",innerHTML:"Submit"},"button-holder","only");
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
				
			}
			else{
				
			}
			//loadAgents();
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
				datePattern="MMMM d";
			}
			else if(unit=="month")
				datePattern="YYYY MMMM";
			else
				datePattern="YYYY";
			homeGraphs.areaApp.vis.data[0].fields[1].format={"datePattern":datePattern};
			homeGraphs.areaEnv.vis.data[0].fields[1].format={"datePattern":datePattern};
			for(var i=0;i<numEnvs;i++){
				envGraphs[envList[i]].areaApp.vis.data[0].fields[1].format={"datePattern":datePattern};
				envGraphs[envList[i]].Line.vis.data[0].fields[1].format={"datePattern":datePattern};
				for(var j=0;j<numEnvs;j++){
					if(i==0){
						appGraphs[appNames[j]].Line.vis.data[0].fields[1].format={"datePattern":datePattern};
					}
					envGraphs[envList[i]].apps[appNames[j]].Line.vis.data[0].fields[1].format={"datePattern":datePattern};
				}
			}
		};
		submitDate = function(){
			updateDate();
			getGraphs();
		};
		updateDate = function(){
			var newDate;
			if(unit!="All Time")
				newDate = $("#picker").datepicker("getUTCDate");
			else
				newDate = null;
			if(newDate!=date){
				homeGraphs.areaApp.loaded=false;
				homeGraphs.areaEnv.loaded=false;
				homeGraphs.OS.loaded=false;
				homeGraphs.Client.loaded=false;
				for(var i=0;i<numEnvs;i++){
					envGraphs[envList[i]].areaApp.loaded=false;
					envGraphs[envList[i]].Line.loaded=false;
					envGraphs[envList[i]].OS.loaded=false;
					envGraphs[envList[i]].Client.loaded=false;
					for(var j=0;j<numApps;j++){
						if(i==0){
							appGraphs[appNames[j]].appGraph.loaded=false;
							appGraphs[appNames[j]].Line.loaded=false;
							appGraphs[appNames[j]].OS.loaded=false;
							appGraphs[appNames[j]].Client.loaded=false;
						}
						envGraphs[envList[i]].apps[appNames[j]].appGraph.loaded=false;
						envGraphs[envList[i]].apps[appNames[j]].Line.loaded=false;
						envGraphs[envList[i]].apps[appNames[j]].OS.loaded=false;
						envGraphs[envList[i]].apps[appNames[j]].Client.loaded=false;
					}
				}
				date=newDate;
			}
		};
		//HTTP functions
		//function to get data for chords
		getGraphs= function(){
			var env = currentEnv;
			var app = currentApp;
			if(env==null&&app==null){
				getVis(env,app,"areaEnv");
				getVis(env,app,"areaApp");
				getVis(env,app,"OS");
				getVis(env,app,"Client");
			}
			else if(env==null){
				getVis(env,app,"Line");
				if(appList[app].graphTypes.length>0){
					getVis(env,app,"Bubble");
				}
				getVis(env,app,"OS");
				getVis(env,app,"Client");
			}
			else if(app==null){
				getVis(env,app,"Line");
				getVis(env,app,"areaApp");
				getVis(env,app,"OS");
				getVis(env,app,"Client");
			}
			else{
				getVis(env,app,"Line");
				if(appList[app].graphTypes.length>0){
					getVis(env,app,"Bubble");
				}
				getVis(env,app,"OS");
				getVis(env,app,"Client");
			}
		};
		
		getVis = function(env,app,type){
			if(type=="Line"){
				load(env,app,"Line");
				return;
			}
			if(env==null&&app==null){
				if(homeGraphs[type].loaded){
					load(env,app,type);
					return;
				}
			}
			else if(app==null){
				var temp = envGraphs[envList[env]];
				if(temp[type].loaded){
					load(env,app,type);
					return;
				}
			}
			else if(env==null){
				if(type=="Bubble"){
					if(appGraphs[appNames[app]].appGraph.loaded){
						load(env,app,type);
						return;
					}
				}
				else{
					console.log(appGraphs[appNames[app]]);
					console.log(type);
					var temp  = appGraphs[appNames[app]];
					if(temp[type].loaded){
						load(env,app,type);
						return;
					}
				}
			}
			else{
				if(type=="Bubble"){
					if(envGraphs[envList[env]].apps[appNames[app]].appGraph.loaded){
						load(env,app,type);
						return;
					}
				}
				else{
					var temp = envGraphs[envList[env]].apps[appNames[app]];
					if(temp[type].loaded){
						load(env,app,type);
						return;
					}
				}
			}
			var data = {};
			if(unit=="all time"){
				data.numUnits = 0;
			}
			else if (unit=="year"){
				data.numUnits = 0;
			}
			else if (unit=="month"){
				data.numUnits = 1;
			}
			else if (unit=="day"){
				data.numUnits = 2;
			}
			else data.numUnits = 3;
			for(var i=0;i<data.numUnits;i++){
				if(i==0){
					data["unit"+i]=date.getUTCFullYear();
				}
				if(i==1){
					data["unit"+i]=date.getUTCMonth()+1;
				}
				if(i==2){
					data["unit"+i]=date.getUTCDate();
				}
			}
			if(env!=null){
				data.env = envList[env];
			}
			if(app!=null){
				data.app = app;
			}
			
			data.queryType = type;
			if(app!=null&&(type=="Client"||type=="OS")){
				data.queryType = type+"App";
			}
			request("Servlet",{
				method:"POST",
				data: data
			})
			.then(function(data){
				parseData = JSON.parse(data);
				if(env==null&&app==null){
					if(type== "areaEnv"){
						homeGraphs.areaEnv.vis.data[0].rows = parseData.data;
						homeGraphs.areaEnv.vis.data[0].fields[0].categories = parseData.categories;
						homeGraphs.areaEnv.data=parseData;
						homeGraphs.areaEnv.loaded=true;
						for(var i=0;i<numEnvs;i++){
							envGraphs[envList[i]].Line.vis.data[0].rows = parseData.data.slice(i*parseData.numSegments,(i+1)*parseData.numSegments);
							envGraphs[envList[i]].Line.vis.data[0].fields[0].categories = parseData.categories;
							envGraphs[envList[i]].Line.loaded=true;
						}
					}
					else if(type== "areaApp"){
						homeGraphs.areaApp.vis.data[0].rows = parseData.data;
						homeGraphs.areaApp.vis.data[0].fields[0].categories = parseData.categories;
						homeGraphs.areaApp.data = parseData;
						homeGraphs.areaApp.loaded=true;
						for(var i=0;i<numApps;i++){
							appGraphs[appNames[i]].Line.vis.data[0].rows = parseData.data.slice(i*parseData.numSegments,(i+1)*parseData.numSegments);
							appGraphs[appNames[i]].Line.vis.data[0].fields[0].categories = parseData.categories;
							appGraphs[appNames[i]].Line.loaded=true;
						}
					}
					else{
						homeGraphs[type].vis.data[0].rows = parseData.data;
						homeGraphs[type].vis.data[0].fields[0].categories = parseData.categories;
						homeGraphs[type].vis.data[0].fields[1].categories = parseData.categoriesParent;
						homeGraphs[type].data = parseData;
						homeGraphs[type].loaded=true;
					}
				}
				else if(env==null){
					if(type=="Bubble"){
						appGraphs[appNames[app]].appGraph.vis.data[0].rows = parseData.data;
						appGraphs[appNames[app]].appGraph.vis.data[0].fields[0].categories = parseData.categories;
						appGraphs[appNames[app]].appGraph.data = parseData;
						appGraphs[appNames[app]].appGraph.loaded=true;
					}
					else{
						var temp = appGraphs[appNames[app]];
						temp[type].data = parseData;
						temp[type].vis.data[0].fields[0].categories = parseData.categories;
						temp[type].vis.data[0].fields[1].categories = parseData.categoriesParent;
						temp[type].vis.data[0].rows = parseData.data;
						temp[type].loaded=true;
					}
				}
				else if(app==null){
					if(type=="areaApp"){
						envGraphs[envList[env]].areaApp.vis.data[0].fields[0].categories = parseData.categories;
						envGraphs[envList[env]].areaApp.vis.data[0].rows = parseData.data;
						envGraphs[envList[env]].areaApp.data = parseData;
						envGraphs[envList[env]].areaApp.loaded=true;
						for(var i=0;i<numApps;i++){
							envGraphs[envList[env]].apps[appNames[i]].Line.vis.data[0].rows = parseData.data.slice(i*parseData.numSegments,(i+1)*parseData.numSegments);
							envGraphs[envList[env]].apps[appNames[i]].Line.vis.data[0].fields[0].categories = parseData.categories;
							envGraphs[envList[env]].apps[appNames[i]].Line.loaded=true;
						}
					}
					else{
						var temp = envGraphs[envlist[env]];
						temp[type].data= parseData;
						temp[type].vis.data[0].rows=parseData.data;
						temp[type].vis.data[0].fields[0].categories = parseData.categories;
						temp[type].vis.data[0].fields[1].categories = parseData.categoriesParent;
						tmep[type].loaded=true;
					}
				}
				else{
					if(type=="Bubble"){
						envGraphs[envList[env]].apps[appNames[app]].appGraph.data = parseData;
						envGraphs[envList[env]].apps[appNames[app]].appGraph.vis.data[0].rows = parseData.data;
						envGraphs[envList[env]].apps[appNames[app]].appGraph.vis.data[0].fields[0].categories = parseData.categories;
						envGraphs[envList[env]].apps[appNames[app]].appGraph.loaded=true;
					}
					else{
						var temp = envGraphs[envList[env]].apps[appNames[app]];
						temp[type].data=parseData;
						temp[type].vis.data[0].rows = parseData.data;
						temp[type].vis.data[0].fields[0].categories=parseData.categories;
						temp[type].vis.data[0].fields[1].categories=parseData.categoriesParent;
						temp[type].loaded=true;
					}
				}
				load(env,app,type);
			});
			
		};
		load = function(env,app,type){
			if(currentEnv!=env||currentApp!=app){
				return;
			}
			if(env==null&&app==null){
				if(type=="areaApp"){
					var visControl = registry.byId("visHomeApps");
					visControl.setSpecification(homeGraphs[type].vis);
				}
				else if(type=="areaEnv"){
					var visControl = registry.byId("visHomeEnv");
					visControl.setSpecification(homeGraphs[type].vis);
				}
				else{
					var visControl =registry.byId("visHome"+type);
					visControl.setSpecification(homeGraphs[type].vis);
				}
			}
			else if(env==null){
				if(type=="Bubble"){
					var visControl =registry.byId("visApp");
					visControl.setSpecification(appGraphs[appNames[app]].appGraph.vis);
				}
				else{
					var visControl = registry.byId("visApp"+type);
					var temp = appGraphs[appNames[app]];
					visControl.setSpecification(temp[type].vis);
				}
			}
			else if(app==null){
				if(type=="areaApp"){
					var visControl =registry.byId("visEnvApps");
					visControl.setSpecification(envGraphs[envList[env]].areaApp.vis);
				}
				else{
					var visControl =registry.byId("visEnv"+type);
					var temp = envGraphs[envList[env]];
					visControl.setSpecification(temp[type].vis);
				}
			}
			else{
				if(type=="Bubble"){
					var visControl = registry.byId("visEnvApp");
					visControl.setSpecification(envGraphs[envList[env]].apps[appNames[app]].appGraph.vis);
				}
				else{
					var visControl =registry.byId("visEnvApp"+type);
					var temp  =envGraphs[envList[env]].apps[appNames[app]];
					visControl.setSpecification(envGraphs[envList[env]].apps[appNames[app]][type].vis);
				}
			}
		};
		//testing functions
		logVisJSONs=function(){

		};
	});
});