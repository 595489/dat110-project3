package no.hvl.dat110.utility;

import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.hvl.dat110.rpc.interfaces.NodeInterface;
import no.hvl.dat110.util.Util;

/**
 * exercise/demo purpose in dat110
 * You can use this class to validate the correctness of the chord ring. 
 * Are the successors and predecessors correct?
 * Are the assigned keys to each node correct?
 * For each process(node/peer), the program computes the expected succ/pred and keys. It then compares
 * these values with the actual values (succ/pred/keys) of the active node. The result is true 
 * if actual = expected for all values and false otherwise.
 * @author tdoy - Tosin D. Oyetoyan
 *
 */


public class RingMonitor {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		String node1 = "process1";														// contact an active node (any active node will do)
		int port = 9091;																// port on which the registry holding the stub is listening on
		NodeInterface p1 = Util.getProcessStub(node1, port);	 						// Look up the registry for each remote object
		
		// resolve the nodes and collect their status in a map		
		List<NodeInterface> activepeers = new ArrayList<>();
		findSuccessors(p1, activepeers);
		
		activepeers.sort(Comparator.comparing(t -> {
			try {
				return t.getNodeID();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			return null;
		}));
		activepeers.forEach(p -> {
			try {
				System.out.println(p.getNodeName()+" | "+p.getNodeID());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		Set<BigInteger> keys = collectKeys(activepeers);
		System.out.println("Key size = "+keys.size());
		
		Map<String, Boolean> succpred = validateSuccPred(activepeers);
		Map<String, Boolean> keyvalidity = validateKeys(keys, activepeers);
		
		boolean ressuccpred = true;
		boolean reskeyvalidity = true;
		for(Boolean b : succpred.values()) {
			ressuccpred = Boolean.logicalAnd(b, ressuccpred);
		}
		for(Boolean b : keyvalidity.values()) {
			reskeyvalidity = Boolean.logicalAnd(b, reskeyvalidity);
		}
		
		System.out.println("Succ/Pred validation results per node |"+succpred+"\n All correct = "+ressuccpred);
		System.out.println("File keys validation results per node |"+keyvalidity+"\n All correct = "+reskeyvalidity);
		
	}
	
	private static void findSuccessors(NodeInterface node, List<NodeInterface> activepeers) throws RemoteException {
		
		activepeers.add(node);
		BigInteger succid = node.getNodeID().add(new BigInteger("1")); 					// get the succid of (nodestub+1)
		NodeInterface cpeer = node.findSuccessor(succid);
		
		if(!findDuplicates(activepeers, cpeer.getNodeName())) {
			findSuccessors(cpeer, activepeers);
		}
			
	}
	
	private static boolean  findDuplicates(List<NodeInterface> activepeers, String nodeip) throws RemoteException {

		for(NodeInterface p1 : activepeers) {
			if(p1.getNodeName().equals(nodeip))
				return true;
		}
		
		return false;
	}
	
	private static Set<BigInteger> collectKeys(List<NodeInterface> activepeers) {
		Set<BigInteger> keys = new HashSet<>();
		
		activepeers.forEach(p -> {
			try {
				keys.addAll(p.getNodeKeys());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		return keys;
	}
	
	private static Map<String, Boolean> validateSuccPred(List<NodeInterface> activepeers) throws RemoteException {
		Map<String, Boolean> result = new HashMap<>();
		for(int i=0; i<activepeers.size(); i++) {
			NodeInterface node = activepeers.get(i);
			// actual
			String asucc = node.getSuccessor().getNodeName();
			String apred = node.getPredecessor().getNodeName();
			// expected
			int indexpluss = i+1;
			if(indexpluss == activepeers.size())
				indexpluss = 0;
			int indexminus = i-1;
			if(indexminus == -1)
				indexminus = activepeers.size()-1;
			String esucc = activepeers.get(indexpluss).getNodeName();
			String epred = activepeers.get(indexminus).getNodeName();
			
			// validate actual vs. expected
			if(asucc.equals(esucc) && apred.equals(epred))
				result.put(node.getNodeName(), true);
			else 
				result.put(node.getNodeName(), false);
			
		}
		
		return result;
	}
	
	private static Map<String, Boolean> validateKeys(Set<BigInteger> keys, List<NodeInterface> activepeers) throws RemoteException {
		
		Map<String, Boolean> result = new HashMap<>();
		for(int i=0; i<activepeers.size(); i++) {
			NodeInterface node = activepeers.get(i);

			// expected
			int indexminus = i-1;
			if(indexminus == -1)
				indexminus = activepeers.size()-1;
	
			NodeInterface epred = activepeers.get(indexminus);
			
			// actual
			List<BigInteger> nodekeys = new ArrayList<>(node.getNodeKeys());
			List<BigInteger> asortedkeys = nodekeys.stream().sorted().collect(Collectors.toList());
			
			// expected
			// distribute keys based on pred(node) < key <= node
			List<BigInteger> enodekeys = new ArrayList<>();
			BigInteger predkey = epred.getNodeID();
			BigInteger nodekey = node.getNodeID();
			keys.forEach(k -> {				
				if(Util.computeLogic(k, predkey.add(new BigInteger("1")), nodekey)) {
					enodekeys.add(k);
				}
			});
			List<BigInteger> esortedkeys = enodekeys.stream().sorted().collect(Collectors.toList());
			
			System.out.println(node.getNodeName()+" | "+esortedkeys+" | "+asortedkeys);
			
			Collections.sort(asortedkeys);
			Collections.sort(esortedkeys);
			boolean eq = asortedkeys.equals(esortedkeys);			
			
			result.put(node.getNodeName(), eq);
			
		}
		
		return result;
	}

}
