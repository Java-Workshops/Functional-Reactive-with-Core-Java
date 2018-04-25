package junit.org.rapidpm.event.frp.core.m01_optionals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.event.frp.core.m01_optionals.M01_003;

import static java.lang.Integer.valueOf;

public class M01_003Test {

  @Test
  void test001() {
    Integer result = new M01_003().doWork("12", new M01_003.Service() { });

    Assertions.assertEquals(valueOf(12), result);
  }


  @Test
  void test002() {
    Integer result = new M01_003().doWork("-1", new M01_003.Service() { });

    Assertions.assertEquals(valueOf(-1), result);
  }


  @Test
  void test003() {
    Integer result = new M01_003().doWork("XX", new M01_003.Service() { });

    Assertions.assertEquals(valueOf(-1), result);
  }

  @Test
  void test004() {
    Integer result = new M01_003().doWork(null, new M01_003.Service() { });

    Assertions.assertEquals(valueOf(-1), result);
  }


}
