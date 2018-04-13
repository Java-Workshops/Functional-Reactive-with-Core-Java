package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;

import java.io.ByteArrayInputStream;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.*;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.filter.FilterFunctions.*;


public class ImageDashboard extends Composite implements HasLogger {

  private FilterCheckBox              filterCheckBox = new FilterCheckBox();
  private ImagePanel                  image          = new ImagePanel();
  private Layout                      layoutOrig     = new HorizontalLayout(filterCheckBox,
                                                                            image
  );
  private Layout                      layoutResults  = new HorizontalLayout();
  private Panel                       panelResults   = new Panel(layoutResults);
  private Layout                      layout         = new VerticalLayout(layoutOrig,
                                                                          panelResults
  );
  private FilterCheckBox.Registration registration;

  public ImageDashboard() {
    logger().warning("ImageDashboard will be created...");
    postConstruct();
    setCompositionRoot(layout);
  }

  private void postConstruct() {

    StreamResource streamResource = nextImageName()
        .andThen(imageAsStreamRessouce())
        .apply(23);

    streamResource.setCacheTime(0);
    image.setStreamRessoure(streamResource);

    ((HorizontalLayout) layoutOrig).setExpandRatio(image, 1);

    panelResults.setCaption("Results");
    panelResults.setWidth(100, Unit.PERCENTAGE);
    panelResults.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);

    registration = filterCheckBox.register(info -> {
      layoutResults.removeAllComponents();

      byte[] bytes             = readImageWithIdAsBytes().apply(info.getFilename());
      byte[] resizedImageBytes = new byte[0];
      byte[] embossImageBytes  = new byte[0];
      byte[] grayImageBytes    = new byte[0];
      byte[] pointsImageBytes  = new byte[0];
      byte[] rotatedImageBytes = new byte[0];


      StreamResource streamResource1 = imageAsStreamRessouce().apply(info.getFilename());
      streamResource1.setCacheTime(0);
      image.setStreamRessoure(streamResource1);


      String percentage = info.getSize()
                              .replace("%",
                                       ""
                              );
      double scale = (percentage.equals("100"))
                     ? 1
                     : (percentage.equals("50"))
                       ? 0.5
                       : (percentage.equals("25"))
                         ? 0.25
                         : 1;

      resizedImageBytes = resize().apply(scale, bytes);

      final ImagePanel imagePanelResized = createImagePanel(resizedImageBytes, "thumbnail");
      layoutResults.addComponent(imagePanelResized);

      // emboss
      if (info.getFilterEmboss()) {
        embossImageBytes = emboss().apply(resizedImageBytes);
        final ImagePanel imagePanelEmboss = createImagePanel(embossImageBytes, "emboss");
        layoutResults.addComponent(imagePanelEmboss);
      }

      // grayscale
      if (info.getFilterGrayscale()) {
        grayImageBytes = grayscale().apply(resizedImageBytes);
        final ImagePanel imagePanelGrayscale = createImagePanel(grayImageBytes, "grayscale");
        layoutResults.addComponent(imagePanelGrayscale);
      }

      //points
      if (info.getFilterPointerize()) {
        pointsImageBytes = points()
            .apply((info.getFilterGrayscale())
                   ? grayImageBytes
                   : resizedImageBytes);
        final ImagePanel imagePanelPoints = createImagePanel(pointsImageBytes, "points");
        layoutResults.addComponent(imagePanelPoints);
      }

      // rotate

      if (info.getFilterRotate()) {
        byte[] toUse;
        if (info.getFilterGrayscale()) {
          if (info.getFilterPointerize()) {
            toUse = pointsImageBytes;
          } else {
            toUse = grayImageBytes;
          }
        } else {
          if (info.getFilterPointerize()) {
            toUse = pointsImageBytes;
          } else {
            toUse = resizedImageBytes;
          }
        }

        rotatedImageBytes = rotate45Degree().apply(toUse);
        final ImagePanel imagePanelRotated = createImagePanel(rotatedImageBytes, "rotated");
        layoutResults.addComponent(imagePanelRotated);
      }
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
