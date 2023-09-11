package ch.heia.isc.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class PiBackendApplication {

    public static final String BASE_PATH = "/api/v1";

    public static void main(String[] args) {
        // Start the application
        SpringApplication.run(PiBackendApplication.class, args);
        System.out.println("Server started with version");
    }
}
