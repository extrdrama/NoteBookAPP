package com.example.tetris.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 在接收到广播时执行 finish 操作
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
