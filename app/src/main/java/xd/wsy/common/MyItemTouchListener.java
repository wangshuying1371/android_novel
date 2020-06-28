package xd.wsy.common;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import xd.wsy.R;

public class MyItemTouchListener implements View.OnTouchListener {
    Context mContext;

    public MyItemTouchListener(Context c) {
        this.mContext = c;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(mContext.getResources().getColor( R.color.gray_text));
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
        return false;
    }
}
