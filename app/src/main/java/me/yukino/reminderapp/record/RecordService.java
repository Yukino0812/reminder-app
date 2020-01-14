package me.yukino.reminderapp.record;

import android.util.Log;

import com.tencent.mmkv.MMKV;

import cn.hutool.core.date.DateUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.yukino.reminderapp.record.api.ListRecordApi;
import me.yukino.reminderapp.record.api.RecordApi;
import me.yukino.reminderapp.record.vo.AddRecordVO;
import me.yukino.reminderapp.record.vo.RecordVO;
import me.yukino.reminderapp.util.MMKVKeyEnum;
import me.yukino.reminderapp.util.RetrofitProvider;

/**
 * @author Yukino Yukinoshita
 */

public class RecordService {

    private final static String AUTH_KEY = "6462c93b1394700014f073dc95797f2399035075e9381509071a8b590023fe25";

    public Observable<String> listRecord(String name) {
        return RetrofitProvider.get().create(ListRecordApi.class)
                .listRecord(AUTH_KEY, name)
                .observeOn(Schedulers.io());
    }

    public Observable<String> addRecord(AddRecordVO vo){
        return RetrofitProvider.getContent().create(RecordApi.class)
                .addRecord(MMKV.defaultMMKV().decodeString(MMKVKeyEnum.COOKIE), vo.getSubject(), vo.getDetails(), vo.getTime())
                .observeOn(Schedulers.io());
    }

    public Observable<String> deleteRecord(RecordVO vo){
        return RetrofitProvider.getContent().create(RecordApi.class)
                .deleteRecord(MMKV.defaultMMKV().decodeString(MMKVKeyEnum.COOKIE), DateUtil.format(vo.getId(), "yyyy-MM-dd HH:mm:ss"))
                .observeOn(Schedulers.io());
    }

}
