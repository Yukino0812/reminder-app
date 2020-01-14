package me.yukino.reminderapp.record.api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author Yukino Yukinoshita
 */

public interface RecordApi {

    @POST("insert.php")
    @FormUrlEncoded
    Observable<String> addRecord(@Header("Cookie") String cookie,
                                 @Field("subject") String subject,
                                 @Field("details") String details,
                                 @Field("time") String time);

    @POST("del.php")
    @FormUrlEncoded
    Observable<String> deleteRecord(@Header("Cookie") String cookie,
                                 @Field("del") String time);

}
