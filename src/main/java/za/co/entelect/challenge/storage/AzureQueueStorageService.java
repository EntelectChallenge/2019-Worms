package za.co.entelect.challenge.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class AzureQueueStorageService {

    private CloudQueueClient serviceClient;
    private CloudQueue queue;

    public AzureQueueStorageService(String connectionString, String queueName) throws Exception {

        serviceClient = CloudStorageAccount.parse(connectionString)
                .createCloudQueueClient();

        queue = serviceClient.getQueueReference(queueName);
    }

    public void enqueueMessage(String message) throws StorageException {
        queue.addMessage(new CloudQueueMessage(message));
    }
}
