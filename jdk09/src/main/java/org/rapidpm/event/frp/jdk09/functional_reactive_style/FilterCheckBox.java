package org.rapidpm.event.frp.jdk09.functional_reactive_style;

import com.vaadin.ui.*;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.serial.Quint;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

// tumbnail -> emboss
// tumbnail -> grayscale -> pointerize
// tumbnail -> grayscale -> rotate

public class FilterCheckBox extends Composite implements HasLogger {

  private ComboBox<Integer> amount = new ComboBox<>();

  //  private CheckBox filterEmboss     = new CheckBox();
  private CheckBox filterGrayscale  = new CheckBox();
  private CheckBox filterPointerize = new CheckBox();
  private CheckBox filterRotate     = new CheckBox();


  private ComboBox<String> sizePercentage = new ComboBox<>();
  private Button           transform      = new Button();
  private FormLayout       layout         = new FormLayout(amount,
//                                                           filterEmboss,
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
    amount.setItems(List.of(0, 1, 4, 8, 12, 16, 20, 23));
    amount.setPlaceholder("select amount");

//    filterEmboss.setCaption("Emboss");
//    filterEmboss.setValue(false);
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
          for (Receiver receiver : registry) {
            receiver.update(new Info(
                                amount.getSelectedItem()
                                      .orElse(4),
                                sizePercentage.getSelectedItem()
                                              .orElse("25%"),
//                                isFilterEmbossSelected(),
                                isFilterGrayscaleSelected(),
                                isFilterPointerizeSelected(),
                                isFilterRotateSelected()
                            )
            );
          }
        }
    );
  }

//  public Boolean isFilterEmbossSelected() { return filterEmboss.getValue(); }

  public Boolean isFilterGrayscaleSelected() { return filterGrayscale.getValue(); }

  public Boolean isFilterPointerizeSelected() { return filterPointerize.getValue(); }

  public Boolean isFilterRotateSelected() { return filterRotate.getValue(); }

  public interface Receiver {
    void update(Info info);
  }

  public interface Registration {
    boolean remove();
  }

  public static class Info extends Quint<Integer, String, Boolean, Boolean, Boolean> {

    public Info(
        Integer amount,
        String size,
//        Boolean filterEmboss,
        Boolean filterGrayscale,
        Boolean filterPointerize,
        Boolean filterRotate) {
      super(amount, size, filterGrayscale, filterPointerize, filterRotate);
    }

    public Integer getAmount() {
      return getT1();
    }

    public String getSize() {
      return getT2();
    }

//    public Boolean isFilterEmboss() {
//      return getT3();
//    }

    public Boolean isFilterGrayscale() {
      return getT3();
    }

    public Boolean isFilterPointerize() {
      return getT4();
    }

    public Boolean isFilterRotate() {
      return getT5();
    }
  }

}
