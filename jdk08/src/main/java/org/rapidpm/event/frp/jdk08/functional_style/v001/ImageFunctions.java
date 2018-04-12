package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;

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


  static Function<String, StreamResource> imageAsStreamRessouce() {
    return (nextImageName) -> readImageWithIdAsBytes()
        .andThen(bytes -> new StreamResource(
            (StreamResource.StreamSource) () -> new ByteArrayInputStream(bytes),
            nextImageName))
        .apply(nextImageName);
  }

  static Function<String, byte[]> readImageWithIdAsBytes() {
    return (nextImageName) -> {
      try {
        return readAllBytes(
            new File("./",
                     "_data/_images/_jpeg/_1024px/" + nextImageName
            ).toPath()
        );
      } catch (IOException e) {
        return failedImage().apply(nextImageName);
      }
    };
  }

  static Function<Integer, String> nextImageName() {
    return (boundary) -> randomImageID()
        .andThen(filename())
        .apply(boundary);
  }

  static Function<Integer, String> randomImageID() {
    return (boundary) -> formatID().apply(current().nextInt(boundary) + 1);
  }

  static Function<Integer, String> formatID(){
    return (id)-> format("%04d", id);
  }

  static Function<String, String> filename() {
    return (id) -> id + "_1024px.jpg";
  }


  static Function<String, byte[]> failedImage() {
    return (imageID) -> {
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
      drawable.drawString(imageID, 75, 216);
      drawable.setColor(new Color(0, 165, 235));
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
