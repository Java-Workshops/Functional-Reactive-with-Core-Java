package org.rapidpm.event.frp.jdk09.functional_reactive_style;

import com.jhlabs.image.*;
import org.rapidpm.frp.model.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.jhlabs.image.TransformFilter.ZERO;
import static java.lang.Double.parseDouble;
import static org.rapidpm.event.frp.jdk09.functional_reactive_style.ImageFunctions.failedBufferedImage;

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


  static Function<Double, Function<byte[], byte[]>> resizeCurried() {
    return a -> b -> resize().apply(a, b);
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


  //usage of class FilterCheckBox.Info is to hard bound to the UI
  static Function<FilterCheckBox.Info, Function<byte[], byte[]>> transform() {
    return (input) -> {
      //How to deal with Exceptions / not the perfect place for the parsing logic
      final Function<String, Double> factor        = (s) -> parseDouble(s.replace("%", "")) * 0.01;
      final Function<byte[], byte[]> resizeCurried = factor.andThen(resizeCurried()).apply(input.getSize());

      return Stream
          .of(Pair.next(input.isFilterGrayscale(), grayscale()),
              Pair.next(input.isFilterPointerize(), points()),
              Pair.next(input.isFilterRotate(), rotate45Degree())
          )
          .filter(Pair::getT1)
          .map(Pair::getT2)
          .reduce(Function::andThen) //[0..1] elements
          .map(resizeCurried::andThen)
          .orElse(resizeCurried);
    };
  }

}
