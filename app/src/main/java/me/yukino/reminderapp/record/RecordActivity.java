package me.yukino.reminderapp.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tencent.mmkv.MMKV;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yukino.reminderapp.R;
import me.yukino.reminderapp.record.adapter.RecordAdapter;
import me.yukino.reminderapp.record.vo.RecordVO;
import me.yukino.reminderapp.user.LoginActivity;
import me.yukino.reminderapp.util.MMKVKeyEnum;
import me.yukino.reminderapp.vo.ResponseResult;

/**
 * @author Yukino Yukinoshita
 */

public class RecordActivity extends AppCompatActivity {

    private RecordPresenter recordPresenter;
    private Unbinder unbinder;

    @BindView(R.id.recyclerViewRecord)
    RecyclerView recyclerViewRecord;
    private RecordAdapter recordAdapter;

    private OnDeleteRecordListener onDeleteRecordListener;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_record);

        this.recordPresenter = new RecordPresenter();
        this.unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecyclerView();
    }

    private void initRecyclerView() {
        this.recordAdapter = new RecordAdapter();
        recordAdapter.setRecordOnClickListener(new RecordAdapter.RecordOnClickListener() {
            @Override
            public void onClickRecord(RecordVO vo) {
                new MaterialAlertDialogBuilder(RecordActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_App)
                        .setTitle(vo.getSubject())
                        .setMessage(vo.getDetails())
                        .show();
            }

            @Override
            public void onClickDeleteRecord(RecordVO vo) {
                if (onDeleteButtonClickListener != null) {
                    onDeleteButtonClickListener.onClick(vo);
                }
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerViewRecord.setLayoutManager(manager);
        recyclerViewRecord.setAdapter(recordAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.compositeDisposable = new CompositeDisposable();
        subscribeListRecord();
        subscribeDelete();
    }

    public Observable<String> observeGetRecord() {
        return Observable.create((ObservableOnSubscribe<String>) emitter ->
                onDeleteRecordListener = () -> emitter.onNext(MMKV.defaultMMKV().decodeString(MMKVKeyEnum.USERNAME)))
                .startWith(MMKV.defaultMMKV().decodeString(MMKVKeyEnum.USERNAME))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public void subscribeListRecord() {
        Disposable disposable = recordPresenter.getRecord(observeGetRecord())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(result -> {
                    if (result.getCode().equals(0)) {
                        return true;
                    }
                    Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    return false;
                })
                .map(ResponseResult::getData)
                .subscribe(recordAdapter::refreshList,
                        error -> Log.e("List record", error.getMessage(), error));
        compositeDisposable.add(disposable);
    }

    public Observable<RecordVO> observeDeleteButton() {
        return Observable.create((ObservableOnSubscribe<RecordVO>) emitter -> onDeleteButtonClickListener = emitter::onNext)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public void subscribeDelete() {
        Disposable disposable = recordPresenter.deleteRecord(observeDeleteButton())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.getCode().equals(0)) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        onDeleteRecordListener.onDelete();
                    } else if (result.getCode().equals(-1)) {
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        toLoginView();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Log.e("Delete record", error.getMessage(), error));
        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.ivBack)
    public void toLoginView() {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(MMKVKeyEnum.USERNAME, "");
        mmkv.encode(MMKVKeyEnum.PASSWORD, "");
        mmkv.encode(MMKVKeyEnum.COOKIE, "");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.ivAddRecord)
    public void toAddRecord() {
        Intent intent = new Intent(this, CreateRecordActivity.class);
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

    interface OnDeleteRecordListener {
        void onDelete();
    }

    interface OnDeleteButtonClickListener {
        void onClick(RecordVO vo);
    }

}
