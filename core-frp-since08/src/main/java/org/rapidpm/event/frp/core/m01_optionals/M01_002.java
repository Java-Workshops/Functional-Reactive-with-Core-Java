package org.rapidpm.event.frp.core.m01_optionals;


/**
 * Change the interface Service,
 * to handle the Value null in a proper way with Optional
 * <p>
 * Test the method doWork(). The test class is available under
 * junit.org.rapidpm.event.frp.core.m01_optionals.M01_002Test
 */
public class M01_002 {

  public Integer doWork(String input, Service<Integer> converter) {
    return converter.convert(input);
  }

  public interface Service<T> {
    T convert(String input);
  }
}
