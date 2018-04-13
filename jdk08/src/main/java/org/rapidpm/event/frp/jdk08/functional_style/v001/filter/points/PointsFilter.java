package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.points;

import com.jhlabs.image.PointillizeFilter;
import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import java.awt.image.BufferedImage;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toBufferedImage;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class PointsFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    final BufferedImage image = toBufferedImage().apply(input);

    final PointillizeFilter filter = new PointillizeFilter();
    filter.setScale(4.0f);
    return toByteArray().apply(filter.filter(image, null));

  }
}
