package com.inguana.vocabularypractice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JsonGetter {

    private static Retrofit retrofit;
    //private static final String BASE_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/";
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    //create logger
    private static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    //create OkHttp Client
    private static OkHttpClient.Builder okHttp = new OkHttpClient.Builder().addInterceptor(logger);

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttp.build())
                    .build();
        }
        return retrofit;
    }

    public static <S> S buildService(Class<S> serviceType) {
        return retrofit.create(serviceType);
    }

    /*public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GerritAPI gerritAPI = retrofit.create(GerritAPI.class);

        Call<List<Change>> call = gerritAPI.loadChanges("status:open");
        call.enqueue(this);

    }*/

    /*@Override
    public void onResponse(Call<List<Change>> call, Response<List<Change>> response) {
        if(response.isSuccessful()) {
            List<Change> changesList = response.body();
            changesList.forEach(change -> System.out.println(change.subject));
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Change>> call, Throwable t) {
        t.printStackTrace();
    }*/
}
