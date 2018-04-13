package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;

import java.util.Objects;
import java.util.function.BiFunction;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.toStreamResourceNoCache;

public class ImagePanel extends Composite {

  private Image image = new Image();
  private Panel panel = new Panel(image);

  public ImagePanel() {
    setCompositionRoot(panel);
  }

  public static BiFunction<byte[], String, ImagePanel> imagePanel() {
    return (image, name) -> {
      final ImagePanel imagePanel = new ImagePanel();
      imagePanel.setCaption(name);
      imagePanel.setStreamResoure(toStreamResourceNoCache().apply(image, name));
      return imagePanel;
    };
  }

  public void setStreamResoure(StreamResource streamResoure) {
    Objects.requireNonNull(streamResoure);
    image.setSource(streamResoure);
    panel.setCaption(streamResoure.getFilename());
  }

}
