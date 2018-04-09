package org.rapidpm.event.frp.core.m09_completable_future;

import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class M09_005 {

  public static BiFunction<String, String, String> fktConcat(){
    return (a, b) -> a + b;
  }

  public static void main(String[] args) {

    final ExecutorService pool = newFixedThreadPool(getRuntime().availableProcessors());

    Supplier<String> sourceA = () -> "A";
    Supplier<String> sourceB = () -> "B";
    Supplier<String> sourceC = () -> "C";
    Consumer<String> consumer = System.out::println;

    supplyAsync(sourceA, pool)
        .thenCombineAsync(supplyAsync(sourceB, pool), fktConcat())
        .thenCombineAsync(supplyAsync(sourceC, pool), fktConcat())
        .thenAcceptAsync(consumer)
        .join();

    pool.shutdown();

  }
}
