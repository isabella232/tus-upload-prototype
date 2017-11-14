package uk.ac.ebi.subs.fileupload;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * Prototype application to upload a file into a server using tus.io.
 */
@SpringBootApplication
public class UploadApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UploadApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UploadClient uploadClient = new UploadClient();

        File file = new File("src/main/resources/central_park2.jpg");

        uploadClient.setFile(file);
        uploadClient.attemptUpload();
    }
}
