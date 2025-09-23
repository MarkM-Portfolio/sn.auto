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

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/ProdServlet")
public class SCSBMetricsProductionServlet extends HttpServlet {
	CloudantManager myManager;
	JSONArray apps;
	JSONArray environments;
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SCSBMetricsProductionServlet() {
        super();
        apps = Service.getApplicationNames();
        myManager = new CloudantManager("properties/cloudant.properties",125,true);
        //temp until Service contains environments
        environments = new JSONArray();
        environments.add("E3");
        environments.add("J3");
        environments.add("G3");
        myManager.setEnvironments(environments);
        
        
        //myManager holds old queries, so it must last for a long time
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //Get request returns application list from service
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		JSONObject toReturn = new JSONObject();
		toReturn.put("apps", apps);
		toReturn.put("environments", environments);
		out.print(toReturn.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//request should specify 3 units and reload, which is a boolean
	//date values should be specified as "uniti": value, for example:
	//2014-6-23 would be "unit0":2014, "unit1":6, "unit2":23
	//post returns the per app counts and the totals counts for all hours in the specified day
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		//intialize date array
		String type = request.getParameter("type");
		boolean reload =false;
		if(request.getParameter("reload")!=null){
			//parse reload from request
			reload = Boolean.parseBoolean(request.getParameter("reload"));
		}
		String environment = request.getParameter("environment");

		JSONObject toReturn = new JSONObject();
		if(type==null){
			toReturn.put("error","no type given");
		} else if(type.equals("bar")){
			int[] date = new int[1];
			date[0] = Integer.parseInt(request.getParameter("unit"+0));
			toReturn=JSONObject.parse(myManager.getCachedCustomerReturnData(date,reload));
			
		} else {
			String unit = request.getParameter("unit");
			boolean excludeIBM = false;
			if(type.equals("customers")){
				if(request.getParameter("excludeIBM")!=null){
					excludeIBM = Boolean.parseBoolean(request.getParameter("excludeIBM"));
				}
			}
			if(unit.equals("Date Range")){
				int[] startDate = new int[3];
				int[] endDate = new int[3];
				for(int i=0;i<3;i++){
					startDate[i] = Integer.parseInt(request.getParameter("startUnit"+i));
					endDate[i] = Integer.parseInt(request.getParameter("endUnit"+i));
				}
				if(type.equals("dashboard")){
					toReturn.put("area",JSONObject.parse(myManager.getCachedCountsOverTime(startDate,endDate,environment,reload)));
					toReturn.put("total",JSONArray.parse(myManager.getCachedTotalCountsOverTime(startDate,endDate,environment,reload)));
				}
				else if(type.equals("environments")){
					toReturn.put("area", JSONObject.parse(myManager.getCachedEnvCountsOverTime(startDate,endDate,reload)));
				}
				else if(type.equals("customers")){
					toReturn.put("area",JSONObject.parse(myManager.getCachedCustomerCountsOverTime(startDate,endDate, 10,excludeIBM,reload)));
				}
			} else {
				int[] date;
				if(unit.equals("Day")){
					date = new int[3];
				} else {
					date = new int[2];
				}
				for(int i=0; i<2;i++){
					//get date units from request
					date[i] = Integer.parseInt(request.getParameter("unit"+i));
				}
				if(unit.equals("Day")){
					date[2] = Integer.parseInt(request.getParameter("unit"+2));
				}
				//default reload value is false
				
				//get data from myManager
				
				if(type.equals("dashboard")){
					toReturn.put("area",JSONObject.parse(myManager.getCachedCountsOverTime(date,environment,reload)));
					toReturn.put("total",JSONArray.parse(myManager.getCachedTotalCountsOverTime(date,environment,reload)));
				}
				else if(type.equals("environments")){
					toReturn.put("area", JSONObject.parse(myManager.getCachedEnvCountsOverTime(date,reload)));
				}
				else if(type.equals("customers")){
					toReturn.put("area",JSONObject.parse(myManager.getCachedCustomerCountsOverTime(date, 10,excludeIBM,reload)));
				}
			}
		}
		//return as string
		out.print(toReturn.toString());
	}

}
