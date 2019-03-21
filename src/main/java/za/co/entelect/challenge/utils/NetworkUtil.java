package za.co.entelect.challenge.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

public class NetworkUtil {

    private static final Logger LOGGER = LogManager.getLogger(NetworkUtil.class);
    private static final int MAX_RETRIES = 3;

    public static <T> Response<T> executeWithRetry(Call<T> call) throws Exception {

        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {

                Thread.sleep(retryCount * 3000);
                Response<T> execute = call.execute();
                if (!execute.isSuccessful()) {
                    throw new Exception("Request failed");
                }

                return execute;
            } catch (Exception e) {
                LOGGER.info("Failed call: " + call.request().toString());
                if (retryCount < MAX_RETRIES - 1) {
                    LOGGER.info("Retrying request");
                }
            }

            retryCount++;
        }

        throw new Exception("Request failed");
    }
}
