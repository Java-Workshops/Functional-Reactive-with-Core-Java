package junit.org.rapidpm.event.frp.core.m00_functional_interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.event.frp.core.m00_functional_interfaces.M00_001;

public class M00_001Test {


  @Test
  void test001() {
    Assertions.assertEquals("Hello_A", new M00_001.ServiceImplA().doWork("Hello"));
  }

  @Test
  void test002() {
    Assertions.assertEquals("Hello_B", new M00_001.ServiceImplB().doWork("Hello"));
  }

  @Test
  void test003() {
    Assertions.assertEquals("HELLO", new M00_001.ServiceImplA().toUpperCase("Hello"));
  }

  @Test
  void test004() {
    Assertions.assertEquals("HELLO", new M00_001.ServiceImplB().toUpperCase("Hello"));
  }


}
