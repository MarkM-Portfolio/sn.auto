// load rave build layer
require(["com/ibm/vis/main", "dojo/dojo-core-layer"], function() {
	//once we have the rave layer loaded, we can use all rave modules
	require(["com/ibm/init/ready", "com/ibm/vis/template/Template","dojo/parser", "dijit/registry", "dojo/_base/lang", "dojo/on","dijit/form/Button","dijit/form/ComboBox", "com/ibm/vis/widget/VisControl", "dojo/_base/event"], function(ready, template, parser, registry, lang, on) {
		//ready function is needed to ensure the rave system is properly loaded
		var widget;
		//variables to submit to server initially
		var unit;
		var startDate;
		var endDate;
		
		//query variables
		var unique = false;
		var apps = [0];
		var type = "area";
		
		ready(function() {
			parser.parse();					
			widget = registry.byId("visControl");
			widget.initRenderer().then(function(w) {
				widget.setSpecificationFromUrl("data/HealthCareStocksPercent.json");
			});
			on( widget, "mousemove", lang.hitch(this, "visOnMouseMove"));
			widget.on("mousemove", visOnMouseMove);
		});
		//console.log(createVisJSON(0,0,0,[1,2]));
		changeFile = function() {
			var url = "data/" + document.getElementById("raveSpec").value;
			//var url = "data/"+x.value;
			var visControl = registry.byId("visControl");
			visControl.setSpecificationFromUrl(url);
		}
		visOnMouseMove = function(dojoEvent) {
			var visControl = registry.byId("visControl");
			
			var interaction = visControl.getInteractivity();
			var item = interaction.getTooltipItem(dojoEvent.clientX, dojoEvent.clientY);
			if(item!=null){
				var string = item.tooltipText();
				showFloatingTooltip(dojoEvent,string);
				return;
			}
			hideFloatingTooltip();
		}
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
		}
		hideFloatingTooltip = function() {
			if (this.tooltipDiv)
				this.tooltipDiv.style.display = "none";
		}
		toggleTooltips = function() {
			tooltips = !tooltips;
			if(tooltips)
				document.getElementById("tooltipbutton").innerHTML = "Hide Tooltips";
			else {
				hideFloatingTooltip();
				document.getElementById("tooltipbutton").innerHTML = "Show Tooltips";
			}
		}
		changeUnit = function(){
			var unit = document.getElementById("timeUnit").value);
			console.log(unit);
			if(unit!=""){
				document.getElementById("timePeriod").removeAttribute("disabled");
			}
		}
		makeToolTipDiv = function() {
			this.tooltipDiv = document.createElement("div");
			this.tooltipDiv.style.position = "absolute";
			this.tooltipDiv.style.backgroundColor= "white";
			this.tooltipDiv.style.display = "none";
			this.tooltipDiv.style.zIndex = 1000;
			document.body.appendChild(this.tooltipDiv);
		};
	});
});