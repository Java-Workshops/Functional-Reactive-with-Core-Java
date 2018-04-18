package org.rapidpm.event.frp.jdk09.mixed_style;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;

import java.util.Objects;

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
}
