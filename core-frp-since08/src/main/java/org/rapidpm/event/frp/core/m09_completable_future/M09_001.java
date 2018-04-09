package org.rapidpm.event.frp.core.m09_completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class M09_001 {

  public static void main(String[] args) {

    final ExecutorService pool = Executors
        .newFixedThreadPool(Runtime
                                .getRuntime()
                                .availableProcessors());

    final Supplier<String> task = () -> "Hello World";


    final CompletableFuture<String> async = CompletableFuture.supplyAsync(task, pool);

    final CompletableFuture<Void> result = async.thenAcceptAsync(System.out::println);

    result.join();

    pool.shutdown();

  }
}
