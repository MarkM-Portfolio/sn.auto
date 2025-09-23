require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", 
	         "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", 
	         "dojo/query", "dojo/dom", "dojo/dom-construct", "dojo/dom-class",
	         "dojo/dom-attr", "dojo/dom-style", "dijit/WidgetSet", "dojo/io-query",
	         "dijit/form/Button", "dijit/form/ComboBox", 
	         "com/ibm/vis/widget/VisControl", "dojo/_base/event"], 
	         function(ready, template, parser, registry, lang, on, request, query, 
	        		 dom, domConstruct, domClass,domAttr, domStyle,WidgetSet,ioQuery) {
		//ready function is needed to ensure the rave system is properly loaded
		var envList=new Array();//Array to hold Application List
		var palette= new Array();
		var graphData = {loaded:false, vis:{},data:{}};//Object to hold visJSON spec and other info about graph
		var numSegs = 0;//number of time segments
		var unique = true;//showing unique vs. total
		var titleString = "Unique Users ";//for use in header
		var dateString = "";//for use in header
		var reload =false;//whether or not to re-run the query
		var type = "environments";
		var unit = "Day";
		var pickerType="Single";
		var showNavbar = true;
		var excludeIBM=false;
		//to be done on page load;
		var userQuery = ioQuery.queryToObject(window.location.hash.substring(1));
		//first get applications from servlet
		request("ProdServlet")
		.then(function(data){
			var parsedData = JSON.parse(data);
			//initialize variable based on data from servlet
			numEnvs = parsedData.environments.length;
			envList = parsedData.environments;
			palette = new Array();
			for(var i=0;i<parsedData.apps.length;i++){
				if(parsedData.apps[i]!=null){
					palette.push(parsedData.apps[i].color);
				}
			}
			//then request base visJSON file
			request("json/vitalsArea.json")
			.then(function(data){
				graphData.vis = JSON.parse(data);
				graphData.vis.data[0].fields[0].format={datePattern:"HH:mm z"};
				graphData.vis.data[0].fields[0].timeZone="GMT";
				//add palette and app list
				graphData.vis.grammar[0].elements[0].color[0].palette = palette;
				graphData.vis.data[0].fields[1].categories = envList;
				//submit currently selected date
				//ready vis widget
				ready(function(){
					parser.parse();
					var widget=registry.byId("visEnv");
					widget.resizeToWindow=true;
					//set up tooltips
					on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
					processQuery();
					submitDate();
					//not using currently
					//on( widget, "click", lang.hitch(this,"visOnClick"));
				});
				//grab element from visJSON
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
			var widget = registry.byId("visEnv");//get widget from html
			
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
			requestData.excludeIBM = excludeIBM;
			//whether or not to reload graph data
			requestData.reload = reload;
			if(type=="customers"){
				userQuery.excludeIBM=excludeIBM;
			} else{
				delete userQuery.excludeIBM;
			}
			requestData.type=type;
			userQuery.type = type;
			userQuery.navbar = showNavbar;
			window.location.hash="#"+ioQuery.objectToQuery(userQuery);
			requestData.unit = unit;
			//send request to Servlet
			request("ProdServlet",{
				method:"POST",
				data : requestData
			})
			.then(function(data){
				dom.byId("graph-title").innerHTML=titleString+dateString;
				var parsedData = JSON.parse(data);
				graphData.data = parsedData;
				//this says that there is some data in the visJSON
				numSegs= parsedData.area.numSegments;
				graphData.loaded=true;
				graphData.vis.data[0].rows = parsedData.area.data;
				if(type=="environments"){
					graphData.vis.data[0].fields[1].categories=envList;
				}
				else{
					graphData.vis.data[0].fields[1].categories=parsedData.area.categories;
				}
				if(unit=="Day"){
					graphData.vis.data[0].fields[0].format.datePattern="HH:mm z";
				} else{
					graphData.vis.data[0].fields[0].format.datePattern="MMMM d";
				}
				//only update header when data is returned
				//not entirely correct if another request is done before one finishes
				
				//store data arrays outside of vis
				
				//then load the graph
				loadGraph();
			});
		};
		
		changeType = function(){
			type = dom.byId("typeSelect").value.toLowerCase();
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
				updateDate = "-2m";
			} else {
				minView = 0;
				dateFormat = "mm-dd-yyyy";
				updateDate="06-01-2014";
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
				//change header string
				titleString = "Unique Users for ";
				graphData.vis.grammar[0].coordinates.dimensions[0].axis.title[0]="Unique Users";
			}
			else{
				//change field references in visJSON
				graphData.vis.grammar[0].elements[0].position[0].field.$ref = "hits";
				//change header string
				titleString = "Total Hits for ";
				graphData.vis.grammar[0].coordinates.dimensions[0].axis.title[0]="Total Hits";
			}
			//set header
			dom.byId("graph-title").innerHTML=titleString+dateString;
			//then load graph
			loadGraph();
		};
		
		//function to load graph
		loadGraph =function(){
			if(!graphData.loaded){//if there isn't any data yet
				hideLoading();
				return;
			}
			//select widget from html
			var visControl  = registry.byId("visEnv");
			//set specification
			var tempDate = new Date();
			visControl.setSpecification(graphData.vis);
			console.log((new Date()).getTime()-tempDate.getTime());
			hideLoading();
		};
		
		showLoading=function(){
			query(".loading").forEach(function(node){
				domStyle.set(node,"display","");
			});
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
			if(userQuery.type=="environemnts"){
				dom.byId("typeSelect").value = "Environments";
			} else if(userQuery.type == "customers"){
				dom.byId("typeSelect").value = "Customers";
			}
			if(userQuery.excludeIBM=="false"){
				excludeIBM=false;
			} else if(userQuery.excludeIBM=="true"){
				excludeIBM=true;
			}
			if(userQuery.navbar=="false"){
				showNavbar=false;
				toggleNavbar();
			} else if(userQuery.navbar=="true"){
				showNavbar=true;
				toggleNavbar();
			}
			if(userQuery.type!=null){
				changeType();
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
			} else {
				query("nav").forEach(function(node){
					domStyle.set(node,"display","none");
				});
				query("body").forEach(function(node){
					domStyle.set(node,"padding-top","0px");
				});
			}
		};
	});
});