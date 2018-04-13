package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.emboss;

import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import java.awt.image.BufferedImage;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toBufferedImage;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class EmbossFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {
    BufferedImage                 image  = toBufferedImage().apply(input);
    com.jhlabs.image.EmbossFilter filter = new com.jhlabs.image.EmbossFilter();

    return toByteArray().apply(filter.filter(image, null));
  }
}
