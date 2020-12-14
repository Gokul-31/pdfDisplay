package com.example.dum;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceLogin {

    @GET("api/police/downloads/{cId}")
    Call<ResponseBody> DownloadPetitionFile(@HeaderMap Map<String, String> jwtToken, @Path("cId") String cId, @Query("filename") String filename);
//    @GET("api/police/downloads/5f991a1a5ca64b50b1ee320c?filename=d1e4d1b2-4ee2-47a5-af6f-269cf50ec78d.pdf")
//    Call<ResponseBody> DownloadPetitionFile(@HeaderMap Map<String, String> jwtToken);

}

