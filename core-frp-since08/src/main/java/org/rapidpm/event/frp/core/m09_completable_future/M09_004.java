package org.rapidpm.event.frp.core.m09_completable_future;

import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class M09_004 {

  public static void main(String[] args) {

    final ExecutorService pool = newFixedThreadPool(getRuntime().availableProcessors());

    supplyAsync(() -> "A", pool)
        .thenCombineAsync(supplyAsync(() -> "B", pool), (a, b) -> a + b)
        .thenCombineAsync(supplyAsync(() -> "C", pool), (ab, c) -> ab + c)
        .thenAcceptAsync(System.out::println)
        .join();

    pool.shutdown();

  }
}
