package com.fqserver.action;

import com.fqserver.server.RequestHandlerV2;
import com.fqserver.server.UrlMap;

public class IndexDo {
	
	
	public static void initUrl(){
		System.out.println("----------加载url路由---------------");
		
		UrlMap.urlMap.put("/", index.class);
	}
	
	public static class index extends RequestHandlerV2{
		
	
		@Override
		public void get() {
			write("------server index get------");
		}
		
		@Override
		public void post() {
			write("------server index post------");
		}
		
	}
	
	

	
	
	

}

