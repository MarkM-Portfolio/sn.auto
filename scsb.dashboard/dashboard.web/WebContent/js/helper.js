//function creates a new JSON variable given the units of time, the start time the end time, and the apps to be examined, this is only a pie chart version right now.
console.log(apps);
function createVisJSON(timeunit,timeStart,timeEnd,apps){
	var visJSON = {"apps":apps,"data":[{}],"grammar":[{}],"size":{"width":400,"height":400},"version": "6.0"};
	visJSON.data[0].fields=[{"id":"time","unit":"timestamp"},{"id":"app","label":"App"},{"id":"uniqueHits","label":"Unique Hits"},{"id":"totalHits","label":"All Hits"}];
	visJSON.data[0].fields[1].categories = new Array(apps.length);
	visJSON.appMap = new Array(masterAppList.length);
	for(var i = 0; i<apps.length;i++){
		visJSON.data[0].fields[1].categories[i] = masterAppList[apps[i]];
		visJSON.appMap[apps[i]]=i;
	}
	var grammar = visJSON.grammar[0];
	grammar.coordinates={"dimensions" : [{"axis":{}},{"axis":{}}],"style":{"fill":"white"},"transforms":[{"type":"polar"}]};
	grammar.elements = [{}];
	grammar.elements[0].type = "interval";
	grammar.elements[0].position = [{"field":{"$ref":"totalHits"}},{"field":{"$ref":"app"}}];
	grammar.elements[0].color = [{"field":{"$ref":"app"}}];
	return visJSON;
}