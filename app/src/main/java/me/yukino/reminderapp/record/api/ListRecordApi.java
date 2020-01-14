package me.yukino.reminderapp.record.api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author Yukino Yukinoshita
 */

public interface ListRecordApi {

    @POST("record/list")
    @FormUrlEncoded
    Observable<String> listRecord(@Header("auth_key") String authKey, @Field("name") String name);

}
