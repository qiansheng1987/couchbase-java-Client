/**  
 * @Project: couchbase
 * @Title: ObservableTest.java
 * @Package com.couchbase.observables
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-5 上午9:50:41
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.observables;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.ReplicaMode;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.connect.ConnectionManager;

import junit.framework.TestCase;

/**
 * @ClassName ObservableTest
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-5
 */

public class ObservableTest extends TestCase {
	
	public Bucket bucket;
	
	@Before
	public void setUp() throws Exception {
		bucket = ConnectionManager.getInstance().createConnect();
	}

	@After
	public void tearDown() throws Exception {
		ConnectionManager.disconnect();
	}
	
	@Test
	public void testConsumeObservable() {
		Observable.just("id1", "id2", "id3")
		.doOnNext(new Action1<String>() {
			public void call(String t1) {
				// TODO Auto-generated method stub
				if (t1.equals("id3")) {
					throw new RuntimeException("I dont like id3");
				}
			}
		})
		.subscribe(new Subscriber<String>() {
			public void onCompleted() {
				// TODO Auto-generated method stub
				System.out.println("Compelete Observable.");
				
			}
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				System.err.println("Whoops:" + e.getMessage());
			}
			public void onNext(String str) {
				// TODO Auto-generated method stub
				System.out.println("Got: " + str);
			}
		});
	}
	
	/*
	 * The subscribe method also returns a Subscription 
	 * which you can use to unsubscribe 
	 * and therefore do not receive further events
	 * Even if you don't unsubscribe explictly, 
	 * operations like take do that for you implicitly. 
	 * The following code only takes the first five values and 
	 * then unsubscribes
	 */
	@Test
	public void testSubscribe() {
		
		Observable
		.just("The", "Dave", "Brubeck", "Quartet", "Time", "Out")
		.take(4)
		.subscribe(new Subscriber<String>() {
			public void onCompleted() {
				// TODO Auto-generated method stub
				System.out.println("Compelete Observable.");
			}
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				System.err.println("Whoops:" + e.getMessage());
			}
			public void onNext(String t) {
				// TODO Auto-generated method stub
				System.out.println("Got: " + t);
			}
		});
		
		//如果不想继承Subscriber的所有方法，只是对某一个事件感兴趣，可以按照一下执行。
		/*Observable.just("The", "Dave", "Brubeck", "Quartet", "Time", "Out")
		.subscribe(new Action1<String>() {
			public void call(String t1) {
				// TODO Auto-generated method stub
				System.out.println("got: " + t1);
			}
		});*/
		
	}
	
	@Test
	public void testAsyncAndSync() throws InterruptedException {
		/*Observable.interval(1, TimeUnit.SECONDS)
		.subscribe(new Action1<Long>() {
			public void call(Long counter) {
				// TODO Auto-generated method stub
				System.out.println("Get: " + counter);
			}
		});*/
		
		//改进
		/* keep synchronize between different threads. 
		 * One thread counts down the latch, 
		 * the other one waits until it is counted down
		 **/
		final CountDownLatch latch = new CountDownLatch(6);
		Observable.interval(1, TimeUnit.SECONDS)
		.subscribe(new Action1<Long>() {
			public void call(Long counter) {
				latch.countDown();
				System.out.println("Get: " + counter);
			}
		});
		latch.await();
	}
	
	@Test
	public void testBlockAndNotBlock() {
		/*
		// this does not bock;
		BlockingObservable<Long> observable = Observable.interval(1, TimeUnit.SECONDS)
			.toBlocking();
		
		//this blocks is called for every emitted item
		observable.forEach(new Action1<Long>() {
			public void call(Long counter) {
				System.out.println("Got: " + counter);
			}
		});	
		*/
		
		/*或者是先建立异步的工作流，然后在结束的时候阻塞*/
		Observable
	    .interval(1, TimeUnit.SECONDS)
	    .take(5)
	    .toBlocking()
	    .forEach(new Action1<Long>() {
	        public void call(Long counter) {
	            System.out.println("Got: " + counter);
	        }
	    });
		
		
		/*int value = Observable.just(1).toBlocking().single();
		//int value = Observable.just(1, 3).toBlocking().single();//throws Sequence contains too many elements
		System.out.println("value: " + value);*/
		
		
		/*List<Integer> list = Observable.just(1, 2, 3)
				.toList()
				.toBlocking().single();
		System.out.println(list);*/
	}
	
	@Test
	public void testCreateObservable() {
		
		//Loads one document ans prints it;
		bucket.async().get("id1").subscribe(new Action1<JsonDocument>() {
			public void call(JsonDocument doc) {
				// TODO Auto-generated method stub
				System.out.println("doc: " + doc);
			}
		});
		
		// Loads 3 documents in parallel
		Observable
		    .just("id1", "id2")
		    .flatMap(new Func1<String, Observable<JsonDocument>>() {
		        public Observable<JsonDocument> call(String id) {
		            return bucket.async().get(id);
		        }
		    }).subscribe(new Action1<JsonDocument>() {
		        public void call(JsonDocument document) {
		            System.out.println("Got: " + document);
		        }
		    });
	}
	
	
	@Test
	public void testTransformObserable() {
		//note  flatMap() returns an Observable<T> whereas the normal map just returns <T>
		
		//use map()
		/*Observable.interval(1, TimeUnit.SECONDS)
		.take(4)
		.map(new Func1<Long, String>() {
			public String call(Long input) {
				if (input % 3 == 0) {
					return "Fizz";
				} else if (input % 5 == 0) {
					return "Buzz";
				}
				return Long.toString(input);
			}
		})
		.toBlocking()
		.forEach(new Action1<String>() {
			public void call(String output) {
				// TODO Auto-generated method stub
				System.out.println("output: " + output);
			}
		});*/
		
		
		//flatMap(), which allows you to do those transformations 
		//with asynchronous calles
		// Loads 3 documents in parallel
		/*Observable
		    .just("id1", "id2")
		    .flatMap(new Func1<String, Observable<JsonDocument>>() {
		        public Observable<JsonDocument> call(String id) {
		            return bucket.async().get(id);
		        }
		    }).subscribe(new Action1<JsonDocument>() {
		        public void call(JsonDocument document) {
		            System.out.println("Got: " + document);
		        }
		    });*/
		
		
		//use scan(), which applies a function to each value emitted by an Observable
		/*Observable.just(1, 2, 3, 4)
		.scan(new Func2<Integer, Integer, Integer>() {
			
			public Integer call(Integer sum, Integer value) {
				// TODO Auto-generated method stub
				return sum + value;
			}
		}).subscribe(new Action1<Integer>() {
			public void call(Integer integer) {
				System.out.println("sum: " + integer);
			}
		});*/
		
		
		//groupBy() comes in handy, which emits one Observable by each group, defined by a function
		Observable.just(1, 2, 3, 4, 5)
		.groupBy(new Func1<Integer, Boolean>() {
			public Boolean call(Integer integer) {
				// TODO Auto-generated method stub
				return integer % 2 == 0;
			}
		}).subscribe(new Action1<GroupedObservable<Boolean, Integer>>() {
			public void call(final GroupedObservable<Boolean, Integer> grouped) {
				// TODO Auto-generated method stub
				grouped.toList().subscribe(new Action1<List<Integer>>() {
					public void call(List<Integer> integers) {
						// TODO Auto-generated method stub
						System.out.println(integers + "(Even: " + grouped.getKey() + ")");
					}
				});
			}
		});
	}
	
	
	@Test
	public void testFilterObservable() {
		
		// This will only let 3 and 4 pass.
		Observable.just(1, 2, 3, 4)
		.filter(new Func1<Integer, Boolean>() {
			public Boolean call(Integer integer) {
				// TODO Auto-generated method stub
				return integer > 2;
			}
		}).subscribe(new Action1<Integer>() {
			public void call(Integer t1) {
				// TODO Auto-generated method stub
				System.out.println(t1);
			}
		});
		
		// Only 1 and 2 will pass.
		Observable
		    .just(1, 2, 3, 4)
		    .take(2)
		    .subscribe(new Action1<Integer>() {
				public void call(Integer t1) {
					// TODO Auto-generated method stub
					System.out.println(t1);
				}
			});
		
		// Only 1 will pass
		Observable
		    .just(1, 2, 3, 4)
		    .first()
		    .subscribe(new Action1<Integer>() {
				public void call(Integer t1) {
					// TODO Auto-generated method stub
					System.out.println(t1);
				}
			});
		
		// Only 4 will pass
		Observable
		    .just(1, 2, 3, 4)
		    .last()
		    .subscribe(new Action1<Integer>() {
				public void call(Integer t1) {
					// TODO Auto-generated method stub
					System.out.println(t1);
				}
			});
		
		// 1, 2, 3, 4 will be emitted
		System.out.println("实例distinct...........");
		Observable
		.just(1, 2, 1, 3, 4, 2)
		.distinct().forEach(new Action1<Integer>() { //去重
			public void call(Integer t1) {
				System.out.println(t1);
			}
		});
	}
	
	/*
	 * Multiple Observables can also be merged to form a combined one. 
	 * Depending on how you want those to be merged, 
	 * there are different operators available. 
	 * Two of the most used ones are merge() and zip()
	 * */
	@Test
	public void testCombineObeservable() {
		
		//use merger() 
		Observable.merge(Observable.just(2, 4), Observable.just(5, 9))
		.subscribe(new Action1<Integer>() {
			public void call(Integer integer) {
				// TODO Auto-generated method stub
				System.out.println("combine: " + integer);
			}
		});
		
		
		//use zip()
		// result: [2, 1][4, 3][6, 5][8, 7][10, 9]
		Observable<Integer> evens = Observable.just(2, 4, 6, 8, 10);
		Observable<Integer> odds = Observable.just(1, 3, 5, 7, 9);
		Observable.zip(evens, odds, new Func2<Integer, Integer, List<Integer>>() {
			public List<Integer> call(Integer t1, Integer t2) {
				// TODO Auto-generated method stub
				return Arrays.asList(t1, t2);
			}
		}).subscribe(new Action1<List<Integer>>() {
			public void call(List<Integer> t1) {
				// TODO Auto-generated method stub
				System.out.print(t1);
			}
		});
	}
	
	/*
	 * Return a default value instead
	 * Flip over to a backup Observable
	 * Retry the Observable (immediately or with backoff)
	 */
	@Test
	public void testErrorHandler() {
		/*Observable.just("Apples", "Bananas")
		.doOnNext(new Action1<String>() {
			public void call(String t1) {
				// TODO Auto-generated method stub
				if (t1.equals("Bananas")) {
					throw new RuntimeException("I dont like: " + t1);
				} 
			}
		}).onErrorReturn(new Func1<Throwable, String>() {
			public String call(Throwable t1) {
				// TODO Auto-generated method stub
				System.err.println(t1.getMessage());
				return "Default";
			}
		}).subscribe(new Action1<String>() {
			public void call(String t1) {
				// TODO Auto-generated method stub
				System.out.println(t1);
			}
		});*/
		
		//Resume
		bucket.async()
	    .get("docId")
	    .timeout(500, TimeUnit.MILLISECONDS)
	    .onErrorResumeNext(new Func1<Throwable, Observable<? extends JsonDocument>>() {
	        public Observable<? extends JsonDocument> call(Throwable throwable) {
	            if (throwable instanceof TimeoutException) {
	                return bucket.async().getFromReplica("docId", ReplicaMode.ALL);
	            }
	            return Observable.error(throwable);
	        }
	    }).subscribe(new Action1<JsonDocument>() {
	    	public void call(JsonDocument t1) {
	    		// TODO Auto-generated method stub
	    		System.out.println(t1);
	    	}
		});
		
		
		//Retry
		Observable.range(1, 10)
		.doOnNext(new Action1<Integer>() {
			public void call(Integer t1) {
				// TODO Auto-generated method stub
				if (new Random().nextInt(10) + 1 == 5) {
		            throw new RuntimeException("Boo!");
		        }
			}
		});
	}
	
	
	
	
}














