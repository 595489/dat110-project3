package no.hvl.dat110.utility;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.FileManager;
import no.hvl.dat110.util.Util;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */


public class FileFinderClient {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		// use this node to find active peers holding these files
		String[] files = {"file1","file2","file3","file4","file5"}; 				// we just limit to 5 files
		
		String node2 = "process2";													// this is the peer we want to use to resolve and find files. Note, we can use any peer
		int port = 9092;
		NodeInterface p2 = Util.getProcessStub(node2, port);	 					// Look up the registry for the remote stub for process2
		
		FileManager fm = new FileManager(p2, Util.numReplicas);						// get the filemanager
		
		for(int i=0; i<files.length; i++) {											
			fm.requestActiveNodesForFile(files[i]);									// lookup the replica and return all active peers holding it
			System.out.println();
			System.out.println("=======");
			fm.printActivePeers();
			System.out.println("=======");
		}
	}
	
	

}
