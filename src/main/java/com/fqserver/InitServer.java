package com.fqserver;

import com.fqserver.action.IndexDo;
import com.fqserver.core.utils.Pools;
import com.fqserver.server.UrlMap;

public class InitServer {

	public  static void init() {
		
		//加载url路由
		
		IndexDo.initUrl();

		//加载对象池
		for(String key : UrlMap.urlMap.keySet()){
			
		Pools.set(UrlMap.urlMap.get(key), null);}
	}

}
