package com.fqserver;

import com.fqserver.server.HttpServer;

public class StartServer {

	public static void main(String[] args) throws Exception {

		HttpServer server = new HttpServer();

		InitServer.init();
		server.start();

	}

}
