package junit.org.rapidpm.event.frp.core.m01_optionals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.event.frp.core.m01_optionals.M01_001;

public class M01_001Test {


  @Test
  void test001() {
    Integer result = new M01_001().doWork("12", new M01_001.Service() { });

    Assertions.assertEquals(Integer.valueOf(12), result);
  }
}
