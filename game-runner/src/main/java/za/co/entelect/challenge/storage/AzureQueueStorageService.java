package za.co.entelect.challenge.storage;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class AzureQueueStorageService {

    private CloudQueueClient serviceClient;
    private Gson gson;

    public AzureQueueStorageService(String connectionString) throws Exception {

        gson = new Gson();
        serviceClient = CloudStorageAccount.parse(connectionString)
                .createCloudQueueClient();
    }

    public void enqueueMessage(String queueName, Object message) throws Exception {
        CloudQueue queue = serviceClient.getQueueReference(queueName);
        queue.addMessage(new CloudQueueMessage(gson.toJson(message)));
    }
}
