package org.rapidpm.event.frp.jdk08.oo_style.filter.points;

import com.jhlabs.image.PointillizeFilter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.rapidpm.event.frp.jdk08.oo_style.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PointsFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    try {
      final BufferedImage image = Imaging.getBufferedImage(input);

      PointillizeFilter filter = new PointillizeFilter();
      filter.setScale(4.0f);

      BufferedImage resultBufferedImage = filter.filter(image, null);

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(resultBufferedImage, "jpeg", os);
      byte[] result = os.toByteArray();
      return result;

    } catch (ImageReadException | IOException e) {
      e.printStackTrace();
      return new byte[0];
    }


  }
}
