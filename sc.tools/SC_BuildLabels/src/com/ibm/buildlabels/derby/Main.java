package com.ibm.buildlabels.derby;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static Connection connect = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static String temp = "";
	private static int num = 0;
	private static boolean multiTagged = false;
	private static List<Integer> numTags = new ArrayList<Integer>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		List<String> builds = new ArrayList<String>();
		List<String> buildLabels = new ArrayList<String>();
		builds.add("Commons");
		builds.add("Connections");
		builds.add("AC");
		builds.add("Shindig202");
		builds.add("Contacts");

		System.out.println("Retrieving build labels");

		for (String a : builds) {
			temp = getBuildLabelDerby(a);
			buildLabels.add(a + " = " + temp);
		}
		
		for(Integer b : numTags){
			if(b > 1){
				System.out.println("Builds tagged more than once");
				multiTagged = true;
			}
		}

		File file = new File("builds.properties");
		if (file.exists()) {
			file.delete();
			file.createNewFile();

		} else {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		for (String b : buildLabels) {
			bw.write(b);
			bw.write("\n");
		}
		
		if(multiTagged ==  true){
			bw.write("TaggedAgain = *");
		}

		bw.close();

	}

	public static String getBuildLabelDerby(String tableName) {
		String buildLabel = "No label retrieved from derby";
		String tagNumber = "No tags found";
		try {

			connect();
			PreparedStatement statement = connect
					.prepareStatement("SELECT * from " + tableName);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				buildLabel = resultSet.getString("buildname");
				tagNumber = resultSet.getString("num");
				System.out.println("Build label from derby: " + buildLabel +" tagged "+tagNumber+" times");
				num = Integer.parseInt(tagNumber);
				numTags.add(num);
				
			}
		} catch (Exception e) {
			// throw e;
			System.err.println("Error retrieveing build label from derby: "
					+ e.getMessage());
		} finally {
			close();
		}
		return buildLabel+" tagged "+tagNumber +" times";
	}

	private static void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public static void connect() {

		try {
			connect = DriverManager
					.getConnection("jdbc:derby://scautotagslave1.swg.usma.ibm.com/c:/Apache/db-derby-10.10.2.0-bin/bin/Tagged");
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
		}
	}

}
