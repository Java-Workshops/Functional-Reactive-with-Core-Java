package org.rapidpm.event.frp.jdk09.functional_reactive_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.FilterCheckBox;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImagePanel;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImageStreamPanel;

import java.io.ByteArrayInputStream;
import java.util.stream.IntStream;

import static org.rapidpm.event.frp.jdk09.functional_reactive_style.FilterFunctions.transform;
import static org.rapidpm.event.frp.jdk09.functional_reactive_style.v001.ImageUtils.fileName;
import static org.rapidpm.event.frp.jdk09.functional_reactive_style.v001.ImageUtils.readImageWithIdAsBytes;


public class ImageDashboard extends Composite implements HasLogger {

  private Layout logmsgLoadImg        = new VerticalLayout();
  private Layout logmsgTransformImg   = new VerticalLayout();
  private Layout logmsgCreateImgPanel = new VerticalLayout();
  private Layout logLayout            = new HorizontalLayout(logmsgLoadImg, logmsgTransformImg, logmsgCreateImgPanel);
  private Panel  statusPanel          = new Panel(logLayout);

  private FilterCheckBox filterCheckBox = new FilterCheckBox();
  private Layout         layoutHeader   = new HorizontalLayout(filterCheckBox, statusPanel);
  private Layout         layoutResults  = new VerticalLayout();
  private Panel          panelResults   = new Panel(layoutResults);
  private Layout         layout         = new VerticalLayout(layoutHeader, panelResults);

  private FilterCheckBox.Registration registration;

  public ImageDashboard() {
    logger().warning("ImageDashboard will be created...");

    layoutHeader.setHeight(400, Unit.PIXELS);
    layoutHeader.setWidth(100f, Unit.PERCENTAGE);
    ((HorizontalLayout) layoutHeader).setExpandRatio(statusPanel, 1);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);
    filterCheckBox.setWidth(210f, Unit.PIXELS);

    statusPanel.setCaption("Log Messages");
    statusPanel.setHeight(100f, Unit.PERCENTAGE);

    ((HorizontalLayout) logLayout).setExpandRatio(logmsgLoadImg, 0.33f);
    ((HorizontalLayout) logLayout).setExpandRatio(logmsgTransformImg, 0.33f);
    ((HorizontalLayout) logLayout).setExpandRatio(logmsgCreateImgPanel, 0.33f);


    panelResults.setCaption("Results");
    panelResults.setWidth(100, Unit.PERCENTAGE);
    panelResults.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

    postConstruct();
    setCompositionRoot(layout);
  }

  private void postConstruct() {

    registration = filterCheckBox.register(info -> {

      layoutResults.removeAllComponents();
      logmsgLoadImg.removeAllComponents();
      logmsgTransformImg.removeAllComponents();
      logmsgCreateImgPanel.removeAllComponents();


      // the CPU intensive part inside the UI - Thread
      IntStream
          .range(1, info.getAmount())
          .mapToObj(i -> readImageWithIdAsBytes(fileName(i)))
          .map(bytes -> transform().apply(info).apply(bytes))
          .map(bytes -> createImagePanel(bytes, "thumbnail"))
          .map(imagePanel -> {
            final ImageStreamPanel imageStreamPanel = new ImageStreamPanel();
            imageStreamPanel.addImagePanel(imagePanel);
            return imageStreamPanel;
          })
          .forEach(layoutResults::addComponent);
    });
  }

  private ImagePanel createImagePanel(byte[] input, String name) {
    final StreamResource streamResourceThumb = new StreamResource(
        (StreamResource.StreamSource) () -> new ByteArrayInputStream(input),
        name + " - thumb"
    );
    streamResourceThumb.setCacheTime(0);

    final ImagePanel imagePanel = new ImagePanel();
    imagePanel.setCaption(name);
    imagePanel.setStreamRessoure(streamResourceThumb);
    return imagePanel;
  }

  @Override
  public void detach() {
    super.detach();
    registration.remove();
  }
}
