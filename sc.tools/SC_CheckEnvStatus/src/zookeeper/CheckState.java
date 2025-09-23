package zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class CheckState {

	public static final String READY_STATE = "READY";
	public static final String FAILED_STATE = "FAILED";
	public static final String STALE_STATE = "STALE";
	public static final String STARTED_STATE = "STARTED";
	
	/**
	 * 
	 * Retrieve the state of a node within the SC env using Zookeeper
	 * 
	 * @param s : url for zookeeper
	 * @param s1 : path of node required
	 * @return
	 * @throws IOException
	 */
	public static String getNodeState(String s, String s1) throws IOException
    {
        boolean flag = true;
        EmptyWatcher emptywatcher = new EmptyWatcher();
        ZooKeeper zookeeper = null;
        String s2 = null;
        zookeeper = new ZooKeeper(s, 10000, emptywatcher);
        
        for(int i = 0; flag && i < 3; i++)
        {
            flag = false;
            try
            {
                s2 = new String(zookeeper.getData(s1, emptywatcher, null), "UTF-8");
                System.out.println(s2);
                continue;
            }
            catch(org.apache.zookeeper.KeeperException.ConnectionLossException connectionlossexception)
            {
                StringBuilder stringbuilder = new StringBuilder();
                stringbuilder.append("ConnectionLossException in getData(");
                stringbuilder.append(s);
                stringbuilder.append(", ");
                stringbuilder.append(s1);
                stringbuilder.append("). Will make a maximum of ");
                stringbuilder.append(3);
                stringbuilder.append(" attempts to connect.");
                stringbuilder.append(connectionlossexception);
                System.out.println(stringbuilder.toString());
                flag = true;
                try
                {
                    Thread.sleep(3000L);
                }
                catch(InterruptedException interruptedexception2) { }
                continue;
            }
            catch(KeeperException keeperexception)
            {
                StringBuilder stringbuilder1 = new StringBuilder();
                stringbuilder1.append("KeeperException in getData(");
                stringbuilder1.append(s);
                stringbuilder1.append(", ");
                stringbuilder1.append(s1);
                stringbuilder1.append(")");
                stringbuilder1.append(keeperexception);
                System.out.println(stringbuilder1.toString());
            }
            catch(InterruptedException interruptedexception1)
            {
                System.out.println(interruptedexception1.getMessage());
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                System.out.println(unsupportedencodingexception.getMessage());
            }
        }

        try
        {
            zookeeper.close();
        }
        catch(InterruptedException interruptedexception)
        {
            System.out.println(interruptedexception.getMessage());
        }
        if(s2 == null)
            throw new IOException("Unable to retrieve node state.");
        else
            return s2;
    	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		boolean ready = true;
		
		try {
			String acnode1 = getNodeState("zookeeper1a.lcscdev.swg.usma.ibm.com:2181", "/status/activation/acnode1a.lcscdev.swg.usma.ibm.com/state");
			String acnode2 = getNodeState("zookeeper1a.lcscdev.swg.usma.ibm.com:2181", "/status/activation/acnode2a.lcscdev.swg.usma.ibm.com/state");
			String acdmgra = getNodeState("zookeeper1a.lcscdev.swg.usma.ibm.com:2181", "/status/activation/acdmgra.lcscdev.swg.usma.ibm.com/state");
			String surveys = getNodeState("zookeeper1a.lcscdev.swg.usma.ibm.com:2181", "/status/activation/surveysprimarya.lcscdev.swg.usma.ibm.com/state");
			System.out.println("Current status of ac node 1 is " + acnode1);
			System.out.println("Current status of ac node 2 is " + acnode2);
			System.out.println("Current status of acdmgr node is " + acdmgra);
			System.out.println("Current status of surveys node is " + surveys);
			
			if (acnode1.toUpperCase().compareTo(READY_STATE) == 0){
				System.out.println("AC Node1 is ready for use.");
			}else{
				System.err.println("AC Node1 is not in a ready state.");
				ready = false;
			}
			if (acnode2.toUpperCase().compareTo(READY_STATE) == 0){
				System.out.println("AC Node2 is ready for use.");
			}else{
				System.err.println("AC Node2 is not in a ready state.");
				ready = false;
			}
			if (acdmgra.toUpperCase().compareTo(READY_STATE) == 0){
				System.out.println("AC Dmgr is ready for use.");	
			}else{
				System.err.println("AC Dmgr is not in a ready state.");
				ready = false;
			}
			if (surveys.toUpperCase().compareTo(READY_STATE) == 0){
				System.out.println("Surveys Node is ready for use.");
			}else{
				System.err.println("Surveys Node is not in a ready state.");
				ready = false;
			}
			
			if (ready == false){
				System.exit(-1);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static class EmptyWatcher implements Watcher {

		public void process(WatchedEvent watchedevent) {
		}

		private EmptyWatcher() {
		}

	}
	
}
