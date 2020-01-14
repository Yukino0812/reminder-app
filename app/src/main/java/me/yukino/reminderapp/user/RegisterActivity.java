package me.yukino.reminderapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import me.yukino.reminderapp.user.vo.RegisterVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;

/**
 * @author Yukino Yukinoshita
 */

public class RegisterActivity extends AppCompatActivity {

    private RegisterPresenter registerPresenter;
    private Unbinder unbinder;

    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etMail)
    EditText etMail;
    @BindView(R.id.etInviteCode)
    EditText etInviteCode;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.registerPresenter = new RegisterPresenter();
        this.unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.compositeDisposable = new CompositeDisposable();
        subscribeRegister();
    }

    public Observable<RegisterVO> observeRegister() {
        return RxView.clicks(btnRegister)
                .map(unit -> {
                    RegisterVO vo = new RegisterVO();
                    vo.setUsername(etUsername.getText().toString());
                    vo.setPassword(etPassword.getText().toString());
                    vo.setMail(etMail.getText().toString());
                    vo.setCode(etInviteCode.getText().toString());
                    return vo;
                })
                .filter(vo -> {
                    if (StrUtil.isBlank(vo.getUsername().trim())) {
                        Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (StrUtil.isBlank(vo.getPassword().trim())) {
                        Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (StrUtil.isBlank(vo.getMail().trim())) {
                        Toast.makeText(getApplicationContext(), "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (StrUtil.isBlank(vo.getCode().trim())) {
                        Toast.makeText(getApplicationContext(), "邀请码不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public void subscribeRegister() {
        Disposable disposable = registerPresenter.register(observeRegister())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(result -> {
                    if (!result.getCode().equals(0)) {
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                })
                .subscribe(result -> toRecordView(),
                        error -> Log.e("Register", error.getMessage(), error));
        compositeDisposable.add(disposable);
    }

    public void toRecordView() {
        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.btnLogin)
    public void toLoginView() {
        this.finish();
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
