package com.example.mazerunner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CommunicationListAdapter extends ArrayAdapter<String> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> textViewMessage;
    private int  mViewResourceId;

    public CommunicationListAdapter(Context context, int tvResourceId, ArrayList<String> messages){
        super(context, tvResourceId,messages);
        this.textViewMessage = messages;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        String message = textViewMessage.get(position);

        if (message != null) {
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);

            if (textViewMessage != null) {
                Log.d("CommunicationLstAdapter", message);
                textViewMessage.setText(message);
            }

        }

        return convertView;
    }

}