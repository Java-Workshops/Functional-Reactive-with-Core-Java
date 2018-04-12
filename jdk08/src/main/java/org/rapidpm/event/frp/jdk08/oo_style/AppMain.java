package org.rapidpm.event.frp.jdk08.oo_style;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.rapidpm.dependencies.core.logger.HasLogger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.Optional;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.redirect;
import static io.undertow.servlet.Servlets.servlet;

public class AppMain implements HasLogger {

  private static Optional<AppMain> appMainOptional = Optional.empty();

  // TODO - handle the exception properly
  public static void main(String[] args) throws ServletException {
    final AppMain main = new AppMain();
    appMainOptional = Optional.of(main);
    main.logger().info("start the server ...  NOW .. ");
    main.startup();
  }

  public void startup() throws ServletException {
    DeploymentInfo servletBuilder
        = Servlets.deployment()
                  .setClassLoader(AppMain.class.getClassLoader())
                  .setContextPath("/")
                  .setDeploymentName("ROOT.war")
                  .setDefaultEncoding("UTF-8")
                  .addServlets(
                      servlet(
                          CoreServlet.class.getSimpleName(),
                          CoreServlet.class
                      ).addMapping("/*")
                       .setAsyncSupported(true)
                  );

    final DeploymentManager manager = Servlets
        .defaultContainer()
        .addDeployment(servletBuilder);
    manager.deploy();
    PathHandler path = path(redirect("/"))
        .addPrefixPath("/", manager.start());
    Undertow.builder()
            .addHttpListener(8899, "0.0.0.0")
            .setHandler(path)
            .build()
            .start();
  }


  @WebServlet("/*")
  @VaadinServletConfiguration(productionMode = false, ui = MyUI.class)
  public static class CoreServlet extends VaadinServlet implements HasLogger {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
      super.init(servletConfig);
      logger().info("Servlet init....");
    }
  }

  //@PreserveOnRefresh
  @Push
  public static class MyUI extends UI implements HasLogger {
    @Override
    protected void init(VaadinRequest request) {
      logger().info("MyUI init...");
      setContent(new ImageDashboard());
    }
  }
}
