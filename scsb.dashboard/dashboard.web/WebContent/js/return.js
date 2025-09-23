require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", 
	         "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", 
	         "dojo/query", "dojo/dom","dojo/dom-construct", "dojo/dom-class",
	         "dojo/dom-attr","dojo/dom-style","dijit/WidgetSet", "dojo/io-query",
	         "dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", 
	         "dojo/_base/event"], 
	         function(ready, template, parser, registry, lang, on, request, query, dom, 
	        		 domConstruct, domClass,domAttr,domStyle, WidgetSet, ioQuery) {
		//ready function is needed to ensure the rave system is properly loaded
		var graphData = {loaded:false, barVis:{}, pieVis:{}, data:{}};//Object to hold visJSON spec and other info about graph
		var numSegs = 0;//number of time segments
		var titleString = "Return Rate for ";//for use in header
		var dateString = "";//for use in header
		var reload =false;//whether or not to re-run the query
		var type = "bar";
		var showNavbar=true;
		var userQuery = ioQuery.queryToObject(window.location.hash.substring(1));
		//to be done on page load;

		$.fn.datepicker.defaults.format = "yyyy";//date format
		$.fn.datepicker.defaults.autoclose = "true"; //sets autoclose
		$.fn.datepicker.defaults.minViewMode = 2;
		$.fn.datepicker.defaults.startView = 2;
		$('.datepicker').datepicker({}); //initialize datepicker
		$('.datepicker').datepicker("update","today");//set date to 6/23/14, should be -1d in the future
		//request base visJSON file
		request("json/vitalsBar.json")
		.then(function(data){
			graphData.barVis = JSON.parse(data);
			//add palette and app list
			
			//grab element from visJSON
		
			request("json/vitalsReturnPie.json")
			.then(function(data){
				graphData.pieVis = JSON.parse(data);
				//ready vis widget
				ready(function(){
					parser.parse();
					var widget=registry.byId("visBar");
					widget.resizeToWindow=true;
					//set up tooltips
					on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
	
					widget=registry.byId("visPie");
					widget.resizeToWindow=true;
					//set up tooltips
					on( widget, "mousemove", lang.hitch(this,"visOnMouseMove"));
					//submit currently selected date
					if(userQuery.date!=null){
						$('.datepicker').datepicker("update",new Date(userQuery.date,1,1));
					}
					if(userQuery.navbar=="false"){
						showNavbar=false;
						toggleNavbar();
					} else if(userQuery.navbar=="true"){
						showNavbar=true;
						toggleNavbar();
					}
					submitDate();
					//not using currently
					//on( widget, "click", lang.hitch(this,"visOnClick"));
				});
			});
		});
		
		//set datepicker defaults
		
		
		
		//function called when mousing over vis widget
		visOnMouseMove = function(dojoEvent) {
			var found=false;//whether or not found item with tooltip
			var widget = registry.byId("visBar");//get widget from html
			
			//Get item from widget
			var interaction = widget.getInteractivity();
			var item = interaction.getTooltipItem(dojoEvent.pageX, dojoEvent.pageY);
			if(item==null){
				widget= registry.byId("visPie");
				interaction =widget.getInteractivity();
				item = interaction.getTooltipItem(dojoEvent.pageX, dojoEvent.pageY);
			}
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
			showLoading();
			var requestData = {};//object to hold data to send to servlet
			requestData.type="environments";
			//get UTC date from datepicker, must use UTC!
			var date = $('.datepicker').datepicker("getUTCDate");
			//get units from date, month is zero indexed, it is important to use UTC
			requestData.unit0 = date.getUTCFullYear();
			userQuery.date = $('.datepicker')[0].value;
			userQuery.navbar = showNavbar;
			window.location.hash = "#"+ioQuery.objectToQuery(userQuery);
			//whether or not to reload graph data
			requestData.reload = reload;
			requestData.type=type;
			//date formatted for the header
			dateString = date.getUTCFullYear();
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
				graphData.loaded=true;
				graphData.barVis.data[0].rows=parsedData.data;
				graphData.barVis.data[0].fields[0].categories = parsedData.months;
				graphData.pieVis.data[0].rows = parsedData.pie;
				graphData.pieVis.data[0].fields[0].categories = parsedData.buckets;
				//only update header when data is returned
				//not entirely correct if another request is done before one finishes
				//store data arrays outside of vis
				
				//then load the graph
				loadGraph();
			});
		};
		
		//function to toggle reload boolean
		toggleReload = function(){
			reload = !reload;
		};
		
		
		//function to load graph
		loadGraph =function(){
			if(!graphData.loaded){//if there isn't any data yet
				hideLoading();
				return;
			}
			//select widget from html
			var visControl  = registry.byId("visBar");
			//set specification
			var tempDate = new Date();
			visControl.setSpecification(graphData.barVis);
			console.log((new Date()).getTime()-tempDate.getTime());
			visControl = registry.byId("visPie");
			tempDate = new Date();
			visControl.setSpecification(graphData.pieVis);
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
			console.log(JSON.stringify(graphData.barVis));
			console.log(JSON.stringify(graphData.pieVis));
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