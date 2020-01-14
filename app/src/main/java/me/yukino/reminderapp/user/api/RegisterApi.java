package me.yukino.reminderapp.user.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author Yukino Yukinoshita
 */

public interface RegisterApi {

    @POST("register.php")
    @FormUrlEncoded
    Call<String> register(@Field("username") String username,
                          @Field("password") String password,
                          @Field("email") String email,
                          @Field("ico") String code);

}
