package org.rapidpm.event.frp.jdk08.oo_style.filter.resize;

import com.jhlabs.image.ScaleFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResizeFilter implements Filter {


  private String percentage;

  private double percentage(String percentage) {
    return (percentage.equals("100"))
           ? 1
           : (percentage.equals("50"))
             ? 0.5
             : (percentage.equals("25"))
               ? 0.25
               : 1;
  }

  public byte[] workOn(byte[] input) {

    try {
//      final BufferedImage image = Imaging.getBufferedImage(input);
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(input));
      double              p     = percentage(percentage);

      final ScaleFilter filter = new ScaleFilter((int) (image.getWidth() * p),
                                                 (int) (image.getHeight() * p)
      );

      BufferedImage resultBufferedImage = filter.filter(image, null);

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(resultBufferedImage, "jpeg", os);
      byte[] result = os.toByteArray();
      return result;

    } catch (IOException e) {
      e.printStackTrace();
    }


    return new byte[0];
  }


  public void setPercentage(String percentage) {
    this.percentage = percentage;
  }
}
