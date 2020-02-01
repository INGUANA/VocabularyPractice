package com.inguana.vocabularypractice;

import com.inguana.vocabularypractice.rest.response.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

// each maps to an endpoint in the web service
public interface APIInterface {



    /*@GET("changes/")
    Call<List<Change>> loadChanges(@Query("q") String status);*/

    /*@GET("translate")
    Call<BaseResponse> getWordTranslation(@Query("key") String key, @Query("text") String wordToTranslate, @Query("lang") String languageToTranslateTo);*/

    @GET("getLangs")
    Call<Object> getLangs(@Query("key") String key);

    @GET("words")
    Call<BaseResponse> getWordTranslation(@Query("keyword") String key);
}
