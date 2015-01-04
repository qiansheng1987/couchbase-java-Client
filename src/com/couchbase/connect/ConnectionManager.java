/**  
 * @Project: couchbase
 * @Title: ConnectionManager.java
 * @Package com.couchbase.connect
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 ÉÏÎç10:11:16
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.connect;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

/**
 * @ClassName ConnectionManager
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class ConnectionManager {
	
	private static final ConnectionManager connectionManager = new ConnectionManager();
	private static Cluster cluster = null;
	
	public static ConnectionManager getInstance() {
		return connectionManager;
	}
	
	public static Bucket createConnect() {
		CouchbaseEnvironment environment = DefaultCouchbaseEnvironment.
				builder().queryEnabled(true).build();
		List<String> nodes = Arrays.asList("172.18.68.81","172.18.68.77");
		cluster = CouchbaseCluster.create(environment, nodes);
		Bucket bucket = cluster.openBucket("test", "");
		return bucket;
	}
	
	public static void disconnect() {
		cluster.disconnect();
	}
}



















