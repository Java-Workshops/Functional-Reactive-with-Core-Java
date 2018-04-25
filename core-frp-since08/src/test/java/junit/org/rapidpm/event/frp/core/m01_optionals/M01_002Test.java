package junit.org.rapidpm.event.frp.core.m01_optionals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rapidpm.event.frp.core.m01_optionals.M01_002;

public class M01_002Test {


  @Test
  @Disabled
  void test001() {
    M01_002.Service converter = null;

    Assertions.assertEquals(Integer.valueOf(133),
                            new M01_002().doWork("133", converter)
    );

  }
}
