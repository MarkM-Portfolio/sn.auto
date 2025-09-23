// load rave build layer
require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", "dijit/registry", "dojo/_base/lang", "dojo/on", "dojo/request", "dojo/query","dojo/dom-construct","dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", "dojo/_base/event"], function(ready, template, parser, registry, lang, on, request,query,domConstruct) {
		//ready function is needed to ensure the rave system is properly loaded
		var widget;
		var visArea;
		var visLine;
		var visPie;

		//variables to submit to server initially
		var unit="hour";
		var startTime;
		var endTime;
		var visEndTime;
		
		var pie = false;
		var graphType = "area";
		var pieRows;
		var data=[];
		var rows;
		var graphRadios=document.getElementsByName("graphOption");
		$('.btn').button();
		
		//query variables
		var unique = false;
		
		//var type = "area";
		//$("[id='uniqueHits']").bootstrapSwitch();
		//internal variables
		masterAppList = ["Activities",
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
		                 "Other"];
		//var masterAppMap = [0,1,3,8,9,11,13,15,16,17,19];
		var apps = new Array(masterAppList.length);
		for(var i=0;i<masterAppList.length;i++){
			apps[i]=i;
		}
		var numApps = masterAppList.length;
		var order = new Array(numApps);
		for(var i=0;i<numApps;i++){
			order[i]=numApps-i-1;
		}
		var range = false;
		var received = false;
		var visLoaded = 0;
		
		/*var palette = [
		                "#005D99",
		                "#009987",
		                "#007527",
		                "#CC8F00",
		                "#D9631F",
		                "#B01611",
		                "#D93D9C",
		                "#742299",
		                "#1483C7",
		                "#00B8A4",
		                "#0E9425",
		                "#EBB300",
		                "#EB7D2A",
		                "#D13838",
		                "#F562BC",
		                "#9A34C7",
		                "#38B1F2",
		                "#5FD4C5",
		                "#4FBD4F",
		                "#F5D118",
		                "#F79F4A",
		                "#EA5E5E",
		                "#FF8CD3",
		                "#C462F5"
		              ];*/
		var palette=[["#82D1F5",
			          "#008ABF",
			          "#003F69",
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
			          "#3B0256"],
			          ["white"]];
		
		$.fn.datepicker.defaults.format = "mm/dd/yyyy";
		$.fn.datepicker.defaults.endDate = "today";
		$.fn.datepicker.defaults.autoclose = "true";
		$.fn.datepicker.defaults.weekStart = "1";
		
		//load hour as default
		createDatePicker();
		$("#dayPicker").datepicker("update","-1d");//yesterday as default

		
		request("json/sampleVitalsVisJSON.json")
			.then(function(data){
				visLoaded++;
				visArea=JSON.parse(data);
				visArea.data[0].fields[0].categories=masterAppList;
				visArea.data[0].fields[0].order=order;
				visArea.grammar[0].elements[0].color[0].palette=palette[0];
				//console.log(visArea);
				changeVisUnit();
				submitDates();
			});
		
		request("json/vitalsPie.json")
			.then(function(data){
				visPie=JSON.parse(data);
				visPie.data[0].fields[0].categories=masterAppList;
				visPie.grammar[0].elements[0].color[0].palette=palette[0];
			});
		
		request("json/vitalsLine.json")
			.then(function(data){
				visLoaded++;
				visLine=JSON.parse(data);
				visLine.data[0].fields[0].categories=masterAppList;
				visLine.grammar[0].elements[0].color[0].palette=palette[0];
				changeVisUnit();
			});
		
		function loadAppBoxes(){
			var temp ='<div class="row"><div class="col-lg-3">';
			for(var i=0;i<masterAppList.length/2;i++){
				temp+='<div class="checkbox myDiv"><div><label style="background:'+palette[0][i]+'" id="app'+i+'" class="panel myBox"><input name="box" type="checkbox" checked onclick="updateApps()">';
				temp+=masterAppList[i];
				temp+='</label></div></div>';
			}
			temp+='</div><div class="col-lg-3">';
			for(var i=Math.ceil(masterAppList.length/2);i<masterAppList.length;i++){
				temp+='<div class="checkbox myDiv"><div><label style="background:'+palette[0][i]+'" id="app'+i+'" class="panel myBox"><input name="box" type="checkbox" checked onclick="updateApps()">';
				temp+=masterAppList[i];
				temp+='</label></div></div>';
			}
			temp+='</div></div>';
			document.getElementById("appBoxes").innerHTML=temp;
		}


		loadAppBoxes();
		appBoxes=document.getElementsByName("box");
		//console.log(appBoxes);
		ready(function() {
			parser.parse();					
			widget = registry.byId("visControl");
			/*widget.initRenderer().then(function(w) {
				widget.setSpecificationFromUrl("data/HealthCareStocksPercent.json");
			});*/
			widget.resizeToWindow=true;
			on( widget, "mousemove", lang.hitch(this, "visOnMouseMove"));
			//widget.on("mousemove", visOnMouseMove);
			on( widget, "click", lang.hitch(this,"visOnClick"));
		});
		
		visOnClick = function(dojoEvent) {
			var visControl=registry.byId("visControl");
			var interaction = visControl.getInteractivity();
			//var item = interaction.getMetaItem(dojoEvent.clientX,dojoEvent.clientY);
			var item = interaction.getTooltipItem(dojoEvent.pageX,dojoEvent.pageY);
			if(item!=null){
				if(apps.length==1)
					selectAll();
				else{
					//var curApp = item.meta().substring(7);
					var curApp = item.tooltipText();
					console.log(curApp);
					var appId = masterAppList.indexOf(curApp);
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
			var visControl = registry.byId("visControl");
			
			var interaction = visControl.getInteractivity();
			var item = interaction.getTooltipItem(dojoEvent.pageX, dojoEvent.pageY);
			if(item!=null){
				var string = item.tooltipText();
				showFloatingTooltip(dojoEvent,string);
				return;
			}
			hideFloatingTooltip();
		};
		//used code from http://vottrave.ottawa.ibm.com/documentation/rave/raveJSGuide.xml#ravejs_tooltips
		var tooltips = true;
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
		changeUnit = function(){
			if(unit==document.getElementById("timeUnit").value)
				return;
			unit = document.getElementById("timeUnit").value;
			//console.log(unit);
			createDatePicker();
			changeVisUnit();
		};
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
					tempDate = new Date();
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
			document.getElementById("appBoxes").removeAttribute("disabled");
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
			times["queryType"]="over-time";
			console.log(times);
//			endDate.setUTCHours(23);
//			endDate.setUTCMinutes(59);
//			endDate.setUTCSeconds(59);
			endTime=endDate.getTime()/1000;
			if(unit=="hour")
				endDate.setUTCHours(-1);
			else if(unit=="day")
				endDate.setUTCDate(endDate.getUTCDate()-1);
			else if(unit=="week")
				endDate.setUTCDate(endDate.getUTCDate()-7);
			else if(unit=="month")
				endDate.setUTCMonth(endDate.getUTCMonth()-1);
			else
				endDate.setUTCFullYear(endDate.getUTCFullYear()-1);
			visEndTime=endDate.getTime()/1000;
			request("TestServlet",{
				method: "POST",
				data: times
			})
				.then(function(gotData){
					updateSpan();
					//console.log(gotData);
					data = JSON.parse(gotData);
					received=true;
					console.log(data);
					updateVisJSON();
				});
		};

		function updateRows(){
			rows=data[apps[0]];
			pieRows=new Array(apps.length);
			pieRows[0]=data[numApps][apps[0]];
			for(var i=1;i<apps.length;i++){
				var temp = rows.concat(data[apps[i]]);
				rows=temp;
				pieRows[i]=data[numApps][apps[i]];
			}
			//console.log(rows.toString());
		}
		updateSpan=function(){
			if(visLoaded<2)
				return;
			var scale = {"spans":[{"min":startTime,"max":visEndTime}]};
			visArea.grammar[0].coordinates.dimensions[1].scale=scale;
			visLine.grammar[0].coordinates.dimensions[1].scale=scale;
		};
		changeVisUnit=function(){
			if(visLoaded<2)
				return;
			var datePattern;
			if(unit=="hour"){
				datePattern = "hh:mm aaa";
			}
			else if(unit=="day"){
				datePattern="MMM d";
			}//TODO: put more date formats here
			visArea.data[0].fields[1].format={"datePattern":datePattern};
			visLine.data[0].fields[1].format={"datePattern":datePattern};
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
		loadVisJSON = function(){
			var visControl=registry.byId("visControl");
			
			if(graphType=="pie"){
				//console.log(JSON.stringify(visPie));
				visControl.setSpecification(visPie);
			}
			else if(graphType=="area"){
				console.log(JSON.stringify(visArea));
				visControl.setSpecification(visArea);
			}
			else{
				//console.log(JSON.stringify(visLine));
				visControl.setSpecification(visLine);
			}

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
		toggleUnique = function(){
			unique=!unique;
			if(unique){
				visArea.grammar[0].elements[0].position[0].field.$ref="uhits";
				visLine.grammar[0].elements[0].position[0].field.$ref="uhits";
				visPie.grammar[0].elements[0].position[0].field.$ref="fuhits";
				visPie.grammar[0].elements[0].tooltip[0].content[2].$ref="fuhits";
			}
			else{
				visArea.grammar[0].elements[0].position[0].field.$ref="hits";
				visLine.grammar[0].elements[0].position[0].field.$ref="hits";
				visPie.grammar[0].elements[0].position[0].field.$ref="fhits";
				visPie.grammar[0].elements[0].tooltip[0].content[2].$ref="fhits";
			}
			updateVisJSON();
		};
		switchGraph = function(){
			for(var i=0;i<graphRadios.length;i++){
				if(graphRadios[i].checked)
					graphType=graphRadios[i].value;
			}
			loadVisJSON();
		};
		togglePie = function(){
			pie=!pie;
			loadVisJSON();
			if(pie)
				document.getElementById("pieButton").innerHTML="View as Area Graph";
			else
				document.getElementById("pieButton").innerHTML="View as Pie Chart";
		};
	});
});