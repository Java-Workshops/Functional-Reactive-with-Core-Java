package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_001 {

  public static void main(String[] args) {

    Function<Integer, Integer> funcAdd = (x) -> x + 2;


    Function<Integer, Integer> funcA = (x) -> x + 2;
    Function<Integer, Integer> funcB = (x) -> x + 10;
    Function<Integer, Integer> funcC = (x) -> x + 5;

    Integer a = funcA.apply(2);
    Integer b = funcB.apply(a);
    Integer c = funcB.apply(b);

    funcA.apply(funcB.apply(funcC.apply(2)));
    funcC.apply(funcB.apply(funcA.apply(2)));


  }

}
