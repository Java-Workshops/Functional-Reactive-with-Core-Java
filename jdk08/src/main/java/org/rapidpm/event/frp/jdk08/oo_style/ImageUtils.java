package org.rapidpm.event.frp.jdk08.oo_style;

import com.vaadin.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.util.concurrent.ThreadLocalRandom.current;

public class ImageUtils {


  static String fileName(int id) {
    return format("%04d", id) + "_1024px.jpg";
  }

  static String fileName(String id) {
    return id + "_1024px.jpg";
  }

  static String randomImageID(int boundary) {
    return format("%04d", current().nextInt(boundary) + 1);
  }

  static String nextImageName(int boundary) {
    return fileName(randomImageID(boundary));
  }

  static byte[] readImageWithIdAsBytes(String nextImageName) {
    byte[] allBytes;
    try {
      allBytes = readAllBytes(
          new File("./",
                   "_data/_images/_jpeg/_1024px/" + nextImageName
          ).toPath()
      );
    } catch (IOException e) {
      //logger().warning(e);
      try {
        allBytes = ImageUtils.failedImage(nextImageName);
      } catch (IOException e1) {
        //logger().warning(e1);
        allBytes = new byte[0];
      }
    }
    return allBytes;
  }

  static InputStream readImageWithIdAsStream(String nextImageName) {
    byte[]                     allBytes    = readImageWithIdAsBytes(nextImageName);
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(allBytes);
    return inputStream;
  }


  static byte[] failedImage(String imageID) throws IOException {
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
    // Write the image to a buffer
    ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", imagebuffer);

    // Return a stream from the buffer
    return imagebuffer.toByteArray();
  }

  static InputStream failedImageAsInputStream(String imageID) throws IOException {
    byte[]                     failedImage = failedImage(imageID);
    final ByteArrayInputStream baos        = new ByteArrayInputStream(failedImage);
    return baos;
  }

  static StreamResource imageAsStreamRessouce(String nextImageName) {
    byte[] bytes = ImageUtils.readImageWithIdAsBytes(nextImageName);
    final StreamResource streamResource = new StreamResource(
        new StreamResource.StreamSource() {
          @Override
          public InputStream getStream() {
            return new ByteArrayInputStream(bytes);
          }
        },
        nextImageName
    );
    return streamResource;
  }

}
