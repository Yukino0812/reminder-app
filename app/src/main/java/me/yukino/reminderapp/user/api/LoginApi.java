package me.yukino.reminderapp.user.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author Yukino Yukinoshita
 */

public interface LoginApi {

    @POST("login.php")
    @FormUrlEncoded
    Call<String> login(@Field("username") String username, @Field("password") String password);

}
