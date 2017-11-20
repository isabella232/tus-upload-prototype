package uk.ac.ebi.subs.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Prototype application to upload a file into a server using tus.io.
 */
@SpringBootApplication
public class UploadApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UploadApplication.class, args);
    }
}
