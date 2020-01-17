package me.yukino.reminderapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.jakewharton.rxbinding3.view.RxView;
import com.tencent.mmkv.MMKV;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.hutool.core.util.StrUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yukino.reminderapp.R;
import me.yukino.reminderapp.record.RecordActivity;
import me.yukino.reminderapp.user.vo.LoginVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;

/**
 * @author Yukino Yukinoshita
 */

public class LoginActivity extends AppCompatActivity {

    private LoginPresenter loginPresenter;
    private Unbinder unbinder;

    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLogin)
    MaterialButton btnLogin;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginPresenter = new LoginPresenter();
        this.unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.compositeDisposable = new CompositeDisposable();
        subscribeLogin();
    }

    public Observable<LoginVO> observeRememberUser(){
        return Observable.just(1)
                .filter(unit -> {
                    MMKV mmkv = MMKV.defaultMMKV();
                    if (!mmkv.containsKey(MMKVKeyEnum.USERNAME) || StrUtil.isBlank(mmkv.decodeString(MMKVKeyEnum.USERNAME))){
                        return false;
                    }
                    if (!mmkv.containsKey(MMKVKeyEnum.PASSWORD) || StrUtil.isBlank(mmkv.decodeString(MMKVKeyEnum.PASSWORD))){
                        return false;
                    }
                    return true;
                })
                .map(unit -> {
                    MMKV mmkv = MMKV.defaultMMKV();
                    LoginVO vo = new LoginVO();
                    vo.setUsername(mmkv.decodeString(MMKVKeyEnum.USERNAME));
                    vo.setPassword(mmkv.decodeString(MMKVKeyEnum.PASSWORD));
                    return vo;
                });
    }

    public Observable<LoginVO> observeLoginButton(){
        return RxView.clicks(btnLogin)
                .map(unit -> {
                    LoginVO vo = new LoginVO();
                    vo.setUsername(etUsername.getText().toString());
                    vo.setPassword(etPassword.getText().toString());
                    return vo;
                })
                .filter(vo -> {
                    if (StrUtil.isBlank(vo.getUsername().trim())){
                        Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (StrUtil.isBlank(vo.getPassword().trim())){
                        Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LoginVO> observeLogin(){
        return observeLoginButton().mergeWith(observeRememberUser());
    }

    public void subscribeLogin(){
        Disposable disposable = loginPresenter.login(observeLogin())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(result -> {
                    if (!result.getCode().equals(0)){
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                })
                .subscribe(result -> toRecordView(),
                        error -> Log.e("login", error.getMessage(), error));
        compositeDisposable.add(disposable);
    }

    public void toRecordView(){
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnRegister)
    public void toRegisterView(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
