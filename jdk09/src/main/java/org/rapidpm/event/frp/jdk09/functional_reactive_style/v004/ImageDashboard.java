package org.rapidpm.event.frp.jdk09.functional_reactive_style.v004;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.FilterCheckBox;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImagePanel;
import org.rapidpm.event.frp.jdk09.functional_reactive_style.ImageStreamPanel;
import org.rapidpm.frp.model.Pair;
import org.rapidpm.frp.model.Quad;
import org.rapidpm.frp.model.Result;
import org.rapidpm.frp.model.Triple;

import java.io.ByteArrayInputStream;
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

  private void postConstruct() {

    registration = filterCheckBox.register(info -> {

      layoutResults.removeAllComponents();
      logmsgLoadImg.removeAllComponents();
      logmsgTransformImg.removeAllComponents();
      logmsgCreateImgPanel.removeAllComponents();


      //###########################
      // no witch compact logging (instead of regular logger)
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
//                    logLoadImg().accept("load img " + triple.getT1());
                    return Quad.next(triple.getT2(), e.getT1(), e.getT2(), now() + " - load img " + triple.getT1());
                  });
            });
          })
          .map(cf -> cf.thenApplyAsync(result -> result
                   .map(quad -> {
                     byte[] bytes = transform().apply(quad.getT1()).apply(quad.getT3());
                     return Quad.next(quad.getT1(), quad.getT2(), bytes, quad.getT4());
                   })
                   .map(quad -> {
//                logTransformImg().accept("filter img " + quad.getT2());
                     return Quad.next(quad.getT1(), quad.getT2(), quad.getT3(), Pair.next(quad.getT4(), now() + " - filter img " + quad.getT2()));
                   }))
          )
          .forEach(cf -> cf.thenAcceptAsync((result) -> result
              .ifPresentOrElse(
                  quad -> layoutResults
                      .getUI()
                      .access(() -> {
                        final ImagePanel       imagePanel       = createImagePanel(quad.getT3(), quad.getT2() + " - thumbnail");
                        final ImageStreamPanel imageStreamPanel = new ImageStreamPanel();
                        imageStreamPanel.addImagePanel(imagePanel);
                        layoutResults.addComponent(imageStreamPanel);

                        //adding log messages
                        Pair<String, String> msg = quad.getT4();
                        logmsgLoadImg.addComponent(new Label(msg.getT1()));
                        logmsgTransformImg.addComponent(new Label(msg.getT2()));
                        logmsgCreateImgPanel.addComponent(new Label(now() + " - ui elem. "));


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
