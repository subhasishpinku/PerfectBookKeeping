package perfect.book.keeping.global;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import perfect.book.keeping.activity.LoginScreen;
import perfect.book.keeping.api.ApiClient;
import perfect.book.keeping.response.RefreshTokenResponse;
import retrofit2.Call;
import retrofit2.Response;


public class RefreshToken {
    private ExecutorService executorService;

    String result;
    public RefreshToken() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public String performAsyncTask(String token, String location) {
        // Submit a task to the executor service
        Future<String> future = executorService.submit(new MyTask(token));

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

    private class MyTask implements Callable<String> {
        String token;
        public MyTask(String token) {
            this.token = token;
        }

        @Override
        public String call() throws Exception {
            Call<RefreshTokenResponse> call = ApiClient.getInstance().getBookKeepingApi().refreshToken(token);
            Response<RefreshTokenResponse> response = call.execute();
            Log.e("Response Code Refresh Token",""+response.code());

            if (response.isSuccessful()) {
                RefreshTokenResponse apiResponse = response.body();
                // Process the response
                Log.e("RESPONSE",""+apiResponse.getData().getAccessToken());
                return apiResponse.getData().getAccessToken();
            } else {
                // Handle the error response
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    Log.e("Response Code Refresh Token Message",""+jObjError.getString("message"));
                    return jObjError.getString("message");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //throw new Exception("API request failed with code: " + response.code());
            }
        }
    }
}

