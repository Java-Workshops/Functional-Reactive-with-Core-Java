package org.rapidpm.event.frp.core.m04_functions_basics;

import java.util.Arrays;
import java.util.List;

public class M04_Task01 {


  public static void main(String[] args) {

    List<String> names = Arrays.asList(
        "Hugo",
        "Willy",
        "Simon",
        "Erwin",
        "Sigfried"
    );

    // example 1
    names.stream().filter(name -> name.contains("i")).forEach(System.out::println);
    //example 2
    names.stream().filter(name -> name.contains("g")).forEach(System.out::println);

    //extract the filter logic and test it via jUnit


  }
}
