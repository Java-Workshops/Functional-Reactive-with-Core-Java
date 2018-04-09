package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_006 {


  public static Function<Integer, Function<Integer, Integer>> adder(){
    return (x) -> (y) -> x + y ;
  }

  public static void main(String[] args) {
    adder()
        .apply(2)
        .andThen(adder().apply(10))
        .andThen(adder().apply(5))
        .apply(2);
  }
}
