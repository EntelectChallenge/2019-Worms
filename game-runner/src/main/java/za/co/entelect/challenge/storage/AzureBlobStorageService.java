package za.co.entelect.challenge.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;

public class AzureBlobStorageService {

    private static final Logger LOGGER = LogManager.getLogger(AzureBlobStorageService.class);

    private CloudBlobClient serviceClient;

    public AzureBlobStorageService(String connectionString) throws Exception {

        serviceClient = CloudStorageAccount.parse(connectionString)
                .createCloudBlobClient();
    }

    public File getFile(String file, String outputFile, String container) throws Exception {

        CloudBlobContainer bloBContainer = serviceClient.getContainerReference(container);

        LOGGER.info(String.format("Downloading %s", file));
        File f = new File(outputFile);

        File parent = f.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        CloudBlockBlob blob = bloBContainer.getBlockBlobReference(file);

        blob.downloadToFile(f.getCanonicalPath());

        return f;
    }

    public void putFile(File file, String outputLocation, String container) throws Exception {

        CloudBlobContainer bloBContainer = serviceClient.getContainerReference(container);

        LOGGER.info(String.format("Uploading %s", file));
        CloudBlockBlob blob = bloBContainer.getBlockBlobReference(outputLocation);

        FileInputStream fileInputStream = new FileInputStream(file);
        blob.upload(fileInputStream, file.length());

        fileInputStream.close();
    }
}
