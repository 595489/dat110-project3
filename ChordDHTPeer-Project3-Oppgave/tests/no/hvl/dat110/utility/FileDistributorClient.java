package no.hvl.dat110.utility;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.FileManager;
import no.hvl.dat110.util.Util;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */


public class FileDistributorClient {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		doDistribute();
	}
	
	public static void doDistribute() throws NoSuchAlgorithmException, IOException {
		// use this node to distribute files to active peers
		String path = "./files/";														// absolute path to the files
		String[] files = {"file1.txt","file2.txt","file3.txt","file4.txt","file5.txt"}; // we just limit to 5 files
		
		String node1 = "process1";														// this is the peer we want to use to resolve and distribute files
		NodeInterface p1 = Util.getProcessStub(node1, 9091);	 						// Look up the registry for the remote stub for process1
		
		FileManager fm = new FileManager(p1, Util.numReplicas);							// get the filemanager
		
		for(int i=0; i<files.length; i++) {												// iterate over the files and distribute them to the running nodes
			fm.setFilepath(path+files[i]);
			fm.readFile();
			fm.distributeReplicastoPeers();												// distribute the replicas to active peers
		}
	}
	
	

}
