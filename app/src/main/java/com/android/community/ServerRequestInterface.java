package com.android.community;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by adamc on 1/21/17.
 */

public interface ServerRequestInterface {

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<ServerResponse> apiPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<ServerResponse> postToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type
    );

    //This method is used for "GET"
    @GET("/api/")
    Call<ServerResponse> apiGet(
            @Query("method") String method,
            @Query("username") String username,
            @Query("password") String password
    );

}