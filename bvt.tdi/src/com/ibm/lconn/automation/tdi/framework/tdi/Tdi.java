package com.ibm.lconn.automation.tdi.framework.tdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.testng.Assert;

public class Tdi {
	
	private String tdiPath;
	
	public Tdi(String tdiPath){
		this.tdiPath = tdiPath;
	}
	
	public String runCollectDns() {
		String script = (System.getProperty("os.name").contains("Windows")) ? "\\collect_dns.bat":"/collect_dns.sh";

		String result = null;
		try {
			result = runScript(tdiPath + script);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Runs SyncAllDns script
	 * @return After synchronization results
	 */
	public String runSyncAllDns() {
		String script = (System.getProperty("os.name").contains("Windows")) ? "\\sync_all_dns.bat":"/sync_all_dns.sh";
		
		String result = null;
		
		try {
			result = runScript(tdiPath + script, "After synchronization");
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		return result;
			
	}
	
	/**
	 * Runs populate_from_dn_file script
	 * @return After operation results
	 */
	public String runPopulate() {
		String script = (System.getProperty("os.name").contains("Windows")) ? "\\populate_from_dn_file.bat":"/populate_from_dn_file.sh";
		
		String result = null;
		try {
			result = runScript(tdiPath + script, "After operation");
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		return result;
	}
	
	private String runScript(String scriptPath) throws IOException, InterruptedException {
		return runScript(scriptPath, null);
	}
	
	private String runScript(String scriptPath, String lookFor) throws IOException, InterruptedException {
		try {
			String result = null;
			String line;
			ProcessBuilder builder = new ProcessBuilder(scriptPath);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));
			String exc = null;
			while ((line = input.readLine()) != null) {
				if(lookFor != null){
					if(line.contains(lookFor)){
						result = line;
					}
				}
				if(line.contains("Exception")) {
					exc = line;
				}
				System.out.println(line);
			}
			
			if(exc != null) {
				Assert.fail(exc);
			}

			input.close();
			p.destroy();
			//p.waitFor();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} //catch (InterruptedException e) {
			//e.printStackTrace();
			//throw e;
		//}
	}

}
