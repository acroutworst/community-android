package com.android.community.authentication;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by adamc on 1/21/17.
 */

public interface ServerRequestInterface {

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postClientToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postUserToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postGoogleAuthToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("id_token") String id_token
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postGoogleUserToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("username") String username,
            @Field("email") String email,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postRefreshToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token
    );

    @FormUrlEncoded
    @POST("/o/revoke_token/")
    Call<Void> postRevokeToken(
            @Field("token") String token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<Account> apiPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );
}