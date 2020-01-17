package me.yukino.reminderapp.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.hutool.core.util.StrUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yukino.reminderapp.BuildConfig;
import me.yukino.reminderapp.R;
import me.yukino.reminderapp.record.RecordActivity;
import me.yukino.reminderapp.user.LoginActivity;
import me.yukino.reminderapp.user.LoginPresenter;
import me.yukino.reminderapp.user.vo.LoginVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;

/**
 * @author Yukino Yukinoshita
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvVersion)
    TextView tvVersion;
    private Unbinder unbinder;
    private LoginPresenter loginPresenter;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_main);
        this.unbinder = ButterKnife.bind(this);
        loginPresenter = new LoginPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvVersion.setText(BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")");
        this.compositeDisposable = new CompositeDisposable();
        if (!haveCache()) {
            toLoginView();
            return;
        }
        subscribeLogin();
    }

    public boolean haveCache() {
        MMKV mmkv = MMKV.defaultMMKV();
        if (!mmkv.containsKey(MMKVKeyEnum.USERNAME) || !mmkv.containsKey(MMKVKeyEnum.PASSWORD)) {
            return false;
        }
        if (StrUtil.isBlank(mmkv.decodeString(MMKVKeyEnum.USERNAME)) || StrUtil.isBlank(mmkv.decodeString(MMKVKeyEnum.PASSWORD))) {
            return false;
        }
        return true;
    }

    public void subscribeLogin() {
        Observable<LoginVO> observableLoginVO = Observable.just(
                new LoginVO(MMKV.defaultMMKV().decodeString(MMKVKeyEnum.USERNAME),
                        MMKV.defaultMMKV().decodeString(MMKVKeyEnum.PASSWORD)))
                .subscribeOn(AndroidSchedulers.mainThread());
        Disposable disposable = loginPresenter.login(observableLoginVO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.getCode().equals(0)) {
                        toRecordView();
                    } else {
                        toLoginView();
                    }
                }, error -> Log.e("MainActivity#login", error.getMessage(), error));
        compositeDisposable.add(disposable);
    }

    public void toRecordView() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void toLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
