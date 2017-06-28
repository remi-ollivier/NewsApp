package esgi.com.newsapp.network;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Grunt on 28/06/2017.
 */

public class RetrofitSession {

    private static String BASE_URL = "https://esgi-2017.herokuapp.com/";
    private static Gson gson;
    private Retrofit retrofit;
    private static final RetrofitSession INSTANCE = new RetrofitSession();
    private UserService userService;

    private static GsonBuilder getDefaultGsonBuilder() {
        GsonBuilder defaultGsonBuilder = new GsonBuilder();
        defaultGsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(Exclude.class) != null && f.getAnnotation(Exclude.class).serialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        defaultGsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(Exclude.class) != null && f.getAnnotation(Exclude.class).deserialize();
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        defaultGsonBuilder.setDateFormat("yyyy-MM-dd");
        return defaultGsonBuilder;
    }

    private static Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = getDefaultGsonBuilder().setLenient();
            gson = builder.create();
        }
        return gson;
    }

    private RetrofitSession() {
        this.initRetrofitClient();
    }

    public static RetrofitSession getInstance() {
        return INSTANCE;
    }

    private void initRetrofitClient() {
        // clean old retrofit client if exists
        if (this.retrofit != null) {
            this.retrofit = null;
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).build());

        //todo auth
        /*String authToken = PreferenceHelper.getInstance().getToken();
        if (authToken != null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.interceptors().add(new AuthTokenHeaderInterceptor());
            retrofitBuilder.client(okHttpClient);
        }*/

        this.retrofit = retrofitBuilder.build();
        this.userService = new UserService(this.retrofit);
    }

    public UserService getUserService() {
        return userService;
    }
}
