package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.gray;

import com.jhlabs.image.GrayscaleFilter;
import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import java.awt.image.BufferedImage;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toBufferedImage;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class GrayScaleFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {
    BufferedImage         image  = toBufferedImage().apply(input);
    final GrayscaleFilter filter = new GrayscaleFilter();
    return toByteArray().apply(filter.filter(image, null));
  }
}
