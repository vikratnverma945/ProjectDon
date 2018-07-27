package com.google.android.cameraview.demo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdpter2 extends ArrayAdapter {

    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public GridViewAdpter2(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder2 holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder2();
            holder.image = (ImageView) row.findViewById(R.id.image2);
            row.setTag(holder);
        } else {
            holder = (ViewHolder2) row.getTag();
        }

        ImageItem2 item = (ImageItem2) data.get(position);
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    static class ViewHolder2 {
        ImageView image;
    }


}
