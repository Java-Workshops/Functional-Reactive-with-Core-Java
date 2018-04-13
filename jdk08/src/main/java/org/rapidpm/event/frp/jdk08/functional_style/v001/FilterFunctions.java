package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.jhlabs.image.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.jhlabs.image.TransformFilter.ZERO;
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


  static Function<byte[], byte[]> emboss() {
    return (input) -> {
      BiFunction<BufferedImage, BufferedImage, BufferedImage> filter = new EmbossFilter()::filter;
      return filter
          .andThen(toByteArray())
          .apply(toBufferedImage().apply(input),
                 null
          );
    };
  }

  static Function<byte[], byte[]> grayscale() {
    return (input) -> {
      final BiFunction<BufferedImage, BufferedImage, BufferedImage> filter = new GrayscaleFilter()::filter;
      return filter
          .andThen(toByteArray())
          .apply(toBufferedImage().apply(input),
                 null
          );
    };
  }

  static Function<byte[], byte[]> points() {
    return (input) -> {
      PointillizeFilter p = new PointillizeFilter();
      p.setScale(4.0f);

      BiFunction<BufferedImage, BufferedImage, BufferedImage> filter = p::filter;

      return filter
          .andThen(toByteArray())
          .apply(toBufferedImage().apply(input),
                 null
          );
    };
  }

  static BiFunction<Float, byte[], byte[]> rotate() {
    return (angle, input) -> {

      final RotateFilter f = new RotateFilter(angle, true);
      f.setEdgeAction(ZERO);

      BiFunction<BufferedImage, BufferedImage, BufferedImage> filter = f::filter;

      return filter
          .andThen(toByteArray())
          .apply(toBufferedImage().apply(input),
                 null
          );
    };
  }

  static Function<byte[], byte[]> rotate45Degree() {
    return (input) -> rotate().apply(45f, input);
  }


  static BiFunction<Double, byte[], byte[]> resize() {
    return (scale, input) -> {

      final BufferedImage image = toBufferedImage().apply(input);
      final ScaleFilter f = new ScaleFilter((int) (image.getWidth() * scale),
                                            (int) (image.getHeight() * scale)
      );
      BiFunction<BufferedImage, BufferedImage, BufferedImage> filter = f::filter;
      return filter
          .andThen(toByteArray())
          .apply(image,
                 null
          );
    };
  }

}
