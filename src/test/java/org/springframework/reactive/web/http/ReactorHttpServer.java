/*
 * Copyright (c) 2011-2015 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.reactive.web.http;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.reactive.web.http.reactor.RequestHandlerAdapter;
import org.springframework.util.Assert;
import reactor.bus.selector.Selectors;
import reactor.io.buffer.Buffer;
import reactor.io.net.NetStreams;


/**
 * @author Stephane Maldini
 */
public class ReactorHttpServer extends HttpServerSupport implements InitializingBean, HttpServer {

	private RequestHandlerAdapter reactorHandler;

	private reactor.io.net.http.HttpServer<Buffer, Buffer> reactorServer;

	private boolean running;


	@Override
	public boolean isRunning() {
		return this.running;
	}


	@Override
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(getHttpHandler());
		this.reactorHandler = new RequestHandlerAdapter(getHttpHandler());

		this.reactorServer = (getPort() != -1 ?
		 NetStreams.httpServer(getPort()) :
		  NetStreams.httpServer());
	}


	@Override
	public void start() {
		if (!this.running) {
			this.running = true;
			this.reactorServer.route(Selectors.matchAll(), this.reactorHandler).start();
		}
	}

	@Override
	public void stop() {
		if (this.running) {
			this.running = false;
			this.reactorServer.shutdown();
		}
	}

}
