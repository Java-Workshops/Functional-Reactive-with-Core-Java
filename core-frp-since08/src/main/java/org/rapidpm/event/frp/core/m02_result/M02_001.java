package org.rapidpm.event.frp.core.m02_result;

public class M02_001 {


  public interface ConverterToInt {
    Integer toIntegerValue(String input);
  }


  public interface ConverterToString {
    String toStringValue(Integer input);
  }


  public interface Calculator {
    Integer calculate(Integer input);
  }

  public interface Workflow {
    default String doWork(String input,
                          ConverterToInt toInt,
                          ConverterToString toStr,
                          Calculator calculator) {
      return toStr.toStringValue(
          calculator.calculate(
              toInt.toIntegerValue(input)));
    }
  }
}
