package com.couchbase.document;



import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.deps.io.netty.util.CharsetUtil;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.BinaryDocument;
import com.couchbase.client.java.document.JsonArrayDocument;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.SerializableDocument;
import com.couchbase.client.java.document.StringDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.connect.ConnectionManager;

public class DocumentTest extends TestCase {
	
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
	public void testInsert() {
		JsonArray friends = JsonArray.empty().add(JsonObject.empty().put("name", "zhangsan"))
							.add(JsonObject.empty().put("name", "jesse"));
		
		JsonObject content = JsonObject.empty()
			    .put("firstname", "Walter")
			    .put("lastname", "White")
			    .put("age", 52)
			    .put("aliases", JsonArray.from("Walt Jackson", "Mr. Mayhew", "David Lynn"))
			    .put("friends", friends);
		
		JsonDocument walter = JsonDocument.create("user:walter", content);
		bucket.upsert(walter);
		
		JsonArray content2 = JsonArray.from("hello", "World", 1234);
		bucket.upsert(JsonArrayDocument.create("docWithArray", content2));
	}
	
	@Test
	public void testQuery() {
		JsonDocument walter = bucket.get("user:walter");
		JsonArrayDocument docwithArray = bucket.get("docWithArray", JsonArrayDocument.class);
		
		System.out.println(walter);
		System.out.println("docwithArray: " + docwithArray);
	}
	
	@Test
	public void testBinaryDocument() {
		
		//create buffer out of a string
		ByteBuf toWriteBuf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
		
		//write it
		bucket.upsert(BinaryDocument.create("binaryDoc", toWriteBuf));
		
		//read it back
		BinaryDocument read = bucket.get("binaryDoc", BinaryDocument.class);
		
		//print it
		System.out.println(read);
		System.out.println(read.content().toString(CharsetUtil.UTF_8));
	}
	
	public void testSerializableDocument() {
		bucket.upsert(SerializableDocument.create("user:mechael",new User("Michael")));
		SerializableDocument doc = bucket.get("user:mechael", SerializableDocument.class);
		System.out.println(doc);
		System.out.println(((User)doc.content()).getUsername());
	}
	
	/*
	 *用于创建一个非json字符串
	 */
	public void testStringDocument() {
		// Create the document
		bucket.upsert(StringDocument.create("stringDoc", "Hello World"));

		// Prints:
		// StringDocument{id='stringDoc', cas=1424054670330, expiry=0, content=Hello World}
		System.out.println(bucket.get("stringDoc", StringDocument.class));
	}

}
























