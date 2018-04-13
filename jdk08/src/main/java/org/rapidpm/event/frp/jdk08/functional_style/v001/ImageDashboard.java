package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;

import static org.rapidpm.event.frp.jdk08.functional_style.v001.FilterFunctions.*;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.*;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImagePanel.imagePanel;


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
        .andThen(imageAsStreamResourceNoCache())
        .apply(23);
//
    image.setStreamResoure(streamResource);

    ((HorizontalLayout) layoutOrig).setExpandRatio(image, 1);

    panelResults.setCaption("Results");
    panelResults.setWidth(100, Unit.PERCENTAGE);
    panelResults.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);

    registration = filterCheckBox.register(info -> {
      layoutResults.removeAllComponents();

      byte[] bytes             = readImageWithIdAsBytes().apply(info.getFilename());
      byte[] resizedImageBytes = new byte[0];
      byte[] grayImageBytes    = new byte[0];
      byte[] pointsImageBytes  = new byte[0];
      byte[] rotatedImageBytes = new byte[0];

      image.setStreamResoure(imageAsStreamResourceNoCache().apply(info.getFilename()));

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

      layoutResults.addComponent(imagePanel().apply(resizedImageBytes, "thumbnail"));

      if (info.getFilterEmboss()) {
        layoutResults.addComponent(imagePanel().apply(emboss().apply(resizedImageBytes), "emboss"));
      }

      if (info.getFilterGrayscale()) {
        grayImageBytes = grayscale().apply(resizedImageBytes);
        layoutResults.addComponent(imagePanel().apply(grayImageBytes, "grayscale"));
      }

      if (info.getFilterPointerize()) {
        pointsImageBytes = points()
            .apply((info.getFilterGrayscale())
                   ? grayImageBytes
                   : resizedImageBytes);
        layoutResults.addComponent(imagePanel().apply(pointsImageBytes, "points"));
      }

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

        layoutResults.addComponent(imagePanel().apply(rotatedImageBytes, "rotated"));
      }
    });
  }



  @Override
  public void detach() {
    super.detach();
    registration.remove();
  }
}
