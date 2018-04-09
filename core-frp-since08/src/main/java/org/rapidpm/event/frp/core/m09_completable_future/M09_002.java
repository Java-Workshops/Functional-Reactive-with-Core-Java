package org.rapidpm.event.frp.core.m09_completable_future;

import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class M09_002 {

  public static void main(String[] args) {

    final ExecutorService pool = newFixedThreadPool(getRuntime()
                                .availableProcessors());

    supplyAsync(() -> "Hello World", pool)
        .thenAcceptAsync(System.out::println)
        .join();

    pool.shutdown();
  }
}
