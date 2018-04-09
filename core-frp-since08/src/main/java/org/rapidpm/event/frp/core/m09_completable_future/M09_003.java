package org.rapidpm.event.frp.core.m09_completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class M09_003 {

  public static void main(String[] args) {

    final ExecutorService pool = newFixedThreadPool(getRuntime().availableProcessors());

    final CompletableFuture<String> stepA = supplyAsync(() -> "A", pool);
    final CompletableFuture<String> stepB = supplyAsync(() -> "B", pool);
    final CompletableFuture<String> stepC = supplyAsync(() -> "C", pool);

    final CompletableFuture<String> stepAB = stepA.thenCombineAsync(stepB, (a, b) -> a + b);
    final CompletableFuture<String> stepABC = stepAB.thenCombineAsync(stepC, (ab, c) -> ab + c);

    stepABC.thenAcceptAsync(System.out::println).join();

    pool.shutdown();

  }
}
