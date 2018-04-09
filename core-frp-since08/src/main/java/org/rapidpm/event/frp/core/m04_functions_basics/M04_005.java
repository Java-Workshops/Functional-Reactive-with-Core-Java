package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.function.Function;

public class M04_005 {


  public static void main(String[] args) {


    Function<Integer, Function<Integer, Integer>> adder
        = (x) -> {return (y) -> {return x + y;};};

    Function<Integer, Integer> adder10 = adder.apply(10);
    Function<Integer, Integer> adder05 = adder.apply(5);
    Function<Integer, Integer> adder02 = adder.apply(5);

    adder02
        .andThen(adder05)
        .andThen(adder10)
        .apply(2);

    adder02
        .compose(adder05)
        .compose(adder10)
        .apply(2);


    adder
        .apply(2)
        .andThen(adder.apply(10))
        .andThen(adder.apply(5))
        .apply(2);

  }
}
