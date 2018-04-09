package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_003 {

  public static void main(String[] args) {

    Function<Integer, Integer> funcA = (x) -> x + 2;
    Function<Integer, Integer> funcB = (x) -> x + 10;
    Function<Integer, Integer> funcC = (x) -> x + 5;

    funcA
        .compose(funcB)
        .compose(funcC)
        .apply(2);

    funcA
        .andThen(funcB)
        .andThen(funcC)
        .apply(2);

  }
}
