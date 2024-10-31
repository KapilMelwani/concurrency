package org.kapil.concurrency.completablefuture;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

  public static Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
      Thread.sleep(1000);
      completableFuture.complete("Hello");
      return null;
    });
    return completableFuture;
  }


  public static void main(String[] args) throws InterruptedException, ExecutionException {
    // Test 1
    Future<String> completableFuture = calculateAsync();
    Future<String> calculateAsyncEasy = CompletableFuture.completedFuture("Hello-2");
    System.out.println(completableFuture.get());
    System.out.println(calculateAsyncEasy.get());
    assert Objects.equals(completableFuture.get(), calculateAsyncEasy.get());

    // Test 2
    processResultsOfAsyncComputations();
  }

  private static void processResultsOfAsyncComputations()
      throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
    // process the result of a computation is to feed it to a function (thenApply)
    CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World");
    assert Objects.equals(future.get(), "Hello World");
  }

  private static void processResultsOfAsyncComputations2()
      throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
    // If we don’t need to return a value down the Future chain,
    // we can use an instance of the Consumer functional interface. Its single method takes a parameter and returns void.
    CompletableFuture<Void> future = completableFuture.thenAccept(
        s -> System.out.println("Computation returned: " + s));
    future.get();
  }

  private static void processResultsOfAsyncComputations3()
      throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
    // If we neither need the value of the computation nor want to return some value at the end of
    // the chain, then we can pass a Runnable lambda to the thenRun method
    CompletableFuture<Void> future = completableFuture.thenRun(
        () -> System.out.println("Computation finished"));
    future.get();
  }

  private static void combiningFutures() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
        .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));
    assert Objects.equals(completableFuture.get(), "Hello World");
    //The thenCompose method, together with thenApply, implements the basic building blocks of
    // the monadic pattern. They closely relate to the map and flatMap methods of Stream and Optional classes, also available in Java 8.
  }

  private static void combiningFutures2() throws ExecutionException, InterruptedException {
    //If we want to execute two independent Futures and do something with their results, we can use the
    // thenCombine method that accepts a Future and a Function with two arguments to process both results:
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
        .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);
    assert Objects.equals(completableFuture.get(), "Hello World");
  }

  private static void combiningFutures3() throws ExecutionException, InterruptedException {
    //  simpler case is when we want to do something with two Futures‘ results but don’t need
    //  to pass any resulting value down a Future chain. The thenAcceptBoth method is there to help:
    CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
        .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
            (s1, s2) -> System.out.println(s1 + s2));
    assert Objects.equals(future.get(), "Hello World");
  }

}
