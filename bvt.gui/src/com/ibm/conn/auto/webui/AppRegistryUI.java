/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                        */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.conn.auto.webui;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.cloud.AppRegistryUICloud;

public abstract class AppRegistryUI extends ICBaseUI {

	public AppRegistryUI(RCLocationExecutor driver) {
		super(driver);
	}

	public static String add_Extension  = "css=a[id='addApplication']";
	public static String manual_Install_RadioBox  = "css=input[id='manualInstallRadioBox']";
	public static String select_Service_List  = "css=select[id='serviceList']:contains('Activity Stream')";
	public static String extension_Name  = "css=input[id='extName']";
	public static String icon_Url  = "css=input[id='iconUrl']";
	public static String ext_Url  = "css=input[id='extUrl']";
	public static String save_Add_Extenson_Form  = "css=a[id='saveAddExtensonForm']";
	
	public static AppRegistryUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  AppRegistryUICloud(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
}