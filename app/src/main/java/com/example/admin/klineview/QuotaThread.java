package com.example.admin.klineview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import java.util.List;

/**
 * 子线程计算五项数据
 * Created by xiesuichao on 2018/8/18.
 */

public class QuotaThread extends HandlerThread implements Handler.Callback {

    public static final int HANDLER_QUOTA_LIST = 100;
    public static final int HANDLER_QUOTA_SINGLE = 101;
    private Handler uiHandler;
    private Handler workHandler;

    public QuotaThread(String name, int priority) {
        super(name, priority);
    }

    public void setUIHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public void quotaListCalculate(List<KData> dataList) {
        Message message = Message.obtain(null, HANDLER_QUOTA_LIST);
        message.obj = dataList;
        workHandler.sendMessage(message);
    }

    public void quotaSingleCalculate(List<KData> dataList){
        Message message = Message.obtain(null, HANDLER_QUOTA_SINGLE);
        message.obj = dataList;
        workHandler.sendMessage(message);
        PrintUtil.log("quotaSingleCalculate");
    }

    private void calculateKDataQuota(List<KData> dataList) {
        QuotaUtil.initEma(dataList);
        QuotaUtil.initBoll(dataList);
        QuotaUtil.initMACD(dataList);
        QuotaUtil.initKDJ(dataList);
        QuotaUtil.initMa(dataList);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        this.workHandler = new Handler(getLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == HANDLER_QUOTA_LIST) {
            handleData(msg, HANDLER_QUOTA_LIST);
        }else if (msg.what == HANDLER_QUOTA_SINGLE){
            handleData(msg, HANDLER_QUOTA_SINGLE);
        }
        return true;
    }

    private void handleData(Message msg, int whatId){
        List<KData> dataList = (List<KData>) msg.obj;
        calculateKDataQuota(dataList);
        Message message = Message.obtain(null, whatId);
        uiHandler.sendMessage(message);
    }

}
