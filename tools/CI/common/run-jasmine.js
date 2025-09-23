/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2013                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

var system = require('system');

try {

	/**
	 * Wait until the test condition is true or a timeout occurs. Useful for waiting
	 * on a server response or for a ui change (fadeIn, etc.) to occur.
	 *
	 * @param testFx javascript condition that evaluates to a boolean,
	 * it can be passed in as a string (e.g.: "1 == 1" or "$('#bar').is(':visible')" or
	 * as a callback function.
	 * @param onReady what to do when testFx condition is fulfilled,
	 * it can be passed in as a string (e.g.: "1 == 1" or "$('#bar').is(':visible')" or
	 * as a callback function.
	 * @param timeOutMillis the max amount of time to wait. If not specified, 3 sec is used.
	 */

	function waitFor(testFx, onReady, timeOutMillis) {
		var maxtimeOutMillis = timeOutMillis ? timeOutMillis : 180001, //< Default Max Timeout
			start = new Date().getTime(),
			condition = false,
			interval = setInterval(function() {
				if ((new Date().getTime() - start < maxtimeOutMillis) && !condition) {
					// If not time-out yet and condition not yet fulfilled
					condition = (typeof(testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
				} else {
					if (!condition) {
						// If condition still not fulfilled (timeout but condition is 'false')
						console.log("'waitFor()' timeout");
						phantom.exit(1);
					} else {
						// Condition fulfilled (timeout and/or condition is 'true')
						console.log("'Specs' finished in " + (new Date().getTime() - start) + "ms.");
						typeof(onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
						clearInterval(interval); //< Stop this interval
					}
				}
			}, 30001); //< Default interval to check results
	};


	if (system.args.length !== 3) {
		console.log('Usage: run-jasmine.js URL xmlOutputFile');
		phantom.exit(1);
	}


	var page = require('webpage').create();

	// Route "console.log()" calls from within the Page context to the main Phantom context (i.e. current "this")
	page.onConsoleMessage = function(msg) {
		console.log("console message: " + msg);
	};
	page.onError = function(msg) {
		console.log("error message: " + msg);
	};
	page.onAlert = function(msg) {
		console.log("page alert: " + msg);
	};

	page.onInitialized = function() {
		console.log("****** About to execute initialization callback!");
		page.evaluate(function() {
			function elapsed(startTime, endTime) {
				return (endTime - startTime) / 1000;
			}

			function ISODateString(d) {
				function pad(n) {
					return n < 10 ? '0' + n : n;
				}
				return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + 'T' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
			}

			function trim(str) {
				return str.replace(/^\s+/, "").replace(/\s+$/, "");
			}

			function escapeInvalidXmlChars(str) {
				return str.replace(/\&/g, "&amp;").replace(/</g, "&lt;").replace(/\>/g, "&gt;").replace(/\"/g, "&quot;").replace(/\'/g, "&apos;");
			}

			var ResultNode = function(result, type, parent) {
					this.result = result;
					this.parent = parent;
					this.type = type;
					this.children = [];
					this.failureCount = 0;
					this.specCount = 0;
				};

			ResultNode.prototype.addChild = function(result, type) {
				var child = new ResultNode(result, type, this);
				this.children.push(child);
				if (child.type == "spec") this.addSpec();
				return child;
			};

			ResultNode.prototype.setFailed = function() {
				this.failureCount++;
				if (this.parent) this.parent.setFailed();
			};

			ResultNode.prototype.addSpec = function() {
				this.specCount++;
				if (this.parent) this.parent.addSpec();
			};


			// Generates JUnit XML for the given spec run.
			// Allows the test results to be used in java based CI
			// systems like CruiseControl and Hudson.
			// 
			// @param {string} savePath where to save the files
			// @param {boolean} consolidate whether to save nested describes within
			// the
			// same file as their parent; default: true
			// @param {boolean} useDotNotation whether to separate suite names with
			// dots rather than spaces (ie "Class.init" not
			// "Class init"); default: true
			var JUnitXmlReporter = function(savePath) {
					this.savePath = savePath || '';
					this.consolidate = false;
					this.useDotNotation = true;
					window["jasmine-result"] = "";
				};

			JUnitXmlReporter.finished_at = null; // will be updated after all
			// files have been written
			JUnitXmlReporter.prototype = {
				specStarted: function(result) {
					this.currentSpec = this.currentParent.addChild(result, "spec")
					this.currentSpec.timeStarted = new Date();
				},

				specDone: function(result) {
					this.currentSpec.timeFinished = new Date();
					if (result.status == "failed") this.currentSpec.setFailed();
				},

				suiteStarted: function(result) {
					this.currentParent = this.currentParent.addChild(result, "suite");
					this.currentParent.timeStarted = new Date();
				},

				suiteDone: function(result) {
					this.currentParent.timeFinished = new Date();
					if (this.currentParent.parent) this.currentParent = this.currentParent.parent;
				},

				jasmineStarted: function(opts) {
					this.rootNode = this.currentParent = new ResultNode({}, "suite", null);
					this.rootNode.timeStarted = new Date();
				},

				jasmineDone: function(opts) {
					this.rootNode.timeFinished = new Date();
					this.writeResults();
					window["jasmine-result"] = this.result;
				},

				write: function(str) {
					if (!this.result) this.result = "";
					this.result += str;
					this.result += "\n";
				},

				writeResults: function() {
					this.write('<?xml version="1.0" encoding="UTF-8" ?>');
					this.write("<testsuites>");
					this.writeChildren(this.rootNode);
					this.write("</testsuites>");
				},

				writeChildren: function(node) {
					for (var i = 0; i < node.children.length; i++) {
						var child = node.children[i];
						if (child.type == "suite") {
							this.writeSuite(child)
						} else if (child.type == "spec") {
							this.writeSpec(child)
						}
					}
				},

				writeSpec: function(spec) {
					var duration = elapsed(spec.timeStarted, spec.timeFinished);
					this.write("<testcase classname=\"" + spec.parent.result.fullName + "\" name=\"" + escapeInvalidXmlChars(spec.result.description) + "\" time=\"" + duration + "\">");
					if (spec.result.status == "failed") {
						for (var i = 0; i < spec.result.failedExpectations.length; i++) {
							var expectation = spec.result.failedExpectations[i];
							this.write("<failure type=\"" + trim(escapeInvalidXmlChars(expectation.message)) + "\" message=\"" + trim(escapeInvalidXmlChars(expectation.message)) + "\">");
							this.write(escapeInvalidXmlChars(expectation.stack));
							this.write("</failure>");
						}
					}
					this.write("</testcase>");
				},

				writeSuite: function(suite) {
					var duration = elapsed(suite.timeStarted, suite.timeFinished);
					this.write("<testsuite name=\"" + suite.result.fullName + "\" errors=\"0\" tests=\"" + suite.specCount + "\" failures=\"" + suite.failureCount + "\" time=\"" + duration + "\" timestamp=\"" + ISODateString(suite.timeStarted) + "\">");
					this.writeChildren(suite);
					this.write("</testsuite>");
				},

				log: function(str) {
					var console = jasmine.getGlobal().console;

					if (console && console.log) {
						console.log(str);
					}
				}
			};

			window.customReporter = new JUnitXmlReporter();

		});
	};

	console.log("opening page:", system.args[1]);

	page.open(system.args[1], function(status) {
		//console.log(status);// ADDED
		if (status !== "success") {
			console.log("Unable to access network");
			phantom.exit();
		} else {
			//console.log("in testFx"); // ADDED
			waitFor(function() {
				//console.log("in waitFor"); // ADDED
				return page.evaluate(function() {
					//console.log("in page.evaluate"); // ADDED
					return document.body.querySelector('.symbolSummary .pending') === null
				});
			}, function() {
				page.render('example.png');
				//console.log("in onReady"); // ADDED
				var ret /*exitCode*/
				= page.evaluate(function() {
					var log = []; //ADDED
					jasmine.getEnv().addReporter(window.customReporter);
					log.push("in page.evaluate"); // ADDED
					//throw(document.body.outerHTML);// ADDED
					console.log(document.body.outerHTML);// ADDED
					//log.push(document.body.querySelector('.description').innerText);
					var list = document.body.querySelectorAll('.results > .failures > .spec-detail.failed');
					if (list && list.length > 0) {
						console.log('');
						log.push(''); //ADDED
						console.log(list.length + ' test(s) FAILED:');
						log.push(list.length + ' test(s) FAILED:'); //ADDED
						for (i = 0; i < list.length; ++i) {
							var el = list[i],
								desc = el.querySelector('.description'),
								msg = el.querySelectorAll('.result-message');
							console.log('');
							log.push(''); //ADDED
							console.log(desc.title);
							log.push(desc.title); //ADDED
							for (j = 0; j < msg.length; ++j) {
								console.log(msg[j].innerText);
								log.push(msg[j].innerText); //ADDED
								console.log('');
								log.push(''); //ADDED
							}
						}
						return {
							code: 1,
							log: log.join('\n')
						};
					} else {
						//console.log(location.href);
						//console.log(document.documentElement.outerHTML);
						//console.log(document.body.querySelector('.alert > .passingAlert.bar').innerText);
						var log = '',
							sel = document.body.querySelector('.alert > .passed.bar');
						if (sel) {
							log = sel.innerText; //ADDED
							console.log(log); // CHANGED
						}
						return {
							code: 0,
							log: log
						};
					}
				});
				console.log("******** Return is : ", ret);

				var xml_results = page.evaluate(function() {
					return window["jasmine-result"] || 'no results';
				});

				var fs = require('fs');

				fs.write(system.args[2], xml_results, "w");

				console.log(ret.log);
				phantom.exit( /*exitCode*/ ret.code);
			},
			360000);
		}
	});

} catch (e) {
	console.log('execution failed: ' + e);
	phantom.exit(1);
}
