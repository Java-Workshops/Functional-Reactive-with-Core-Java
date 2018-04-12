package org.rapidpm.event.frp.jdk08.functional_style.v001;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

public interface ImageFunctions {


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
