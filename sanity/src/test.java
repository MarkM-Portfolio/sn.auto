import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.ibm.json.java.JSONObject;




public class test {
	public static void main(String [] args) throws IOException {
		String s = "C:\\Users\\IBM_ADMIN\\workspace\\sn.auto\\lwp\\build\\sanity\\sanity\\ldapConf.json";
		JSONObject json = JSONObject.parse(new BufferedReader(new FileReader(s)));
		System.out.println(((Map)json.get("params")).get("LDAP_FILTER"));
	}	
}
