package org.rapidpm.event.frp.jdk08.oo_style.filter.emboss;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.rapidpm.event.frp.jdk08.oo_style.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EmbossFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    try {
      final BufferedImage image = Imaging.getBufferedImage(input);

      com.jhlabs.image.EmbossFilter filter = new com.jhlabs.image.EmbossFilter();

      final BufferedImage resultBufferedImage = filter.filter(image, null);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(resultBufferedImage, "jpeg", os);
      byte[] result = os.toByteArray();
      return result;

    } catch (ImageReadException | IOException e) {
      e.printStackTrace();
    }

    return new byte[0];
  }
}
