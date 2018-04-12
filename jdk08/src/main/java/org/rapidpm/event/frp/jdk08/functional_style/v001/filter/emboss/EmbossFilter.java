package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.emboss;

import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EmbossFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    try {
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(input));

      com.jhlabs.image.EmbossFilter filter = new com.jhlabs.image.EmbossFilter();

      final BufferedImage   resultBufferedImage = filter.filter(image, null);
      ByteArrayOutputStream os                  = new ByteArrayOutputStream();
      ImageIO.write(resultBufferedImage, "jpeg", os);
      byte[] result = os.toByteArray();
      return result;

    } catch (IOException e) {
      e.printStackTrace();
    }

    return new byte[0];
  }
}
