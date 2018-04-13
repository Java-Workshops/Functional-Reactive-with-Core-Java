package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.emboss;

import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class EmbossFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    try {
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(input));
      com.jhlabs.image.EmbossFilter filter = new com.jhlabs.image.EmbossFilter();
      return toByteArray().apply(filter.filter(image, null));
    } catch (IOException e) {
      e.printStackTrace();
      return new byte[0];
    }
  }
}
