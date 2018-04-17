package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.memoizer.Memoizer;
import org.rapidpm.frp.model.Pair;
import org.rapidpm.frp.model.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.util.concurrent.ThreadLocalRandom.current;

public interface ImageFunctions {

  static Function<Pair<String, byte[]>, Result<StreamResource>> toStreamResource() {
    return (CheckedFunction<Pair<String, byte[]>, StreamResource>) input -> new StreamResource(
        (StreamResource.StreamSource) () -> new ByteArrayInputStream(input.getT2()), input.getT1());
  }

  static Function<Pair<String, byte[]>, Result<StreamResource>> toStreamResourceNoCache() {
    return (input) -> toStreamResource()
        .andThen(r -> r.ifPresent(s -> s.setCacheTime(0)))
        .apply(input);
  }

  static Function<Integer, String> nextImageNameMemoized() {
    return Memoizer.memoize(nextImageName());
  }

  static Function<Integer, String> nextImageName() {
    return (boundary) -> randomImageID()
        .andThen(filename())
        .apply(boundary);
  }

  static Function<Integer, String> randomImageID() {
    return (boundary) -> formatID().apply(current().nextInt(boundary) + 1);
  }

  static Function<Integer, String> formatID() {
    return (id) -> format("%04d", id);
  }

  static Function<String, String> filename() {
    return (id) -> id + "_1024px.jpg";
  }


  static Function<String, Result<Pair<String, byte[]>>> readImageWithIdAsBytesMemoized() {
    return Memoizer.memoize(readImageWithIdAsBytes());
  }

  static Function<String, Result<Pair<String, byte[]>>> readImageWithIdAsBytes() {
    return (CheckedFunction<String, Pair<String, byte[]>>) nextImageName -> {
      byte[] bytes = readAllBytes(
          new File("./",
                   "_data/_images/_jpeg/_1024px/" + nextImageName
          ).toPath()
      );
      return Pair.next(nextImageName, bytes);
    };
  }

  static Function<String, Pair<String, byte[]>> loadOrigOrFailedImg() {
    return (imgName) -> readImageWithIdAsBytes()
        .andThen(r -> r.getOrElse(() -> Pair.next(imgName,
                                                  failedImage().apply("failed " + imgName)
        )))
        .apply(imgName);
  }


  static Function<String, BufferedImage> failedBufferedImage() {
    return (txt) -> {
      BufferedImage image = new BufferedImage(512, 512,
                                              BufferedImage.TYPE_INT_RGB
      );
      Graphics2D drawable = image.createGraphics();
      drawable.setStroke(new BasicStroke(5));
      drawable.setColor(Color.WHITE);
      drawable.fillRect(0, 0, 512, 512);
      drawable.setColor(Color.BLACK);
      drawable.drawOval(50, 50, 300, 300);

      drawable.setFont(new Font("Montserrat", Font.PLAIN, 20));
      drawable.drawString(txt, 75, 216);
      drawable.setColor(new Color(0, 165, 235));
      return image;
    };
  }

  static Function<String, byte[]> failedImage() {
    return (imageID) -> {
      final BufferedImage image = failedBufferedImage().apply(imageID);

      try {
        // Write the image to a buffer
        ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", imagebuffer);
        // Return a stream from the buffer
        return imagebuffer.toByteArray();
      } catch (IOException e) {
        return new byte[0];
      }
    };
  }

}
