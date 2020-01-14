package me.yukino.reminderapp.user;

import android.util.Log;

import com.tencent.mmkv.MMKV;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.yukino.reminderapp.user.vo.RegisterVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;
import me.yukino.reminderapp.vo.ResponseResult;
import retrofit2.Response;

/**
 * @author Yukino Yukinoshita
 */

public class RegisterPresenter {

    private RegisterService registerService;

    public RegisterPresenter(){
        this.registerService = new RegisterService();
    }

    public Observable<ResponseResult> register(Observable<RegisterVO> registerObservable){
        return registerObservable.observeOn(Schedulers.io())
                .doOnNext(vo -> {
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(MMKVKeyEnum.USERNAME, vo.getUsername());
                    mmkv.encode(MMKVKeyEnum.PASSWORD, vo.getPassword());
                })
                .map(registerService::register)
                .doOnNext(response -> {
                    String sessionId = response.headers().get("Set-Cookie");
                    MMKV.defaultMMKV().encode(MMKVKeyEnum.COOKIE, sessionId);
                })
                .map(Response::body)
                .map(responseStr -> {
                    if (responseStr.contains("login.html")){
                        return new ResponseResult(0);
                    }
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(MMKVKeyEnum.USERNAME, "");
                    mmkv.encode(MMKVKeyEnum.PASSWORD, "");

                    String msg = responseStr.replaceAll("[\\w\\W]*alert\\(\"(.*)\"\\);[\\w\\W]*","$1");
                    Log.i("RegisterPresenter#register", msg);
                    return new ResponseResult(-1, msg);
                });
    }

}
