package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.rotate;

import com.jhlabs.image.RotateFilter;
import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.jhlabs.image.TransformFilter.ZERO;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class Rotate45DegreeFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    try {
      final BufferedImage image  = ImageIO.read(new ByteArrayInputStream(input));
      final RotateFilter  filter = new RotateFilter(45f, true);
      filter.setEdgeAction(ZERO);

      return toByteArray().apply(filter.filter(image, null));

    } catch (IOException e) {
      e.printStackTrace();
      return new byte[0];
    }
  }
}
