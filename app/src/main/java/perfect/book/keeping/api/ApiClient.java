package perfect.book.keeping.api;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //public static final String BASE_URL = "https://api.rentmycargovan.com/";
    public static final String BASE_URL = "https://api.perfectbookkeeping.com/";
   // public static final String BASE_URL = "http://16.171.174.101:3000/";

    public static final String INVOICE_BASE_URL = "https://perfectbookkeeping.com/payment-pdf/";

    private final String key = "a93e82d9be0d860cdde873826fa8f37d9b83092f8fb04e138fbca1dcd0131947";

    public static Retrofit bookKeepingRetrofit;
    private static ApiClient mInstance;
    private ApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okhttp = new OkHttpClient.Builder();
        okhttp.addInterceptor(loggingInterceptor);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(getHttpLoggingInterceptor())
                .build();

        //Book Keeping API
        bookKeepingRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(new JSONObjectWithoutNamespacesConverterFactory())
                .build();

    }

    public static synchronized ApiClient getInstance(){
        if(mInstance == null) {
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public Api getBookKeepingApi(){
        return bookKeepingRetrofit.create(Api.class);
    }


    public String getKey() {return  key; }


    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        if(BuildConfig.DEBUG){
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        } else {
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//        }
        return httpLoggingInterceptor;
    }

    public class JSONObjectWithoutNamespacesConverterFactory extends Converter.Factory {
        @Override
        public @Nullable Converter<?, RequestBody> requestBodyConverter(
                Type type,
                Annotation[] parameterAnnotations,
                Annotation[] methodAnnotations,
                Retrofit retrofit) {
            if (type == JSONObject.class) {
                return new JSONObjectWithoutNamespacesRequestBodyConverter();
            }
            return null;
        }

        private static class JSONObjectWithoutNamespacesRequestBodyConverter implements Converter<JSONObject, RequestBody> {
            @Override
            public RequestBody convert(JSONObject value) throws IOException {
                MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
                String jsonString = removeNamespaces(value.toString());
                return RequestBody.create(jsonString, mediaType);
            }

            private String removeNamespaces(String jsonString) {
                // Replace all occurrences of "namespaceValues" with an empty string
                return jsonString.replaceAll("\"namespaceValues\":\\{[^}]*\\},?", "");
            }
        }
    }

}
