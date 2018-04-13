package org.rapidpm.event.frp.jdk08.functional_style.v001.filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.failedBufferedImage;

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

  static Function<byte[], BufferedImage> toBufferedImage() {
    return (input) -> {
      try {
        return ImageIO.read(new ByteArrayInputStream(input));
      } catch (IOException e) {
        e.printStackTrace();
        return failedBufferedImage().apply("read failed !");
      }
    };
  }

}
