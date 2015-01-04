/**  
 * @Project: couchbase
 * @Title: test.java
 * @Package com.couchbase
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2014-12-31 ÏÂÎç2:29:56
 * @Copyright: 2014 
 * @version V1.0  
 */

package com.couchbase;

import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import com.couchbase.client.CouchbaseClient;

/**
 * @ClassName test
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2014-12-31
 */

public class test {

	/** 
	 * @Title: main 
	 * @Description: TODO
	 * @param @param args     
	 * @return void     
	 * @throws 
	 */
	public static void main(String[] args) {
		System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
		
		ArrayList<URI> nodes = new ArrayList<URI>();
	    // Add one or more nodes of your cluster (exchange the IP with yours)
	    nodes.add(URI.create("http://172.18.68.77:8091/pools"));
	    //nodes.add(URI.create("http://172.18.68.81:8091/pools"));

	    // Try to connect to the client
	    CouchbaseClient client = null;
	    try {
	      client = new CouchbaseClient(nodes, "S", "111111");
	      //Map<SocketAddress, Map<String, String>> map = client.getStats();
	      //System.out.println(map.toString());
	    } catch (Exception e) {
	      System.err.println("Error connecting to Couchbase: " + e.getMessage());
	      System.exit(1);
	    }
	}

}
