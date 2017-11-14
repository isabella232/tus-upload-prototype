package uk.ac.ebi.subs.fileupload;

import io.tus.java.client.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is an implementation of the {@link TusExecutor} abstract class.
 */
public class UploadExecutor extends TusExecutor {

    private static final Logger LOGGER = Logger.getLogger( UploadExecutor.class.getName() );

    private TusClient uploadClient;
    private TusUpload upload;

    public UploadExecutor(TusClient uploadClient, TusUpload upload) {
        this.uploadClient = uploadClient;
        this.upload = upload;
    }

    @Override
    protected void makeAttempt() throws ProtocolException, IOException {
        LOGGER.info("Starting upload...");

        TusUploader uploader = uploadClient.resumeOrCreateUpload(upload);
        uploader.setChunkSize(1024);

        do {
            // Calculate the progress using the total size of the uploading file and
            // the current offset.
            long totalBytes = upload.getSize();
            long bytesUploaded = uploader.getOffset();
            double progress = (double) bytesUploaded / totalBytes * 100;

            System.out.printf("Upload at %06.2f%%.\n", progress);
        } while(uploader.uploadChunk() > -1);

        uploader.finish();

        LOGGER.info("Upload finished.");
        LOGGER.info(String.format("Upload available at: %s", uploader.getUploadURL().toString()));
    }
}
