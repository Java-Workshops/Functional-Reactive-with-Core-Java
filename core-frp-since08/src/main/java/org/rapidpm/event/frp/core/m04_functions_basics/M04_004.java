package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_004 {

  public static Function<Integer, Integer> funcA(){
    return (x) -> x + 2;
  }

  public static Function<Integer, Integer> funcB(){
    return (x) -> x + 10;
  }

  public static Function<Integer, Integer> funcC(){
    return (x) -> x + 5;
  }


  public static void main(String[] args) {

    funcA()
        .compose(funcB())
        .compose(funcC())
        .apply(2);

    funcA()
        .andThen(funcB())
        .andThen(funcC())
        .apply(2);
  }
}
