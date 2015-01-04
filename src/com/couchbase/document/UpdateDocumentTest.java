/**  
 * @Project: couchbase
 * @Title: UpdateDocumentTest.java
 * @Package com.couchbase.document
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 ÏÂÎç3:15:06
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.document;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.connect.ConnectionManager;

import junit.framework.TestCase;

/**
 * @ClassName UpdateDocumentTest
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class UpdateDocumentTest extends TestCase {
	
	public Bucket bucket;
	
	@Before
	protected void setUp() throws Exception {
		bucket = ConnectionManager.getInstance().createConnect();
	}

	@After
	protected void tearDown() throws Exception {
		ConnectionManager.disconnect();
	}
	
	@Test 	 	
	public void testReplace() {
		JsonObject content = JsonObject.empty().put("name", "Michael");
		JsonDocument doc = JsonDocument.create("docId", content);
		Observable<JsonDocument> inserted = bucket.async().replace(doc);
	}
	
	@Test
	public void testReadAndTouch() {
		// Get and set the new expiration time to 4 seconds
		Observable<JsonDocument> doc = bucket.async().getAndTouch("id2", 100000);
	}
	
	@Test
	public void testDelete() {
		Observable<JsonDocument> doc2 = bucket.async().remove("id3");
		System.out.println(doc2);
	}
	
}



















