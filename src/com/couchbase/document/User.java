/**  
 * @Project: couchbase
 * @Title: User.java
 * @Package com.couchbase.document
 * @Description: TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4 ÏÂÎç2:03:40
 * @Copyright: 2015 
 * @version V1.0  
 */

package com.couchbase.document;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description TODO
 * @author qsdepth@foxmail.com
 * @date 2015-1-4
 */

public class User implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	
	private static final long serialVersionUID = 7552568808772227261L;
	private String username;
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
}









