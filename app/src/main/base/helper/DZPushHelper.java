/**
  * Generated by smali2java 1.0.0.558
  * Copyright (C) 2013 Hensence.com
  */

package com.mobcent.discuz.base.helper;

import com.mobcent.discuz.android.db.constant.SharedPreferencesDBConstant;
import android.content.Context;
import com.mobcent.discuz.android.db.SharedPreferencesDB;
import android.text.TextUtils;
import com.mobcent.discuz.base.task.PushBindTask;
import com.mobcent.discuz.base.task.BaseRequestCallback;

public class DZPushHelper implements SharedPreferencesDBConstant {
    
    public static void bindPushToAnMiServer(Context context, boolean foces) {
        SharedPreferencesDB sdb = SharedPreferencesDB.getInstance(context);
        if(TextUtils.isEmpty(sdb.getString("channelId"))) {
            return;
        }
        String chanelId = sdb.getString("channelId");
        boolean isBinded = sdb.getBoolean("isPushBind");
        if((foces) || (!isBinded)) {
            (context, new BaseRequestCallback(sdb) {
                
                public void onPostExecute(BaseResult result) {
                    if((result != null) && (result.getRs() == 0x1)) {
                        sdb.setBoolean("isPushBind", true);
                    }
                }
                
                public void onPreExecute() {
                }
            }, sdb.getUserId(), chanelId).execute(new Void[0x0]);
        }
    }
}