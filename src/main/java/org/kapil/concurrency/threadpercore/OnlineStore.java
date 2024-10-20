package org.kapil.concurrency.threadpercore;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineStore {

  private HttpClient httpClient;
  /** Starts an HTTP Server listening on port 8080.
   Delegates the handling of the requests to the handleHttpRequest method
   **/
  private void startHttpServer() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    HttpContext context = server.createContext("/");
    context.setHandler(this::handleHttpRequest);

    ExecutorService executorService = Executors.newFixedThreadPool(8);
    this.httpClient = HttpClient.newBuilder().executor(executorService).build();
    server.setExecutor(executorService);

    server.start();
  }

  /** Handles an incoming HTTP request from a user
   */
  private void handleHttpRequest(HttpExchange httpExchange) {
    int numberOfProducts = 1; // do something
    URI requestURI = URI.create(String.format("best-online-store/products?number-of-products=%d", numberOfProducts));

    CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(
        HttpRequest.newBuilder()
            .GET()
            .uri(requestURI)
            .build(),
        BodyHandlers.ofString());

    future.thenAccept(System.out::println);
  }
}
