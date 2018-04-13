package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;

import java.util.Objects;
import java.util.function.BiFunction;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.toStreamResource;

public class ImagePanel extends Composite {

  private Image image = new Image();
  private Panel panel = new Panel(image);

  public ImagePanel() {
    setCompositionRoot(panel);
  }

  public void setStreamRessoure(StreamResource streamRessoure) {
    Objects.requireNonNull(streamRessoure);
    image.setSource(streamRessoure);
    panel.setCaption(streamRessoure.getFilename());
  }


  public static BiFunction<byte[], String, ImagePanel> imagePanel() {
    return (image, name) -> {
      StreamResource streamResource = toStreamResource().apply(image, name);
      streamResource.setCacheTime(0);
      final ImagePanel imagePanel = new ImagePanel();
      imagePanel.setCaption(name);
      imagePanel.setStreamRessoure(streamResource);
      return imagePanel;
    };
  }

}
