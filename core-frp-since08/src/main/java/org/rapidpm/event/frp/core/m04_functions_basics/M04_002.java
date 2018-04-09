package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_002 {

  public static void main(String[] args) {

    Function<Integer, Integer> funcAdd = (x) -> x + 2;

    Integer composeAdd = funcAdd
        .compose((Function<Integer, Integer>) x -> x + 10)
        .compose((Function<Integer, Integer>) x -> x + 5)
        .apply(2);

    // (2) + 5 + 10 + 2 = 19
    System.out.println("composeAdd = " + composeAdd);

    Function<Integer, Integer> funcMinus = (x) -> x - 2;

    Integer composeMinus = funcMinus
        .compose((Function<Integer, Integer>) x -> x - 10)
        .compose((Function<Integer, Integer>) x -> x - 5)
        .apply(2);

    // (2) - 5 - 10 - 2 = 19
    System.out.println("composeMinus = " + composeMinus);


    Function<Integer, Integer> func = x -> x + 2;
    func
        .andThen(x -> x + 10)
        .andThen(x -> x + 5)
        .apply(2);

  }

}
