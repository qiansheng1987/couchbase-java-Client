/**  
 * @Project: couchbase
 * @Title: CreateDocumentTest.java
 * @Package com.couchbase.document
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 下午2:30:50
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.document;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.connect.ConnectionManager;

import junit.framework.TestCase;

/**
 * @ClassName CreateDocumentTest
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class CreateDocumentTest extends TestCase {
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
	public void testUpsert() {
		JsonObject content = JsonObject.empty().put("name", "Michael");
		JsonDocument doc = JsonDocument.create("docId", content);
		//JsonDocument inserted = bucket.upsert(doc);
		bucket.upsert(doc, ReplicateTo.TWO);
	}
	
	@Test
	public void testUpsert2() {
		JsonObject content1 = JsonObject.empty().put("name", "Michael");
		JsonDocument doc1 = JsonDocument.create("docId", content1);
		Observable<JsonDocument> inserted1 = bucket.async().upsert(doc1);
	}
	
	/*
	 * A combination of just() and flatMap() is used to store them without blocking
	 * 非阻塞的批量添加
	 */
	@Test
	public void testBatching() {
		JsonObject content = JsonObject.empty().put("name", "Michael");
		JsonDocument doc1 = JsonDocument.create("id1", content);
		JsonDocument doc2 = JsonDocument.create("id2", content);
		JsonDocument doc3 = JsonDocument.create("id3", content);
		
		Observable.just(doc1, doc2, doc3)
		.flatMap(new Func1<JsonDocument, Observable<JsonDocument>>() {
			public Observable<JsonDocument> call(JsonDocument document) {
				return bucket.async().insert(document);
			}
		}).subscribe();
	}
	
}
