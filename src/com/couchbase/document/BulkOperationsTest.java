/**  
 * @Project: couchbase
 * @Title: BulkOperationsTest.java
 * @Package com.couchbase.document
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 ÏÂÎç5:46:09
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
import rx.functions.Func1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.connect.ConnectionManager;

/**
 * @ClassName BulkOperationsTest
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class BulkOperationsTest {
	
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
	public void testBatchWithRxJava() {
		/*List<JsonDocument> foundDocs = Observable
				.just("id1", "id2")
				.flatMap(new Func1<String, Observable<JsonDocument>>() {
					public Observable<JsonDocument> call(String id) {
						// TODO Auto-generated method stub
						return bucket.async().get(id);
					}
				}).toList().toBlocking().single();
		
		for (int i = 0; i < foundDocs.size(); i++) {
			System.out.println(foundDocs.get(i).content());
		}*/
		
		List<String> list = new ArrayList<String>();
		list.add("id1");
		list.add("id2");
		List<JsonDocument> foundDocs = bulkGet(list);
		for (int i = 0; i < foundDocs.size(); i++) {
			System.out.println(foundDocs.get(i).content().getString("name"));
		}
	}
	
	public List<JsonDocument> bulkGet(final Collection<String> ids) {
		return Observable.from(ids)
				.flatMap(new Func1<String, Observable<JsonDocument>>() {
					public Observable<JsonDocument> call(String id) {
						// TODO Auto-generated method stub
						return bucket.async().get(id);
					}
				}).toList().toBlocking().single();
	}
}
