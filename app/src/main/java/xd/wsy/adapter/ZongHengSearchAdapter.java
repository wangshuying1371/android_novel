package xd.wsy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import xd.wsy.R;
import xd.wsy.constants.ZongHengBookDetail;

public class ZongHengSearchAdapter extends BaseAdapter {

    List<ZongHengBookDetail> books;
    Context mContext;

    class ViewHolder {
        TextView name;
        TextView author;
    }

    public ZongHengSearchAdapter(List<ZongHengBookDetail> books, Activity c) {
        this.books = books;
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.activity_search_item, null);
            holder.name = convertView.findViewById(R.id.tv_searchName);
            holder.author = convertView.findViewById(R.id.tv_searchAuthor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ZongHengBookDetail book = books.get(position);
        holder.name.setText(book.name);
        holder.author.setText(book.author);
        return convertView;
    }
}
