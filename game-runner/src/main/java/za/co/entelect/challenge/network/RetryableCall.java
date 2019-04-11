package za.co.entelect.challenge.network;

import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class RetryableCall<T> implements Call<T> {

    private static final Logger LOGGER = LogManager.getLogger(RetryableCall.class);

    private Call<T> originalCall;
    private int maxRetries;
    private int retryTimeout;

    public RetryableCall(Call<T> originalCall, int maxRetries, int retryTimeout) {
        this.originalCall = originalCall;
        this.maxRetries = maxRetries;
        this.retryTimeout = retryTimeout;
    }

    @Override
    public Response<T> execute() throws IOException {

        String request = originalCall.request().toString();
        for (int i = 0; i < maxRetries; i++) {
            try {

                Thread.sleep(i * retryTimeout);
                Response<T> execute = originalCall.execute();
                if (!execute.isSuccessful()) {
                    throw new Exception(String.format("Request failed: %s", request));
                }

                return execute;
            } catch (Exception e) {
                LOGGER.error(String.format("Failed call: %s", request));
            }

            LOGGER.info(String.format("Retrying request: %s", request));
            originalCall = originalCall.clone();
        }

        throw new IOException(String.format("Request failed: %s", request));
    }

    @Override
    public void enqueue(Callback<T> callback) {
        originalCall.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return originalCall.isExecuted();
    }

    @Override
    public void cancel() {
        originalCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return originalCall.isCanceled();
    }

    @Override
    public Call<T> clone() {
        return originalCall.clone();
    }

    @Override
    public Request request() {
        return originalCall.request();
    }
}
