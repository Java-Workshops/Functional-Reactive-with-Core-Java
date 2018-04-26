package junit.org.rapidpm.event.frp.core.m02_result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rapidpm.event.frp.core.m02_result.M02_001;

public class M02_001Test {

  // definition of test cases
  final M02_001.ConverterToInt    toInt = Integer::parseInt;
  final M02_001.ConverterToString toStr = String::valueOf;

  @Test
  void test001() {
    final String result = new M02_001.Workflow() { }.doWork("2",
                                                            new M02_001.ConverterToInt() {
                                                              @Override
                                                              public Integer toIntegerValue(String input) {
                                                                return Integer.parseInt(input);
                                                              }
                                                            },
                                                            new M02_001.ConverterToString() {
                                                              @Override
                                                              public String toStringValue(Integer input) {
                                                                return String.valueOf(input);
                                                              }
                                                            },
                                                            new M02_001.Calculator() {
                                                              @Override
                                                              public Integer calculate(Integer input) {
                                                                return input * input;
                                                              }
                                                            }
    );
    Assertions.assertEquals("4", result);
  }

  @Test
  void test002() {
    final String result = new M02_001.Workflow() { }.doWork("2",
                                                            Integer::parseInt,
                                                            String::valueOf,
                                                            input -> input * input
    );
    Assertions.assertEquals("4", result);
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = {"1", "2", "3", "0", "4"})
  void test010(String input) {
    final M02_001.Calculator calc = (i) -> i / i;

    final String result = new M02_001.Workflow() { }.doWork(input,
                                                            toInt,
                                                            toStr,
                                                            calc
    );
    Assertions.assertEquals("1", result);
  }


}
