package org.rapidpm.event.frp.jdk08.oo_style;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.event.frp.jdk08.oo_style.filter.emboss.EmbossFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.gray.GrayScaleFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.points.PointsFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.resize.ResizeFilter;
import org.rapidpm.event.frp.jdk08.oo_style.filter.rotate.Rotate45DegreeFilter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class ImageDashboard extends Composite implements HasLogger {

  private FilterCheckBox filterCheckBox = new FilterCheckBox();
  private ImagePanel     image          = new ImagePanel();
  private Layout         layoutOrig     = new HorizontalLayout(filterCheckBox,
                                                               image
  );
  private Layout         layoutResults  = new HorizontalLayout();
  private Layout         layout         = new VerticalLayout(layoutOrig,
                                                             layoutResults
  );

  public ImageDashboard() {
    logger().warning("ImageDashboard will be created...");
    postConstruct();
    setCompositionRoot(layout);
  }

  private FilterCheckBox.Registration registration;

  private String nextImageName = ImageUtils.nextImageName(100);

  private void postConstruct() {

    StreamResource streamResource = ImageUtils.imageAsStreamRessouce(nextImageName);
    streamResource.setCacheTime(0);
    image.setStreamRessoure(streamResource);

    ((HorizontalLayout) layoutOrig).setExpandRatio(image, 1);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);

    registration = filterCheckBox.register(new FilterCheckBox.Receiver() {
      @Override
      public void update(FilterCheckBox.Info info) {
        layoutResults.removeAllComponents();

        //process all steps.... -> write REAMDE.md

        // tumbnail -> emboss
        // tumbnail -> grayscale -> pointerize
        // tumbnail -> grayscale -> rotate


        byte[] bytes             = ImageUtils.readImageWithIdAsBytes(nextImageName);
        byte[] resizedImageBytes = new byte[0];
        byte[] embossImageBytes  = new byte[0];
        byte[] grayImageBytes    = new byte[0];
        byte[] pointsImageBytes  = new byte[0];
        byte[] rotatedImageBytes = new byte[0];

        final ResizeFilter resizeFilter = new ResizeFilter();
        resizeFilter.setPercentage(info.getSize()
                                       .replace("%",
                                                ""
                                       ));
        resizedImageBytes = resizeFilter.workOn(bytes);
        final ImagePanel imagePanelResized = createImagePanel(resizedImageBytes, "thumbnail");
        layoutResults.addComponent(imagePanelResized);


        // emboss
        if (info.getFilterEmboss()) {
          EmbossFilter embossFilter = new EmbossFilter();
          embossImageBytes = embossFilter.workOn(resizedImageBytes);
          final ImagePanel imagePanelEmboss = createImagePanel(embossImageBytes, "emboss");
          layoutResults.addComponent(imagePanelEmboss);
        }

        // grayscale
        if (info.getFilterGrayscale()) {
          final GrayScaleFilter grayScaleFilter = new GrayScaleFilter();
          grayImageBytes = grayScaleFilter.workOn(resizedImageBytes);
          final ImagePanel imagePanelGrayscale = createImagePanel(grayImageBytes, "grayscale");
          layoutResults.addComponent(imagePanelGrayscale);
        }

        //points
        if (info.getFilterPointerize()) {
          PointsFilter pointsFilter = new PointsFilter();
          pointsImageBytes = pointsFilter.workOn((info.getFilterGrayscale()) ? grayImageBytes : resizedImageBytes);
          final ImagePanel imagePanelPoints = createImagePanel(pointsImageBytes, "points");
          layoutResults.addComponent(imagePanelPoints);
        }

        // rotate

        if(info.getFilterRotate()){
          byte[] toUse;
          if(info.getFilterGrayscale()){
            if(info.getFilterPointerize()){
              toUse = pointsImageBytes;
            } else{
              toUse = grayImageBytes;
            }
          } else {
            if(info.getFilterPointerize()){
              toUse = pointsImageBytes;
            } else{
              toUse = resizedImageBytes;
            }
          }
          Rotate45DegreeFilter rotate45DegreeFilter = new Rotate45DegreeFilter();
          rotatedImageBytes = rotate45DegreeFilter.workOn(toUse);
          final ImagePanel imagePanelRotated = createImagePanel(rotatedImageBytes, "rotated");
          layoutResults.addComponent(imagePanelRotated);
        }
      }
    });
  }

  private ImagePanel createImagePanel(byte[] input, String name) {
    final StreamResource streamResourceThumb = new StreamResource(
        new StreamResource.StreamSource() {
          @Override
          public InputStream getStream() {
            return new ByteArrayInputStream(input);
          }
        },
        nextImageName + " - thumb"
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
