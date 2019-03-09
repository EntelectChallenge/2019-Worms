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
    private CloudBlobContainer container;

    public AzureBlobStorageService(String connectionString, String container) throws Exception {

        serviceClient = CloudStorageAccount.parse(connectionString)
                .createCloudBlobClient();

        setContainer(container);
    }

    public File getFile(String file, String outputFile) throws Exception {

        LOGGER.info(String.format("Downloading %s", file));
        File f = new File(outputFile);

        File parent = f.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        CloudBlockBlob blob = container.getBlockBlobReference(file);

        blob.downloadToFile(f.getCanonicalPath());

        return f;
    }

    public void putFile(File file, String outputLocation) throws Exception {

        LOGGER.info(String.format("Uploading %s", file));
        CloudBlockBlob blob = container.getBlockBlobReference(String.format("%s/%s", outputLocation, file.getName()));

        FileInputStream fileInputStream = new FileInputStream(file);
        blob.upload(fileInputStream, file.length());

        fileInputStream.close();
    }

    private void setContainer(String containerName) throws URISyntaxException, StorageException {
        container = serviceClient.getContainerReference(containerName);
    }
}
