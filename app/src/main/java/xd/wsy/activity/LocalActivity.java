package xd.wsy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.List;

import xd.wsy.R;
import xd.wsy.constants.Record;
import xd.wsy.utils.LocalUtils;

public class LocalActivity extends AppCompatActivity {

    GridView GvLocal;
    List<Record> list;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        GvLocal = findViewById(R.id.gv_local);
        list = LocalUtils.getBooks(this);
//        Log.i("TAG_list_local", String.valueOf(list));


        final LocalAdapter localAdapter = new LocalAdapter(this);
        GvLocal.setAdapter(localAdapter);

        final Button BtnOpen = this.findViewById(R.id.btn_open);
        View view = getLayoutInflater().inflate(R.layout.pop_folder, null);
        final PopupWindow popupWindow = new PopupWindow(view, 350, 150);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        BtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(BtnOpen, Gravity.TOP, 500, 200);
            }
        });

        Button BtnOpenFolder = view.findViewById(R.id.btn_openFolder);
        BtnOpenFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalActivity.this, FolderActivity.class);
                startActivity(intent);
            }
        });

//        localAdapter.notifyDataSetChanged();
//        GvLocal.notify();

        // 在onCreate()中开启线程
//        new Thread(new GameThread()).start();

    }
    // 实例化一个handler
   /* @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        // 接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocalActivity.REFRESH:
                    GvLocal.invalidate(); // 刷新界面
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class GameThread implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = LocalActivity.REFRESH;
                // 发送消息
                myHandler.sendMessage(message);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }*/


   /* class GameThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            GvLocal.postInvalidate();
        }
    }*/

    class LocalAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;
        private Activity mContext;

        LocalAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            this.mContext = (Activity) context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            ImageView image;
        }

        @SuppressLint("LongLogTag")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.activity_local_item,null);
                holder.image = convertView.findViewById(R.id.iv_local2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Record info = list.get(position);
//            Log.i("TAG_info_local", String.valueOf(info));

            holder.image.setImageBitmap(BitmapFactory.decodeFile(info.book_picture_src));

            /*LocalActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.image.setImageBitmap(BitmapFactory.decodeFile(info.book_picture_src));

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    GvLocal.postInvalidate();
                }
            });*/


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ReadActivity.class);
                    intent.putExtra("bookPath", info.book_path);
                    mContext.startActivity(intent);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(mContext).setTitle("删除").setMessage("是否删除?").setIcon(R.drawable.local_delete)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LocalUtils.delete(info, mContext);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton("否", null).show();
                    return false;
                }
            });
            return convertView;
        }

    }

}
