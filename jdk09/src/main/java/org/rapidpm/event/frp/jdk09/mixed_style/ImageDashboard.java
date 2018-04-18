package org.rapidpm.event.frp.jdk09.mixed_style;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.event.frp.jdk09.mixed_style.filter.gray.GrayScaleFilter;
import org.rapidpm.event.frp.jdk09.mixed_style.filter.points.PointsFilter;
import org.rapidpm.event.frp.jdk09.mixed_style.filter.resize.ResizeFilter;
import org.rapidpm.event.frp.jdk09.mixed_style.filter.rotate.Rotate45DegreeFilter;

import java.io.ByteArrayInputStream;
import java.util.stream.IntStream;

import static org.rapidpm.event.frp.jdk09.mixed_style.ImageUtils.fileName;
import static org.rapidpm.event.frp.jdk09.mixed_style.ImageUtils.readImageWithIdAsBytes;


public class ImageDashboard extends Composite implements HasLogger {

  private FilterCheckBox              filterCheckBox = new FilterCheckBox();
  private Panel                       statusPanel    = new Panel();
  private Layout                      layoutHeader   = new HorizontalLayout(filterCheckBox,
                                                                            statusPanel
  );
  private Layout                      layoutResults  = new VerticalLayout();
  private Panel                       panelResults   = new Panel(layoutResults);
  private Layout                      layout         = new VerticalLayout(layoutHeader,
                                                                          panelResults
  );
  private FilterCheckBox.Registration registration;

  public ImageDashboard() {
    logger().warning("ImageDashboard will be created...");
    postConstruct();
    setCompositionRoot(layout);
  }

  private void postConstruct() {
    panelResults.setCaption("Results");
    panelResults.setWidth(100, Unit.PERCENTAGE);
    panelResults.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

    ((HorizontalLayout) layoutHeader).setExpandRatio(statusPanel, 1);
    layoutHeader.setHeight(380, Unit.PIXELS);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);
    registration = filterCheckBox.register(info -> {
      layoutResults.removeAllComponents();

      IntStream
          .range(1, info.getAmount())
          .mapToObj(i -> readImageWithIdAsBytes(fileName(i)))
          .map(bytes -> {
            final ResizeFilter resizeFilter = new ResizeFilter();
            resizeFilter.setPercentage(info.getSize()
                                           .replace("%",
                                                    ""
                                           ));
            return resizeFilter.workOn(bytes);
          })
          .map(bytes -> { //gray if selected
            if (info.isFilterGrayscale()) {
              return new GrayScaleFilter().workOn(bytes);
            } else {
              return bytes;
            }
          })
          .map(bytes -> { //pointers if selected
            if (info.isFilterPointerize()) {
              return new PointsFilter().workOn(bytes);
            } else {
              return bytes;
            }
          })
          .map(bytes -> { //rotate45 if selected
            if (info.isFilterRotate()) {
              return new Rotate45DegreeFilter().workOn(bytes);
            } else {
              return bytes;
            }
          })
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
