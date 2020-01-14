package me.yukino.reminderapp.user;

import android.util.Log;

import com.tencent.mmkv.MMKV;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.yukino.reminderapp.user.vo.LoginVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;
import me.yukino.reminderapp.vo.ResponseResult;
import retrofit2.Response;

/**
 * @author Yukino Yukinoshita
 */

public class LoginPresenter {

    private LoginService loginService;

    public LoginPresenter(){
        this.loginService = new LoginService();
    }

    public Observable<ResponseResult> login(Observable<LoginVO> loginObservable){
        return loginObservable.observeOn(Schedulers.io())
                .doOnNext(vo -> {
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(MMKVKeyEnum.USERNAME, vo.getUsername());
                    mmkv.encode(MMKVKeyEnum.PASSWORD, vo.getPassword());
                })
                .map(loginService::login)
                .doOnNext(response -> {
                    String sessionId = response.headers().get("Set-Cookie");
                    MMKV.defaultMMKV().encode(MMKVKeyEnum.COOKIE, sessionId);
                })
                .map(Response::body)
                .map(responseStr -> {
                    if (responseStr.contains("demo.php")){
                        return new ResponseResult(0);
                    }
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(MMKVKeyEnum.USERNAME, "");
                    mmkv.encode(MMKVKeyEnum.PASSWORD, "");

                    String msg = responseStr.replaceAll("[\\w\\W]*alert\\(\"(.*)\"\\);[\\w\\W]*","$1");
                    Log.i("LoginPresenter#login", msg);
                    return new ResponseResult(-1, msg);
                });
    }

}
