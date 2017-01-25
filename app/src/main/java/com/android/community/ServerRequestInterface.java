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

    @FormUrlEncoded
    @POST("/o/token/")
    Call<ServerResponse> postClientToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<ServerResponse> postUserToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<ServerResponse> postRefreshToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token
    );

    @FormUrlEncoded
    @POST("/o/revoke-token/")
    Call<ServerResponse> postRevokeToken(
            @Field("token") String token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<ServerResponse> apiPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );
}