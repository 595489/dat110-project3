package no.hvl.dat110.unit.tests;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.hvl.dat110.middleware.Message;
import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.FileManager;
import no.hvl.dat110.util.Util;

class DHTTestFilePeers {
	
	@BeforeEach
	void setUp() throws Exception {
		

	}

	@Test
	void test() throws InterruptedException, RemoteException {
		
		// retrieve the process stubs to be contacted for finding replicas of file2
		NodeInterface p3 = Util.getProcessStub("process3", 9093);
		
		FileManager fm = new FileManager(p3, Util.numReplicas);
		String filename = "file2";
		
		Set<Message> activepeers = fm.requestActiveNodesForFile(filename);  // 2, 3 ,4 are holding file2
		
		// retrieve the actual file names from the peers
		List<String> actualpeers = new ArrayList<>();
		activepeers.forEach(peer -> {
			actualpeers.add(peer.getNodeIP());
			
		});
		
		List<String> expectedpeers = new ArrayList<>();
		expectedpeers.add("process2");
		expectedpeers.add("process3");
		expectedpeers.add("process4");
		expectedpeers.add("process4");
		
		
		// sort both lists
		
		Collections.sort(actualpeers);
		Collections.sort(expectedpeers);
		
		Assert.assertArrayEquals(expectedpeers.toArray(), actualpeers.toArray());
	
	}

}
