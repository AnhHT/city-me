package com.cityme.asia.task;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cityme.asia.R;
import com.cityme.asia.helper.CustomContract;

/**
 * Created by AnhHoang on 3/10/2016.
 */
public class SuggestionAdapter extends CursorAdapter {
    public SuggestionAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.suggestion_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.nameView.setText(cursor.getString(CustomContract.SuggestionEntry.COL_NAME));
        holder.addressView.setText(cursor.getString(CustomContract.SuggestionEntry.COL_ADDRESS));
        Log.d("SuggestionAdapter", String.format("%s - %s - %s - %s", cursor.getString(0),
                holder.nameView.getText().toString(), holder.addressView.getText().toString(),
                cursor.getString(CustomContract.SuggestionEntry.COL_SLUG)));
    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView addressView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.sName);
            addressView = (TextView) view.findViewById(R.id.sAddress);
        }
    }
}
