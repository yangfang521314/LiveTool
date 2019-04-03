package com.young.minor.livetool.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Window;
import com.young.minor.livetool.R;


public class LoadingDialog extends Dialog {


    public LoadingDialog(Context context, int theme) {
        super(context,theme);
        initView();
    }

    private void initView() {
        setContentView(R.layout.custom_toast);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        Window window = this.getWindow();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (LoadingDialog.this.isShowing()) {
                    LoadingDialog.this.dismiss();
                }
                break;
        }
        return true;
    }
}
