package junit.org.rapidpm.event.frp.jdk10.m001;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.event.frp.jdk10.m001.HelloWorldService;

public class HelloWorldServiceTest {


  @Test
  void test001() {
    final HelloWorldService service = new HelloWorldService();
    Assertions.assertEquals(Integer.valueOf(1),
                            service.convert("1")
    );
  }
}
