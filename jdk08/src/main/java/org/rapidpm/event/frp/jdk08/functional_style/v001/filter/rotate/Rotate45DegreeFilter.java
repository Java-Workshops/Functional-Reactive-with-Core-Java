package org.rapidpm.event.frp.jdk08.functional_style.v001.filter.rotate;

import com.jhlabs.image.RotateFilter;
import org.rapidpm.event.frp.jdk08.functional_style.v001.filter.Filter;

import java.awt.image.BufferedImage;

import static com.jhlabs.image.TransformFilter.ZERO;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toBufferedImage;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.toByteArray;

public class Rotate45DegreeFilter implements Filter {
  @Override
  public byte[] workOn(byte[] input) {

    final BufferedImage image  = toBufferedImage().apply(input);
    final RotateFilter  filter = new RotateFilter(45f, true);
    filter.setEdgeAction(ZERO);

    return toByteArray().apply(filter.filter(image, null));
  }
}
