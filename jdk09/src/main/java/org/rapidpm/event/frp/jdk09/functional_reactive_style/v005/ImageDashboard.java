package org.rapidpm.event.frp.jdk09.functional_reactive_style.v005;

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
import org.rapidpm.frp.reactive.CompletableFutureQueue;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.time.LocalTime.now;
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


  //methods for first extracting to CFQ


  private Function<
      Triple<String, FilterCheckBox.Info, Function<String, Result<Pair<String, byte[]>>>>,
      Result<Quad<FilterCheckBox.Info, String, byte[], String>>> step01() {

    return (triple) -> {
      final Function<String, Result<Pair<String, byte[]>>> readImage = triple.getT3();
      return readImage
          .apply(triple.getT1())
          .map(e -> Quad.next(triple.getT2(), e.getT1(), e.getT2(), now() + " - load img " + triple.getT1()));

    };
  }

  private Function<
      Result<Quad<FilterCheckBox.Info, String, byte[], String>>,
      Result<Quad<FilterCheckBox.Info, String, byte[], Pair<String, String>>>> step02() {
    return (input) -> input
        .map(quad -> {
          byte[] bytes = transform().apply(quad.getT1()).apply(quad.getT3());
          return Quad.next(quad.getT1(),
                           quad.getT2(),
                           bytes,
                           Pair.next(quad.getT4(),
                                     now() + " - filter img " + quad.getT2()
                           )
          );
        });
  }

  private Function<
      Result<Quad<FilterCheckBox.Info, String, byte[], Pair<String, String>>>,
      Result<Triple<String, String, String>>> step03() {
    return (input) -> {
      input
          .ifPresentOrElse(
              quad -> layoutResults
                  .getUI()
                  .access(() -> {
                    final ImagePanel imagePanel = createImagePanel(quad.getT3(),
                                                                   quad.getT2() + " - thumbnail"
                    );
                    final ImageStreamPanel imageStreamPanel = new ImageStreamPanel();
                    imageStreamPanel.addImagePanel(imagePanel);
                    layoutResults.addComponent(imageStreamPanel);

                    //adding log messages
                    Pair<String, String> msg = quad.getT4();
                    logger().info("msg = " + msg);
                    logmsgLoadImg.addComponent(new Label(msg.getT1()));
                    logmsgTransformImg.addComponent(new Label(msg.getT2()));
                    logmsgCreateImgPanel.addComponent(new Label(now() + " - ui elem. "));
                  }),
              logger()::warning
          );

      return input.map(q -> Triple.next(
          q.getT4().getT1(),
          q.getT4().getT2(),
          now() + " - ui elem. "
      )); //VOID
    };
  }


  private void postConstruct() {

    registration = filterCheckBox.register(info -> {

      layoutResults.removeAllComponents();
      logmsgLoadImg.removeAllComponents();
      logmsgTransformImg.removeAllComponents();
      logmsgCreateImgPanel.removeAllComponents();

      // compact code / extract functions
      IntStream
          .rangeClosed(1, info.getAmount())
          .mapToObj(i -> formatID().andThen(filename()).apply(i))
          .map(filename -> Triple.next(filename, info, readImageWithIdAsBytes()))
          .map(e -> Pair.next(
              e,
              CompletableFutureQueue
                  .define(step01())
                  .thenCombineAsync(step02())
                  .thenCombineAsync(step03())
                  .resultFunction()
          ))
          .map(e -> e.getT2().apply(e.getT1()))
          .forEach(e -> System.out.println(e.getNumberOfDependents()));

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
