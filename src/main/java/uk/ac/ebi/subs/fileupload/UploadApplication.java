package uk.ac.ebi.subs.fileupload;

import java.io.File;

/**
 * Prototype application to upload a file into a server using tus.io.
 */
public class UploadApplication {

    public static void main(String[] args) throws Exception {
        UploadClient uploadClient = new UploadClient();

        File file = new File("src/main/resources/central_park1.jpg");

        uploadClient.setFile(file);
        uploadClient.attemptUpload();
    }
}
