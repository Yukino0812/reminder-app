package me.yukino.reminderapp.record;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.yukino.reminderapp.record.vo.AddRecordVO;
import me.yukino.reminderapp.record.vo.RecordVO;
import me.yukino.reminderapp.vo.ResponseResult;

/**
 * @author Yukino Yukinoshita
 */

public class RecordPresenter {

    private RecordService recordService;

    public RecordPresenter(){
        this.recordService = new RecordService();
    }

    public Observable<ResponseResult<List<RecordVO>>> getRecord(Observable<String> observable){
        return observable.observeOn(Schedulers.io())
                .flatMap(recordService::listRecord)
                .observeOn(Schedulers.computation())
                .map(resultJson -> {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode head = mapper.readTree(resultJson);
                    int code = head.get("code").asInt();
                    String msg = head.get("msg").asText();
                    if (code==0){
                        RecordVO[] vos = mapper.readValue(head.get("data").toString(), RecordVO[].class);
                        List<RecordVO> voList = Arrays.stream(vos).collect(Collectors.toList());
                        return new ResponseResult<>(code,msg,voList);
                    }else {
                        return new ResponseResult<>(code,msg);
                    }
                });
    }

    public Observable<ResponseResult> addRecord(Observable<AddRecordVO> addRecordObservable){
        return addRecordObservable.observeOn(Schedulers.io())
                .flatMap(recordService::addRecord)
                .observeOn(Schedulers.computation())
                .map(result -> {
                    if (result.contains("Permission denied")){
                        return new ResponseResult(-1, "身份信息已过期，请重新登录");
                    }
                    if (result.contains("添加成功")){
                        return new ResponseResult(0);
                    }
                    Log.e("RecordPresenter#addRecord","未分析的返回结果："+result);
                    return new ResponseResult(-2,"返回结果异常");
                });
    }

    public Observable<ResponseResult> deleteRecord(Observable<RecordVO> recordObservable){
        return recordObservable.observeOn(Schedulers.io())
                .flatMap(recordService::deleteRecord)
                .observeOn(Schedulers.computation())
                .map(result -> {
                    if (result.contains("Permission denied")){
                        return new ResponseResult(-1, "身份信息已过期，请重新登录");
                    }
                    if (result.contains("demo.php")){
                        return new ResponseResult(0);
                    }
                    Log.e("RecordPresenter#deleteRecord","未分析的返回结果："+result);
                    return new ResponseResult(-2,"返回结果异常");
                });
    }

}
