package org.rapidpm.event.frp.jdk08.functional_style.v001.filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

public interface FilterFunctions {

  static Function<BufferedImage, byte[]> toByteArray() {
    return (image) -> {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try {
        ImageIO.write(image, "jpeg", os);
        return os.toByteArray();
      } catch (IOException e) {
        e.printStackTrace();
        return new byte[0];
      }
    };
  }


}
