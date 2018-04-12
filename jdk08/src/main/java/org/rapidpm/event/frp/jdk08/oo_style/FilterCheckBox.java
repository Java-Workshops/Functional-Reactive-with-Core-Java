package org.rapidpm.event.frp.jdk08.oo_style;

import com.vaadin.data.HasValue;
import com.vaadin.ui.*;
import org.rapidpm.dependencies.core.logger.HasLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

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

  private Panel panel = new Panel(layout);

  public static class Info {

    private String  filename;
    private String  size;
    private Boolean filterEmboss;
    private Boolean filterGrayscale;
    private Boolean filterPointerize;
    private Boolean filterRotate;

    public Info(
        String filename,
        String size,
        Boolean filterEmboss,
        Boolean filterGrayscale,
        Boolean filterPointerize,
        Boolean filterRotate) {
      this.filename = filename;
      this.size = size;
      this.filterEmboss = filterEmboss;
      this.filterGrayscale = filterGrayscale;
      this.filterPointerize = filterPointerize;
      this.filterRotate = filterRotate;
    }

    public String getFilename() {
      return filename;
    }

    public String getSize() {
      return size;
    }

    public Boolean getFilterEmboss() {
      return filterEmboss;
    }

    public Boolean getFilterGrayscale() {
      return filterGrayscale;
    }

    public Boolean getFilterPointerize() {
      return filterPointerize;
    }

    public Boolean getFilterRotate() {
      return filterRotate;
    }
  }

  public interface Receiver {
    void update(Info info);
  }

  public interface Registration {
    boolean remove();
  }

  private Set<Receiver> registry = newKeySet();

  public Registration register(Receiver receiver) {
    registry.add(receiver);
    return new Registration() {
      @Override
      public boolean remove() {
        logger().warning("Remove Registration .. NOW .. ");
        return registry.remove(receiver);
      }
    };
  }


  public FilterCheckBox() {
    postConstruct();
    setCompositionRoot(panel);
  }


  private void postConstruct() {

    panel.setCaption("Filter Tool Box");

    List<String> filenameList = new ArrayList<>();
    for (int i = 1; i < 23; i++) {
      filenameList.add(ImageUtils.fileName(i));
    }
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
        new Button.ClickListener() {
          @Override
          public void buttonClick(Button.ClickEvent event) {
            logger().info("transform button clicked .. ");
            //send event with info
            for (Receiver receiver : registry) {
              receiver.update(new Info(
                                  filenames.getSelectedItem()
                                           .orElseGet(() -> ImageUtils.nextImageName(20)),
                                  sizePercentage.getSelectedItem()
                                                .orElse("100%"),
                                  isFilterEmbossSelected(),
                                  isFilterGrayscaleSelected(),
                                  isFilterPointerizeSelected(),
                                  isFilterRotateSelected()
                              )
              );
            }
          }
        }
    );


  }

  public Boolean isFilterEmbossSelected() { return filterEmboss.getValue(); }

  public Boolean isFilterGrayscaleSelected() { return filterGrayscale.getValue(); }

  public Boolean isFilterPointerizeSelected() { return filterPointerize.getValue(); }

  public Boolean isFilterRotateSelected() { return filterRotate.getValue(); }

}
