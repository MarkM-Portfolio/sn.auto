require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready",
	         "com/ibm/vis/template/Template",
	         "dojo/parser", 
	         "dijit/registry", 
	         "dojo/_base/lang", 
	         "dojo/on", 
	         "dojo/request", 
	         "dojo/query", 
	         "dojo/dom",
	         "dojo/dom-construct", 
	         "dojo/dom-class",
	         "dojo/dom-attr", 
	         "dojo/dom-style",
	         "dijit/WidgetSet",
	         "com/ibm/vis/interaction/ChangeEffects", 
	         "com/ibm/vis/interaction/ChangeEffect",
	         "com/ibm/vis/interaction/EffectTarget",
	         "dojo/io-query",
	         "dijit/form/Button",
	         "dijit/form/ComboBox", 
	         "com/ibm/vis/widget/VisControl", 
	         "dojo/_base/event"], 
	         function(ready, template, parser, registry, lang, on, request, 
	        		 query, dom, domConstruct, domClass, domAttr, domStyle, 
	        		 WidgetSet, ChangeEffects, ChangeEffect, EffectTarget,
	        		 ioQuery) {
		//ready function is needed to ensure the rave system is properly loaded
		var actualApps = new Array();
		var actualNumApps = 0;
		var appList=new Array();//Array to hold Application List
		var envList = new Array();
		var palette= new Array();
		var apps = new Array(); //Array to hold indices of apps currently being shown
		var graphData = {loaded:false, vis:{},data:{}};//Object to hold visJSON spec and other info about graph
		var appData = new Array();
		var currentData = new Array();//array for rows of data currently being used
		var numApps = 0;//number of apps
		var numEnvs = 0;
		var numSegs = 24;//number of time segments
		var unique = true;//showing unique vs. total
		var titleString = "Unique Users ";//for use in header
		var dateString = "on ";//for use in header
		var reload =false;//whether or not to re-run the query
		var totalsOn = true;// whether or not to show totals line
		var totalsElement = {};// object to hold rave element for totals line
		var environment = null;
		var environmentString = "";
		var unit="Day";
		var pickerType = "Single";
		var pageHash = window.location.hash;
		var userQuery = ioQuery.queryToObject(pageHash.substring(1));
		var showNavbar = false;
		console.log(userQuery);

		showLoading=function(){
			query(".loading").forEach(function(node){
				domStyle.set(node,"display","");
			});
		};
		//REMOVE LATER
		//var badApps = [0,1,4,5,11,13,14,16];
		
		//to be done on page load;
		
		//first get applications from servlet
		showLoading();
		request("ProdServlet")
		.then(function(data){
			var parsedData = JSON.parse(data);
			//initialize variable based on data from servlet
			numApps = parsedData.apps.length;
			envList = parsedData.environments;
			numEnvs = envList.length;
			appList = new Array(numApps);
			apps = new Array();
			palette = new Array(numApps);
			actualApps = new Array();
			for(var i=0;i<numApps;i++){
				if(parsedData.apps[i]==null){
					appList[i] = "";
					palette[i]="white";
				} else{
					actualApps.push(i);
					appList[i] = parsedData.apps[i].name;
					apps.push(i);
					palette[i] = parsedData.apps[i].color;
				}
			}
			actualNumApps = actualApps.length;
			appData = new Array(numApps);
			//load app checkboxes
			loadAppBoxes();
			loadEnvSelect();

			processQuery();
			
			if(showNavbar){
				domStyle.set("visDash","min-height",(window.innerHeight-110)+"px");
			} else{
				domStyle.set("visDash","min-height",(window.innerHeight-60)+"px");
			}
			//then request base visJSON file
			request("json/vitalsAreaAndTotal.json")
			.then(function(data){
				graphData.vis = JSON.parse(data);
				//add palette and app list
				graphData.vis.grammar[0].elements[0].color[0].palette = palette;
				graphData.vis.data[0].fields[1].categories = appList;
				//ready vis widget
				ready(function(){
					parser.parse();
					var widget=registry.byId("visDash");
					widget.resizeToWindow=true;
					//set up tooltips
					on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
					//submit currently selected date
					submitDate();
					
					//transition doesn't actually look very good
					
//					var effects = widget.getChangeEffects();
//					var transition = effects.makeTransitionEffect(500);
//					effects.setChangeEffect(transition,0);
					//not using currently
					//on( widget, "click", lang.hitch(this,"visOnClick"));
				});
				
				//grab element from visJSON
				totalsElement = graphData.vis.grammar[0].elements[1];
			});
		});
		
		//set datepicker defaults
		$.fn.datepicker.defaults.format = "mm-dd-yyyy";//date format
		$.fn.datepicker.defaults.endDate = "today";//can't pick future date, could also be set to "-1d" for yesterday 
		$.fn.datepicker.defaults.autoclose = "true"; //sets autoclose
		$.fn.datepicker.defaults.weekStart = "1";// week starts on Monday
		$('.datepicker').datepicker({}); //initialize datepicker
		$('.datepicker').datepicker("update","07-01-2014");//set date to 6/23/14, should be -1d in the future
		
		
		
		//function called when mousing over vis widget
		visOnMouseMove = function(dojoEvent) {
			var found=false;//whether or not found item with tooltip
			var widget = registry.byId("visDash");//get widget from html
			
			//Get item from widget
			var interaction = widget.getInteractivity();
			var item = interaction.getTooltipItem(dojoEvent.pageX, dojoEvent.pageY);
			
			
			if(item!=null){//if user is mousing over an element with a tooltip
				var string = item.tooltipText();//tooltip string
				found=true;
				showFloatingTooltip(dojoEvent,string);//calls tooltip function
				return;
			}
			if(!found)
				hideFloatingTooltip();//hide tooltip otherwise
		};
		//used code from http://vottrave.ottawa.ibm.com/documentation/rave/raveJSGuide.xml#ravejs_tooltips
		showFloatingTooltip = function(event, txt){
			if(!this.tooltipDiv)//if there is already a tooltip on the page
				makeToolTipDiv();
			//sets tooltip location
			this.tooltipDiv.style.top = (event.pageY + 20)+"px";
			this.tooltipDiv.style.left = (event.pageX)+"px";
			this.tooltipDiv.style.display = "block";
			//sets tooltip text
			this.tooltipDiv.innerHTML = txt;
		};
		//function too hide tooltip
		hideFloatingTooltip = function() {
			if (this.tooltipDiv)//if there is a tooltip
				this.tooltipDiv.style.display = "none";
		};
		//function to create a tooltip
		makeToolTipDiv = function() {
			//creates a tooltip appended to the body of the html page
			this.tooltipDiv = domConstruct.create("div",null,document.body);
			//sets style
			this.tooltipDiv.style.position = "absolute";
			this.tooltipDiv.style.backgroundColor= "white";
			this.tooltipDiv.style.display = "none";
			this.tooltipDiv.style.zIndex = 1000;
		};
		
		//function to load app checkboxes
		loadAppBoxes = function(){
			for(var i=0; i<actualNumApps;i++){//for each app
				//wrapper div with custom class
				var tempDiv = domConstruct.create("div", {class:"checkbox myDiv"},"appSelectors");
				//wrapper label with custom class
				var tempLabel=domConstruct.create("label",{id:"app"+i,style:{background:palette[actualApps[i]]},
					class:"panel myBox",innerHTML:appList[actualApps[i]]},tempDiv);
				domConstruct.create("input",{name:"box",type:"checkbox",checked:true},tempLabel,"first");
				//add checkbox before the label text
				
			}
		};
		loadEnvSelect = function(){
			for(var i=0;i<numEnvs;i++){
				domConstruct.create("option",{innerHTML:envList[i]},"envSelect");
			}
		};
		//function to get graph for selected date
		submitDate = function(){
			if(unit=="Day"){
				userQuery.unit = "day";
			} else if(unit=="Month"){
				userQuery.unit="month";
			} else {
				userQuery.unit="range";
			}
			showLoading();
			var requestData = {};//object to hold data to send to servlet
			requestData.type="dashboard";
			//get UTC date from datepicker, must use UTC!
			var date;
			var startDate;
			var endDate;
			if(pickerType=="Single"){
				date = $('.datepicker').datepicker("getUTCDate");
				//get units from date, month is zero indexed, it is important to use UTC
				userQuery.date=$('.datepicker')[0].value;
				delete userQuery.startDate;
				delete userQuery.endDate;
				requestData.unit0 = date.getUTCFullYear();
				requestData.unit1 = date.getUTCMonth()+1;
				if(unit=="Day"){
					requestData.unit2 = date.getUTCDate();
					dateString = "on "+$('.datepicker')[0].value;
				} else{
					dateString = "in "+$('.datepicker')[0].value;
				}
			} else{
				startDate = $('#rangeStart').datepicker("getUTCDate");
				endDate = $('#rangeEnd').datepicker("getUTCDate");
				delete userQuery.date;
				userQuery.startDate = $('#rangeStart')[0].value;
				userQuery.endDate = $('#rangeEnd')[0].value;
				requestData.startUnit0 = startDate.getUTCFullYear();
				requestData.startUnit1 = startDate.getUTCMonth()+1;
				requestData.startUnit2 = startDate.getUTCDate();
				requestData.endUnit0 = endDate.getUTCFullYear();
				requestData.endUnit1 = endDate.getUTCMonth()+1;
				requestData.endUnit2 = endDate.getUTCDate()+1;
				dateString = "from "+$('#rangeStart')[0].value+" to " +$('#rangeEnd')[0].value;
			}
			if(environment!=null){
				requestData.environment = environment;
				userQuery.env = environment;
			} else{
				userQuery.env = "all";
			}
			userQuery.navbar = showNavbar;
			userQuery.reload=reload;
			window.location.hash = "#"+ioQuery.objectToQuery(userQuery);
			//whether or not to reload graph data
			requestData.reload = reload;
			requestData.unit = unit;
			//date formatted for the header
			if(environment==null){	
				environmentString ="";
			}
			else{
				environmentString = "for "+environment+" ";
			}

			//send request to Servlet
			request("ProdServlet",{
				method:"POST",
				data : requestData
			})
			.then(function(data){
				//only update header when data is returned
				//not entirely correct if another request is done before one finishes
				dom.byId("graph-title").innerHTML=titleString+environmentString+dateString;
				var parsedData = JSON.parse(data);
				
				//store data arrays outside of vis
				graphData.data = parsedData;
				//this says that there is some data in the visJSON
				numSegs= parsedData.area.numSegments;
				
				appData = parsedData.area.data;
				console.log(appData);
				//appData = parsedData.area.data;
				graphData.loaded=true;
				//put totals rows into visJSON
				graphData.vis.data[1].rows = graphData.data.total;
				if(unit=="Day"){
					graphData.vis.data[0].fields[0].format.datePattern="HH:mm z";
					graphData.vis.data[1].fields[0].format.datePattern="HH:mm z";
				} else{
					graphData.vis.data[0].fields[0].format.datePattern="MMMM d";
					graphData.vis.data[1].fields[0].format.datePattern="MMMM d";
				}
				//then load the graph
				updateRows();
			});
		};
		changeEnv = function(){
			if(dom.byId("envSelect").value=="All Environments"){
				environment = null;
			}
			else{
				environment = dom.byId("envSelect").value;
			}
		};
		
		changeUnit = function(){
			if(dom.byId("unitSelect").value!=unit){
				unit = dom.byId("unitSelect").value;
				loadDatepicker();
			}
		};
		
		getPickerType = function(pickerUnit){
			if(pickerUnit=="Date Range"){
				return "Range";
			} else{
				return "Single";
			}
		};
		loadDatepicker = function(){
			var minView;
			var dateFormat;
			var updateDate;
			if(unit=="Month"){
				minView = 1;
				dateFormat = "MM yyyy";
				updateDate = "-1m";
			} else {
				minView = 0;
				dateFormat = "mm-dd-yyyy";
				updateDate="07-01-2014";
			}
			pickerType = getPickerType(unit);
			if(pickerType=="Single"){
				domConstruct.create("input",{class:"datepicker form-control","data-provide":"datepicker"},"datepicker","only");
				$('.datepicker').datepicker({minViewMode:minView,format:dateFormat});
				$('.datepicker').datepicker("update",updateDate);
			}
			else {
				var inputGroup = domConstruct.create("div",{class:"input-group input-daterange"},"datepicker","only");
				domConstruct.create("input",{class:"datepicker form-control","data-provide":"datepicker",style:{width:"100px"},id:"rangeStart"},inputGroup);
				domConstruct.create("span",{class:"input-group-addon",innerHTML:"to"},inputGroup);
				domConstruct.create("input",{class:"datepicker form-control","data-provide":"datepicker",style:{width:"100px"},id:"rangeEnd"},inputGroup);
				$("#rangeStart").datepicker({minViewMode:minView,format:dateFormat});
				$("#rangeEnd").datepicker({minViewMode:minView,format:dateFormat});
				$("#rangeStart").datepicker("update","05-14-2014");
				$("#rangeEnd").datepicker("update","06-24-2014");
			}
			
		};
		//function to update apps array
		updateApps = function(){
			showLoading();
			apps = [];//reset apps
			var index=0;//counter
			query("[name=box]").forEach(function(node){//for each app checkbox
				if(node.checked){
					console.log(index);
					console.log(actualApps[index]);
					apps.push(actualApps[index]);//add index to apps array if checked
				}
				index++;//increment counter
			});
			updateRows();//then load graph
		};
		
		//function to toggle reload boolean
		toggleReload = function(){
			reload = !reload;
		};
		
		//function to toggle whether or not to show unique users or total hits
		toggleUnique = function(){
			showLoading();
			unique=!unique;//switch boolean
			if(unique){
				//change field references in visJSON
				graphData.vis.grammar[0].elements[0].position[0].field.$ref = "uhits";
				if(totalsOn){
					graphData.vis.grammar[0].elements[1].position[0].field.$ref = "tuhits";
				}
				else{
					totalsElement.position[0].field.$ref = "tuhits";
				}
				//change header string
				titleString = "Unique Users ";
				graphData.vis.grammar[0].coordinates.dimensions[0].axis.title[0]="Unique Users";
			}
			else{
				//change field references in visJSON
				graphData.vis.grammar[0].elements[0].position[0].field.$ref = "hits";
				if(totalsOn){
					graphData.vis.grammar[0].elements[1].position[0].field.$ref = "thits";
				}
				else{
					totalsElement.position[0].field.$ref = "thits";
				}
				//change header string
				titleString = "Total Hits ";
				graphData.vis.grammar[0].coordinates.dimensions[0].axis.title[0]="Total Hits";
			}
			//set header
			dom.byId("graph-title").innerHTML=titleString+environmentString+dateString;
			//then load graph
			loadGraph();
		};
		
		//function to load graph
		loadGraph =function(){
			if(!graphData.loaded){//if there isn't any data yet
				hideLoading();
				return;
			}
			if(!totalsOn&&apps.length==0){
				//this combination would cause the page to hang completely, so return instead
				//it seems to be a bug in RAVE
				//the combination should create a blank graph, so it isn't a big problem that it breaks
				hideLoading();
				return;
			}
			//set rows to be currentData
			graphData.vis.data[0].rows = currentData;
			//select widget from html
			var visControl  = registry.byId("visDash");
			//set specification
			var tempDate = new Date();
			visControl.setSpecification(graphData.vis);
			console.log((new Date()).getTime()-tempDate.getTime());
			hideLoading();
		};
		updateRows = function(){
			showLoading();
			if(apps.length==0){
				//currentData is an empty array
				currentData = [];
				//set rows to currentData
				graphData.vis.data[0].rows = currentData;
			}
			//if there is at least one app selected
			else{ 
				currentData = appData[apps[0]];
				for(var i=1;i<apps.length;i++){
					currentData = currentData.concat(appData[apps[i]]);
				}
			}
			loadGraph();
		};
		//function to select all apps
		selectAll = function(){
			showLoading();
			//recreate apps array
			apps = new Array(actualNumApps);
			//add all apps to the apps array
			for(var i=0; i<actualNumApps;i++){
				apps[i]=actualApps[i];
			}
			//load graph
			updateRows();
			//check every app checkbox
			query("[name=box]").forEach(function(node){
				domAttr.set(node,"checked",true);
			});
		};
		
		//function to deselect all apps
		deselectAll = function(){
			showLoading();
			//recreate apps array
			apps = new Array(0);
			//uncheck every app checkbox
			query("[name=box]").forEach(function(node){
				domAttr.set(node,"checked",false);
			});
			//load graph
			updateRows();
		};
		
		//function to toggle totals line
		toggleTotals = function(){
			showLoading();
			//toggle boolean
			totalsOn = !totalsOn; 
			if(totalsOn){//if it should be displayed
				if(graphData.vis.grammar[0].elements.length<2){//if the element isn't there
					//add the element
					graphData.vis.grammar[0].elements.push(totalsElement);
				}
				else{//this case shouldn't happen but is here just in case
					graphData.vis.grammar[0].elements[1] = totalsElement;
				}
			}
			else{//if it shouldn't be displayed
				if(graphData.vis.grammar[0].elements.length==2){//if there are two elements in the grammar
					graphData.vis.grammar[0].elements.pop();//remove last element
				}
			}
			//load graph
			loadGraph();
		};
		hideLoading=function(){
			query(".loading").forEach(function(node){
				domStyle.set(node,"display","none");
			});
		};
		//function to log visJSON to console
		logVisJSON = function(){
			console.log(JSON.stringify(graphData.vis));
		};
		processQuery = function(){
			if(userQuery.unit=="day"){
				dom.byId("unitSelect").value = "Day";
			} else if(userQuery.unit=="month"){
				dom.byId("unitSelect").value="Month";
			} else if(userQuery.unit=="range"){
				dom.byId("unitSelect").value="Date Range";
			}
			if(userQuery.unit!=null){
				changeUnit();
			}
			if(pickerType=="Single"){
				if(userQuery.date!=null){
					$('.datepicker').datepicker("update",userQuery.date);
				}
			} else {
				if(userQuery.startDate!=null){
					$("#rangeStart").datepicker("update",userQuery.startDate);
				} 
				if(userQuery.endDate!=null){
					$("#rangeEnd").datepicker("update",userQuery.endDate);
				}
			}
			if(userQuery.env=="all"){
				dom.byId("envSelect").value = "All Environments";
			} else if(userQuery.env!=null){
				dom.byId("envSelect").value = userQuery.env;
			}
			if(userQuery.navbar=="false"){
				showNavbar=false;
			} else{
				showNavbar=true;
			}
			toggleNavbar();
			if(userQuery.env!=null){
				changeEnv();
			}
			if(userQuery.reload=="true"){
				reload=true;
				domAttr.set("reload","checked",true);
			} else{
				reload=false;
				domAttr.remove("reload","checked");
			}
		};
		toggleNavbar=function(){
			if(showNavbar){
				query("nav").forEach(function(node){
					domStyle.set(node,"display","");
				});
				query("body").forEach(function(node){
					domStyle.set(node,"padding-top","51px");
				});
				query("[name=box]").forEach(function(node){
					domStyle.set(node,"display","");
				});
				query(".mybtn").forEach(function(node){
					domStyle.set(node,"display","");
				});
				domStyle.set("totalsDiv","display","");
			} else {
				query("nav").forEach(function(node){
					domStyle.set(node,"display","none");
				});
				query("body").forEach(function(node){
					domStyle.set(node,"padding-top","0px");
				});
				query("[name=box]").forEach(function(node){
					domStyle.set(node,"display","none");
				});
				query(".mybtn").forEach(function(node){
					domStyle.set(node,"display","none");
				});
				domStyle.set("totalsDiv","display","none");
			}
		};
	});
});