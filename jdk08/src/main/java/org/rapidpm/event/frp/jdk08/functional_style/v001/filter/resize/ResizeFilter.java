package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.resize;

import com.jhlabs.image.ScaleFilter;
import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import java.awt.image.BufferedImage;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toBufferedImage;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

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

    final BufferedImage image = toBufferedImage().apply(input);
    double              p     = percentage(percentage);

    final ScaleFilter filter = new ScaleFilter((int) (image.getWidth() * p),
                                               (int) (image.getHeight() * p)
    );

    return toByteArray().apply(filter.filter(image, null));
  }


  public void setPercentage(String percentage) {
    this.percentage = percentage;
  }
}
