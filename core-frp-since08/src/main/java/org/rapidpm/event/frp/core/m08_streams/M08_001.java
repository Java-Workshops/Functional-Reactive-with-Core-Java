package org.rapidpm.event.frp.core.m08_streams;

import java.util.stream.Stream;

public class M08_001 {

  public static void main(String[] args) {

    Stream
        .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .forEach(System.out::println);
//        .generate(new Supplier<Integer>() {
//          @Override
//          public Integer get() {
//            return null;
//          }
//        })
//        .iterate(0, (i) -> i <= 10, i -> i + 2)

    Stream
        .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .filter(i -> (i * i) > 10)
        .filter(i -> (i * i) < 40)
        .forEach(System.out::println);

  }
}
