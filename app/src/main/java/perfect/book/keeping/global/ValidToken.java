package perfect.book.keeping.global;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.response.ValidTokenResponse;
import retrofit2.Call;
import retrofit2.Response;

public class ValidToken {
    private ExecutorService executorService;

    String result;

    public ValidToken() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public String performAsyncTask(String token, String location) {
        // Submit a task to the executor service
        Future<String> future = executorService.submit(new ValidToken.validToken(token));

        // Perform other operations or wait for the result
        // ...

        try {
            // Wait for the task to complete and get the result
            result = future.get();

            // Do something with the result

        } catch (Exception e) {
            // Handle any exceptions that occurred during task execution
            e.printStackTrace();
        }


        return result;
    }

    private class validToken implements Callable<String> {
        String token;
        public validToken(String token) {
            this.token = token;
        }

        @Override
        public String call() throws Exception {
            Call<ValidTokenResponse> validTokenCall = ApiClient.getInstance().getBookKeepingApi().checkValidToken("Bearer "+token);
            Response<ValidTokenResponse> responseCheck = validTokenCall.execute();
            Log.e("REPONSE VALID TOKEN",""+responseCheck.code());
            if (responseCheck.isSuccessful()) {
                return token;
            } else {
                try {
                    JSONObject jObjError = new JSONObject(responseCheck.errorBody().string());
                    Log.e("Response Code Valid Token Message",""+jObjError.getString("message"));
                    return jObjError.getString("message");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
