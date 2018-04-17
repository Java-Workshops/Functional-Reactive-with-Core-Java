package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.Pair;

import java.util.function.Function;

import static java.lang.Double.parseDouble;
import static org.atmosphere.annotation.AnnotationUtil.logger;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.FilterFunctions.*;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.*;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImagePanel.imagePanelCurried;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.memoizer.Memoizer.memoize;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;


public class ImageDashboard extends Composite implements HasLogger {

  public static final int BORDER = 23;

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

    ((HorizontalLayout) layoutOrig).setExpandRatio(this.image, 1);

    panelResults.setCaption("Results");
    panelResults.setWidth(100, Unit.PERCENTAGE);
    panelResults.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

    filterCheckBox.setHeight(100f, Unit.PERCENTAGE);

    postConstruct();
    setCompositionRoot(layout);
  }


  private void postConstruct() {
    nextImageName()
        .andThen(loadOrigOrFailedImg())
        .andThen(toStreamResourceNoCache())
        .apply(BORDER)
        .ifPresentOrElse(v -> image.setStreamResoure(v),
                         logger()::warning
        );

    registration = filterCheckBox.register(info -> {
      layoutResults.removeAllComponents();

      final Boolean isEmboss      = info.isFilterEmboss();
      final Boolean isGray        = info.isFilterGrayscale();
      final Boolean isPointerized = info.isFilterPointerize();
      final Boolean isRotated     = info.isFilterRotate();
      final String  filename      = info.getFilename();
      final String  percentage    = info.getSize();

      final Function<String, Pair<String, byte[]>> loadImgMemoized = memoize(loadOrigOrFailedImg());

      loadImgMemoized
          .andThen(toStreamResourceNoCache())
          .apply(filename)
          .ifPresentOrElse(image::setStreamResoure,
                           logger()::warning
          );

      //How to deal with Exceptions
      final Function<String, Double> factor = (s) -> parseDouble(s.replace("%", "")) * 0.01;

      final Function<byte[], byte[]> resize = factor.andThen(resizeCurried())
                                                    .apply(percentage);


      final Function<String, byte[]> loadResizedImg = memoize(loadOrigOrFailedImg()
                                                                  .andThen(input -> resize.apply(input.getT2())));

      loadResizedImg
          .andThen(imagePanelCurried().apply("thumbnail"))
          .apply(filename)
          .ifPresentOrElse(layoutResults::addComponent,
                           logger::warn
          );

      // if is imperative - how to get it out f this code ?
      if (isEmboss) {
        loadResizedImg
            .andThen(emboss())
            .andThen(imagePanelCurried().apply("emboss"))
            .apply(filename)
            .ifPresentOrElse(layoutResults::addComponent,
                             logger::warn
            );
      }

      //memoizing grayscale
      Function<byte[], byte[]> grayscale = memoize(grayscale());

      if (isGray) {
        loadResizedImg
            .andThen(grayscale)
            .andThen(imagePanelCurried().apply("grayscale"))
            .apply(filename)
            .ifPresentOrElse(layoutResults::addComponent,
                             logger::warn
            );
      }

      //memoizing points
      Function<byte[], byte[]> points = memoize(points());

      match(
          matchCase(() -> failure("filter Pointerize is not selected ")),
          matchCase(() -> (isPointerized && isGray),
                    () -> success(grayscale.andThen(points))
          ),
          matchCase(() -> (isPointerized && !isGray),
                    () -> success(points)
          )
      ).map(loadResizedImg::andThen)
       .map(imagePanelCurried().apply("points")::compose)
       .flatMap(f -> f.apply(filename))
       .ifPresentOrElse(
           layoutResults::addComponent,
           logger::warn
       );


      match(
          matchCase(() -> failure("filter Rotate45Degree is not selected")),
          matchCase(() -> (isRotated && isGray && isPointerized),
                    () -> success(grayscale.andThen(points).andThen(rotate45Degree()))
          ),
          matchCase(() -> (isRotated && isGray && !isPointerized),
                    () -> success(grayscale.andThen(rotate45Degree()))
          ),
          matchCase(() -> (isRotated && !isGray && isPointerized),
                    () -> success(points.andThen(rotate45Degree()))
          ),
          matchCase(() -> (isRotated && !isGray && !isPointerized),
                    () -> success(rotate45Degree())
          )
      ).map(loadResizedImg::andThen)
       .map(imagePanelCurried().apply("rotated")::compose)
       .flatMap(f -> f.apply(filename))
       .ifPresentOrElse(
           layoutResults::addComponent,
           logger::warn
       );
    });
  }


  @Override
  public void detach() {
    super.detach();
    registration.remove();
  }
}
