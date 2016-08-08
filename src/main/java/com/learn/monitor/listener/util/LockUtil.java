package com.learn.monitor.listener.util;

import java.util.concurrent.ConcurrentMap;

public class LockUtil {
  private final ConcurrentMap<String,String> lockMap;
  
  public LockUtil(final ConcurrentMap<String, String> lockMap){
	  this.lockMap = lockMap;
  }
  
  public String getLock(String key) {
	  final String lock = lockMap.putIfAbsent(key, key);
	  return (null == lock ? key : lock);
  }
  
}
