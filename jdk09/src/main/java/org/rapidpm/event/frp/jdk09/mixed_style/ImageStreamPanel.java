package org.rapidpm.event.frp.jdk09.mixed_style;

import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;

import java.util.stream.Stream;

public class ImageStreamPanel extends Composite {

  private Layout imagePanels = new HorizontalLayout();
  private Panel  panel       = new Panel(imagePanels);

  public ImageStreamPanel() {
    setCompositionRoot(panel);
  }

  private void postConstruct() {

  }

  public void clear() {
    imagePanels.removeAllComponents();
  }

  public void addImagePanel(ImagePanel imagePanel) {
    imagePanels.addComponent(imagePanel);
  }

  public void addImagePanels(Stream<ImagePanel> imagePanelStream) {
    imagePanelStream.forEach(imagePanels::addComponent);
  }

}
