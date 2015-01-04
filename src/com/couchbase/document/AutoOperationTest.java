/**  
 * @Project: couchbase
 * @Title: AutoOperationTest.java
 * @Package com.couchbase.document
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 ÏÂÎç4:08:48
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.JsonLongDocument;
import com.couchbase.client.java.document.LegacyDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.connect.ConnectionManager;

import junit.framework.TestCase;

/**
 * @ClassName AutoOperationTest
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class AutoOperationTest extends TestCase {

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
	public void testCounter() {
		Observable<JsonLongDocument> doc = bucket.async().counter("id", 5);
		bucket.async().counter("user::id", 1, 1)
		.map(new Func1<JsonLongDocument, String>() {
			public String call(JsonLongDocument counter) {
				// TODO Auto-generated method stub
				return "user::" + counter.content();
			}
		}).flatMap(new Func1<String, Observable<JsonDocument>>() {
			public Observable<JsonDocument> call(String id) {
				// TODO Auto-generated method stub
				return bucket.async().insert(JsonDocument.create(id, JsonObject.empty()));
			}
		}).subscribe();
	}
	
	@Test
	public void testAppend() {
		bucket.async().replace(LegacyDocument.create("doc", "Hello, "))
		.flatMap(new Func1<LegacyDocument, Observable<LegacyDocument>>() {
			public Observable<LegacyDocument> call(LegacyDocument document) {
				// TODO Auto-generated method stub
				return bucket.async().append(LegacyDocument.create("doc", "World!"));
			}
		}).flatMap(new Func1<LegacyDocument, Observable<LegacyDocument>>() {
			public Observable<LegacyDocument> call(LegacyDocument document) {
				// TODO Auto-generated method stub
				return bucket.async().get(document);
			}
		}).toBlocking().forEach(new Action1<LegacyDocument>() {

			public void call(LegacyDocument Ldoc) {
				// TODO Auto-generated method stub
				System.out.println(Ldoc.content());
			}
			
		});
	}
	
	
	@Test
	public void testdis() {
		LegacyDocument Ldoc = bucket.get("doc", LegacyDocument.class);
		System.out.println(Ldoc.content());
	}
	
}






















