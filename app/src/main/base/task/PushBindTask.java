/**
  * Generated by smali2java 1.0.0.558
  * Copyright (C) 2013 Hensence.com
  */

package com.mobcent.discuz.base.task;

import com.mobcent.discuz.android.service.PayStateService;
import android.content.Context;
import com.mobcent.discuz.android.model.BaseResult;
import com.mobcent.discuz.android.service.impl.PayStateServiceImpl;

public class PushBindTask extends BaseTask {
    private String chanelId;
    private PayStateService payStateService;
    private long userId;
    
    public PushBindTask(Context context, BaseRequestCallback<BaseResult> _callback, long userId, String chanelId) {
        super(context, _callback);
        payStateService = new PayStateServiceImpl(context);
        userId = userId;
        chanelId = chanelId;
    }
    
    protected void onPreExecute() {
        super.onPreExecute();
    }
    
    protected BaseResult doInBackground(Void[] params) {
        return payStateService.pushBindUser(userId, chanelId);
    }
}