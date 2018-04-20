package org.rapidpm.event.frp.jdk09.functional_reactive_style.v003;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.FilterCheckBox;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImagePanel;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImageStreamPanel;
import org.rapidpm.frp.model.Pair;
import org.rapidpm.frp.model.Result;
import org.rapidpm.frp.model.Triple;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.time.LocalTime.now;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.rapidpm.event.frp.jdk09.functional_reactive_style.FilterFunctions.transform;
import static org.rapidpm.event.frp.jdk09.functional_reactive_style.ImageFunctions.*;


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


  private Consumer<String> logLoadImg() {
    return (message) -> statusPanel
        .getUI()
        .access(() -> logmsgLoadImg
            .addComponent(new Label(now() + " - " + message)));
  }

  private Consumer<String> logTransformImg() {
    return (message) -> statusPanel
        .getUI()
        .access(() -> logmsgTransformImg
            .addComponent(new Label(now() + " - " + message)));
  }

  private Consumer<String> logCreateImgPanel() {
    return (message) -> statusPanel
        .getUI()
        .access(() -> logmsgCreateImgPanel
            .addComponent(new Label(now() + " - " + message)));
  }


  private void postConstruct() {

    registration = filterCheckBox.register(info -> {

      layoutResults.removeAllComponents();
      logmsgLoadImg.removeAllComponents();
      logmsgTransformImg.removeAllComponents();
      logmsgCreateImgPanel.removeAllComponents();


      // one CF for every step - too many UI updates -> switch to compact logging
      IntStream
          .rangeClosed(1, info.getAmount())
          .mapToObj(i -> formatID().andThen(filename()).apply(i))
          .map(filename -> Triple.next(filename, info, readImageWithIdAsBytes()))
          .map(triple -> { // running out of the UI Thread
            return supplyAsync(() -> {
              final Function<String, Result<Pair<String, byte[]>>> readImage = triple.getT3();
              return readImage
                  .apply(triple.getT1())
                  .map(e -> {
                    logLoadImg().accept("load img " + triple.getT1());
                    return Triple.next(triple.getT2(), e.getT1(), e.getT2());
                  });
            });
          })
          .map(cf -> cf.thenApplyAsync(result -> result
              .map(triple -> {
                byte[] bytes = transform().apply(triple.getT1()).apply(triple.getT3());
                return Triple.next(triple.getT1(), triple.getT2(), bytes);
              })
              .map(triple -> {
                logTransformImg().accept("filter img " + triple.getT2());
                return triple;
              }))
          )
          .forEach(cf -> cf.thenAcceptAsync((result) -> result
              .map(e -> {
                logCreateImgPanel().accept("create ui elem. " + e.getT2());
                return e;
              })
              .ifPresentOrElse(
                  triple -> layoutResults
                      .getUI()
                      .access(() -> {
                        final ImagePanel       imagePanel       = createImagePanel(triple.getT3(), triple.getT2() + " - thumbnail");
                        final ImageStreamPanel imageStreamPanel = new ImageStreamPanel();
                        imageStreamPanel.addImagePanel(imagePanel);
                        layoutResults.addComponent(imageStreamPanel);
                      }),
                  logger()::warning
              )));


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
