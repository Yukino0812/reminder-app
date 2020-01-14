package me.yukino.reminderapp.record;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.tencent.mmkv.MMKV;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yukino.reminderapp.R;
import me.yukino.reminderapp.record.vo.AddRecordVO;
import me.yukino.reminderapp.user.LoginActivity;
import me.yukino.reminderapp.util.MMKVKeyEnum;
import me.yukino.reminderapp.widget.CustomDatePicker;

/**
 * @author Yukino Yukinoshita
 */

public class CreateRecordActivity extends AppCompatActivity {

    private RecordPresenter recordPresenter;
    private Unbinder unbinder;

    @BindView(R.id.etSubject)
    EditText etSubject;
    @BindView(R.id.etDetail)
    EditText etDetail;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.ivCheck)
    ImageView ivCheck;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        this.recordPresenter = new RecordPresenter();
        this.unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDatePicker();
    }

    public void initDatePicker() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = DateUtil.now();
        tvTime.setText(now.substring(0, now.length()-3));

        Date dateAfterHalfYear = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateAfterHalfYear);
        calendar.add(Calendar.DATE, 180);
        dateAfterHalfYear.setTime(calendar.getTime().getTime());
        String dateAfterHalfYearStr = simpleDateFormat.format(dateAfterHalfYear);

        final CustomDatePicker customDatePicker = new CustomDatePicker(this, tvTime::setText, now, dateAfterHalfYearStr);

        tvTime.setOnClickListener(v -> customDatePicker.show(tvTime.getText().toString()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.compositeDisposable = new CompositeDisposable();
        subscribeAddRecord();
    }

    public Observable<AddRecordVO> observeAddRecord() {
        return RxView.clicks(ivCheck)
                .map(unit -> {
                    String subject = etSubject.getText().toString();
                    String detail = etDetail.getText().toString();
                    String time = tvTime.getText().toString(); //
                    time = DateUtil.format(DateUtil.parse(time), "yyyyMMddHHmm00");
                    return new AddRecordVO(subject, detail, time);
                })
                .filter(vo -> {
                    if (StrUtil.isBlank(vo.getSubject().trim())) {
                        Toast.makeText(getApplicationContext(), "主题不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (StrUtil.isBlank(vo.getDetails().trim())) {
                        Toast.makeText(getApplicationContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public void subscribeAddRecord() {
        Disposable disposable = recordPresenter.addRecord(observeAddRecord())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.getCode().equals(0)) {
                        Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        toRecordView();
                    } else if (result.getCode().equals(-1)) {
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        toLoginView();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        toRecordView();
                    }
                });
        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.ivBack)
    public void toRecordView() {
        this.finish();
    }

    public void toLoginView() {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(MMKVKeyEnum.USERNAME, "");
        mmkv.encode(MMKVKeyEnum.PASSWORD, "");
        mmkv.encode(MMKVKeyEnum.COOKIE, "");
        Intent intent = new Intent(this, LoginActivity.class);
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
