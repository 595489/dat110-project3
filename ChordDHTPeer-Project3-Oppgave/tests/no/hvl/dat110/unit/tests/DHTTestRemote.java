package no.hvl.dat110.unit.tests;


import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.hvl.dat110.middleware.Message;
import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.FileManager;
import no.hvl.dat110.util.Util;

class DHTTestRemote {
	
	@BeforeEach
	void setUp() throws Exception {
		

	}

	@Test
	void test() throws InterruptedException, RemoteException {
		
		/** This is a client **/
		/** start -> Client -> update(m) -> p1 -> replicasofm(m)[2,3,4] -> Client -> locatePrimary[2, 3, 4] -> primary=4
		 * -> update(m)[4] -> 4 -> distributeUpdate(m)[2, 3, 4] - > end **/
		// retrieve the process stubs to be contacted by the client
		NodeInterface p1 = Util.getProcessStub("process1", 9091);
		
		FileManager fm = new FileManager(p1, Util.numReplicas);
		String filename = "file3";
		String newupdate = "overwrite the content of this existing file - i.e. file3";
		
		Set<Message> activepeers = fm.requestActiveNodesForFile(filename);  // 2, 3 ,4 are holding file2
		
		/** Find the primary for file3 */
		NodeInterface primary = fm.findPrimaryOfItem();
		
		System.out.println("Primary = "+primary.getNodeName());
		
		// update file
		primary.requestRemoteWriteOperation(newupdate.getBytes(), primary, activepeers);
		
		
		// retrieve the current file content from all replicas holding file2
		activepeers.forEach(peer -> {
			String name = peer.getNodeIP();
			int port = peer.getPort();
			BigInteger fileid = peer.getHashOfFile();			
			NodeInterface p = Util.getProcessStub(name, port);
			
			Message m = null;
			try {
				m = p.getFilesMetadata(fileid);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			assertArrayEquals(m.getBytesOfFile(), newupdate.getBytes());
			
		});
		
	
	}

}
