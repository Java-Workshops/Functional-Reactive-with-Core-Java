package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.matcher.Case;
import org.rapidpm.frp.model.Result;

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
      byte[] grayImageBytes    = new byte[0];
      byte[] pointsImageBytes  = new byte[0];
      byte[] rotatedImageBytes = new byte[0];

      image.setStreamResoure(imageAsStreamResourceNoCache().apply(info.getFilename()));

      String percentage = info.getSize()
                              .replace("%",
                                       ""
                              );

      Result<Double> scale = Case
          .match(
              Case.matchCase(() -> Result.success(1.0)),
              Case.matchCase(() -> percentage.equals("100"), () -> Result.success(1.0)),
              Case.matchCase(() -> percentage.equals("50"), () -> Result.success(0.5)),
              Case.matchCase(() -> percentage.equals("25"), () -> Result.success(0.25))
          );

      Result<byte[]> resizedImageBytes = scale
          .thenCombine(bytes, (aDouble, image) -> Result.success(resize().apply(aDouble, image)));

      resizedImageBytes.ifPresent(v -> layoutResults.addComponent(imagePanel().apply(v, "thumbnail")));


      if (info.isFilterEmboss()) {
        layoutResults.addComponent(imagePanel().apply(emboss().apply(resizedImageBytes.get()), "emboss"));
      }

      if (info.isFilterGrayscale()) {
        grayImageBytes = grayscale().apply(resizedImageBytes.get());
        layoutResults.addComponent(imagePanel().apply(grayImageBytes, "grayscale"));
      }

      if (info.isFilterPointerize()) {
        pointsImageBytes = points()
            .apply((info.isFilterGrayscale())
                   ? grayImageBytes
                   : resizedImageBytes.get());
        layoutResults.addComponent(imagePanel().apply(pointsImageBytes, "points"));
      }

      if (info.isFilterRotate()) {
        byte[] toUse;
        if (info.isFilterGrayscale()) {
          if (info.isFilterPointerize()) {
            toUse = pointsImageBytes;
          } else {
            toUse = grayImageBytes;
          }
        } else {
          if (info.isFilterPointerize()) {
            toUse = pointsImageBytes;
          } else {
            toUse = resizedImageBytes.get();
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
