package org.rapidpm.event.frp.core.m00_functional_interfaces;


/**
 * Change the code in a way that the
 * interface Service will be a FunctionalInterface.
 */
public class M00_001 {


  public interface Service {
    String doWork(String input);

    String toUpperCase(String input);
  }

  public static class ServiceImplA implements Service {

    @Override
    public String doWork(String input) {
      return input + "_A";
    }

    @Override
    public String toUpperCase(String input) {
      return input.toUpperCase();
    }
  }

  public static class ServiceImplB implements Service {

    @Override
    public String doWork(String input) {
      return input + "_B";
    }

    @Override
    public String toUpperCase(String input) {
      return input.toUpperCase();
    }
  }

}
