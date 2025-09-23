package com.ibm.smartcloud.metrics.dashboard.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import com.ibm.smartcloud.metrics.dashboard.core.*;
import com.ibm.json.java.*;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/Servlet")
public class SCSBMetricsDashboardServlet extends HttpServlet {
	CloudantManager myManager;
	JSONArray apps;
	JSONArray envs;
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SCSBMetricsDashboardServlet() {
        super();
        this.apps = Service.getApplicationNames();
        this.myManager = new CloudantManager("properties/cloudant.properties",125);
        this.envs = myManager.getEnvironments();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		JSONObject answer = new JSONObject();
		answer.put("apps", apps);
		if(envs==null){
			envs=myManager.getEnvironments();
		}
		answer.put("envs", envs);
		out.print(answer);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//current accepted queryType values: Counts, areaApp, envApp OS, OSApp ClientApp, Bubble
	//numUnits is the number of units specified in the date
	//date values should be specified as "uniti": value, for example:
	//2014-6-23 would be "unit0":2014, "unit1":6, "unit2":23, "numUnits":3 (use startunit and endunit when getting counts over time)
	//unit is 0 indexed, 0 for year, 1 for month, etc.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("queryType");
		int numUnits = Integer.parseInt(request.getParameter("numUnits"));
		PrintWriter out = response.getWriter();
		String environment = request.getParameter("env");
		if(query.indexOf("area")>=0&&request.getParameter("startunit0")!=null){
			int[] startDate = new int[numUnits];
			int[] endDate = new int[numUnits];
			for(int i=0; i<numUnits;i++){
				startDate[i] = Integer.parseInt(request.getParameter("startunit"+i));
				endDate[i] = Integer.parseInt(request.getParameter("endunit"+i));
			}
			int unit = Integer.parseInt(request.getParameter("unit"));
			if(query.equals("areaApp")){
				out.print(myManager.getCountsOverTime(startDate, endDate,unit,environment,false));
			}
			else{
				out.print(myManager.getEnvCountsOverTime(startDate, endDate, unit,false));
			}
		}
		else {
			int [] date = new int[numUnits];
			for(int i=0;i<numUnits;i++){
				date[i] = Integer.parseInt(request.getParameter("unit"+i));
			}
			if(query.equals("Counts")){
				out.print(myManager.getCounts(date,environment));
			}
			else if(query.equals("OS")){
				out.print(myManager.getOS(date,environment));
			}
			else if(query.equals("OSApp")){
				int app = Integer.parseInt(request.getParameter("app"));
				out.print(myManager.getOSApp(date, app,environment));
			}
			else if(query.equals("Client")){
				out.print(myManager.getClient(date,environment));
			}
			else if(query.equals("ClientApp")){
				int app = Integer.parseInt(request.getParameter("app"));
				out.print(myManager.getClientApp(date,app,environment));
			}
			else if(query.equals("Bubble")){
				int app = Integer.parseInt(request.getParameter("app"));
				out.print(myManager.getAppBubble(date,app,environment));
			}
			else if(query.equals("areaApp")){
				out.print(myManager.getCountsOverTime(date,environment));
			}
			else if(query.equals("areaEnv")){
				out.print(myManager.getEnvCountsOverTime(date));
			}
			else {
				System.out.println(query);
				out.print("Invalid query: "+query);
			}
		}
	}

}
