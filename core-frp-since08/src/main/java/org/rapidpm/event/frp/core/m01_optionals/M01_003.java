package org.rapidpm.event.frp.core.m01_optionals;


import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.util.Optional.of;

/**
 * Change the interface Service, to handle the Value null in a proper way with Optional
 * <p>
 * Change the interface Service into an FunctionalInterface, so the
 * the method doWork can be invoked with lambdas
 * <p>
 * exception´s must be mapped into the value -1;
 */
public class M01_003 {

  public Integer doWork(String input, Service converter) {
    return converter.convert(input).get();
  }

  public interface Service {
    default Optional<Integer> convert(String input) {
      return of(parseInt(input));
    }

  }


}
