package no.hvl.dat110.util;

/**
 * project 3
 * @author tdoy
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Hash { 
	
	private static BigInteger hashint;
	private static int digest;
	
	public static BigInteger hashOf(String entity) {		
		
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(entity.getBytes());
			byte[] dig = md.digest(entity.getBytes("utf8"));
			digest = dig.length*8;
			String hex = toHex(dig);

			hashint = new BigInteger(hex, 16);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e){

			e.printStackTrace();
		}
		
		return hashint;
	}
	
	public static BigInteger addressSize() {
		
		// Task: compute the address size of MD5
		
		// get the digest length
		
		// compute the number of bits = digest length * 8
		
		// compute the address size = 2 ^ number of bits
		
		// return the address size

		BigInteger size = BigInteger.valueOf((long) Math.pow(2, digest));

		return size;
	}
	
	public static int bitSize() {

		//int digestlen = 0;

		// find the digest length

		//return digestlen*8;
		return digest;
	}
	
	public static String toHex(byte[] digest) {
		StringBuilder strbuilder = new StringBuilder();
		for(byte b : digest) {
			strbuilder.append(String.format("%02x", b&0xff));
		}
		return strbuilder.toString();
	}

}
