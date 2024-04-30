package femcoworking.servidor;


import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

        
        
/**
 * Classe principal del projecte basat amb Spring Boot
 * 
 */

@SpringBootApplication
public class ServidorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServidorApplication.class, args);
    }
    

 @Configuration
public class ServerConfig {
  
  @Bean
  public ServletWebServerFactory servletContainer() {
    /**
     * Permet trafic SSL.
     */
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
        @Override
        protected void postProcessContext(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
  }
    };
    
    /**
     * Agrega HTTP a HTTPS per redireccionar.
     */
    tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
    return tomcat;
  }
  
  /**
     * Necessitem redireccionar de HTTP fins HTTPS. Sense SSL, aquesta aplicació 
     * feia servir el port 8080. Amb SSL fara servir el port 8443. 
     * Qualsevol consulta al port 8080 es redireccionara al port 8443.
     */
  private Connector httpToHttpsRedirectConnector() {
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setScheme("http");
    connector.setPort(8081);
    connector.setSecure(false);
    connector.setRedirectPort(8443);
    return connector;
  }
}

}
