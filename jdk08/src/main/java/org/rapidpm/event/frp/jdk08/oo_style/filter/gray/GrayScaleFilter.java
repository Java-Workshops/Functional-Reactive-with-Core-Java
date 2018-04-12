package org.rapidpm.event.frp.jdk08.oo_style.filter.gray;

import com.jhlabs.image.GrayscaleFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GrayScaleFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {
    try {
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(input));

      final GrayscaleFilter grayFilter    = new GrayscaleFilter();
      BufferedImage         bufferedImage = grayFilter.filter(image, null);
      ByteArrayOutputStream os            = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "jpeg", os);
      byte[] result = os.toByteArray();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
      return new byte[0];
    }
  }
}
