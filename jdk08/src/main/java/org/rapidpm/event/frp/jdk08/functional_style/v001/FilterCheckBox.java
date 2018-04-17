package org.rapidpm.event.frp.jdk08.functional_style.v001;

import com.vaadin.ui.*;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.serial.Sext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static org.rapidpm.event.frp.jdk08.functional_style.v001.ImageFunctions.*;

// tumbnail -> emboss
// tumbnail -> grayscale -> pointerize
// tumbnail -> grayscale -> rotate

public class FilterCheckBox extends Composite implements HasLogger {

  private ComboBox<String> filenames = new ComboBox<>();

  private CheckBox filterEmboss     = new CheckBox();
  private CheckBox filterGrayscale  = new CheckBox();
  private CheckBox filterPointerize = new CheckBox();
  private CheckBox filterRotate     = new CheckBox();


  private ComboBox<String> sizePercentage = new ComboBox<>();
  private Button           transform      = new Button();
  private FormLayout       layout         = new FormLayout(filenames,
                                                           filterEmboss,
                                                           filterGrayscale,
                                                           filterPointerize,
                                                           filterRotate,
                                                           sizePercentage,
                                                           transform
  );

  private Panel         panel    = new Panel(layout);
  private Set<Receiver> registry = newKeySet();

  public FilterCheckBox() {
    postConstruct();
    setCompositionRoot(panel);
  }

  public Registration register(Receiver receiver) {
    registry.add(receiver);
    return () -> {
      logger().warning("Remove Registration .. NOW .. ");
      return registry.remove(receiver);
    };
  }

  private void postConstruct() {

    panel.setCaption("Filter Tool Box");

    List<String> filenameList = IntStream
        .rangeClosed(1, 23)
        .mapToObj(id -> formatID().andThen(filename()).apply(id))
        .collect(Collectors.toList());

    filenames.setItems(filenameList);
    filenames.setPlaceholder("select filename");

    filterEmboss.setCaption("Emboss");
    filterEmboss.setValue(false);
    filterGrayscale.setCaption("Grayscale");
    filterGrayscale.setValue(false);
    filterPointerize.setCaption("Pointerize");
    filterPointerize.setValue(false);
    filterRotate.setCaption("Rotate 45");
    filterRotate.setValue(false);

    sizePercentage.setItems("100%", "50%", "25%");
    sizePercentage.setPlaceholder("select the size..");

    transform.setCaption("activate filter");
    transform.addClickListener(
        (Button.ClickListener) event -> {
          logger().info("transform button clicked .. ");
          //send event with info
          registry
              .forEach(receiver -> receiver
                  .update(new Info(
                              filenames.getSelectedItem()
                                       .orElseGet(() -> nextImageName().apply(20)),
                              sizePercentage.getSelectedItem()
                                            .orElse("100%"),
                              isFilterEmbossSelected(),
                              isFilterGrayscaleSelected(),
                              isFilterPointerizeSelected(),
                              isFilterRotateSelected()
                          )
                  ));
        }
    );


  }

  public Boolean isFilterEmbossSelected() { return filterEmboss.getValue(); }

  public Boolean isFilterGrayscaleSelected() { return filterGrayscale.getValue(); }

  public Boolean isFilterPointerizeSelected() { return filterPointerize.getValue(); }

  public Boolean isFilterRotateSelected() { return filterRotate.getValue(); }

  public interface Receiver {
    void update(Info info);
  }

  public interface Registration {
    boolean remove();
  }

  public static class Info extends Sext<String, String, Boolean, Boolean, Boolean, Boolean> {

    public Info(
        String filename,
        String size,
        Boolean filterEmboss,
        Boolean filterGrayscale,
        Boolean filterPointerize,
        Boolean filterRotate) {
      super(filename, size, filterEmboss, filterGrayscale, filterPointerize, filterRotate);
    }

    public String getFilename() {
      return getT1();
    }

    public String getSize() {
      return getT2();
    }

    public Boolean isFilterEmboss() {
      return getT3();
    }

    public Boolean isFilterGrayscale() {
      return getT4();
    }

    public Boolean isFilterPointerize() {
      return getT5();
    }

    public Boolean isFilterRotate() {
      return getT6();
    }
  }

}
