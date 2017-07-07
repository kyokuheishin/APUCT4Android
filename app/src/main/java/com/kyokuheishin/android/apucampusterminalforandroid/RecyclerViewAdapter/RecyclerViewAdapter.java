package com.kyokuheishin.android.apucampusterminalforandroid.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyokuheishin.android.apucampusterminalforandroid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qbx on 2017/7/7.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder> {
    private HashMap<String, ArrayList<String>> mHashMap;
    private Context mContext;

    public RecyclerViewAdapter(HashMap mHashmap,Context mContext){
        this.mHashMap = mHashmap;
        this.mContext = mContext;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{
        CardView mCardView;
        TextView mTitleTextView;
        TextView mSendingTimeTextView;
        TextView mReadingTimeTextView;
        TextView mSourceTextView;


        public MessageViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title_text);
            mSendingTimeTextView = (TextView)itemView.findViewById(R.id.date_sending_text);
            mReadingTimeTextView = (TextView)itemView.findViewById(R.id.date_reading_text);
            mSourceTextView = (TextView) itemView.findViewById(R.id.source_text);

        }
    }

    @Override
    public RecyclerViewAdapter.MessageViewHolder onCreateViewHolder(ViewGroup viewGroup,int i ){
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_card,viewGroup,false);
        MessageViewHolder nvh = new  MessageViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.mTitleTextView.setText(mHashMap.get("title").get(position));
        holder.mReadingTimeTextView.setText(mHashMap.get("dateReading").get(position));
        holder.mSendingTimeTextView.setText(mHashMap.get("dateSending").get(position));
        holder.mSourceTextView.setText(mHashMap.get("source").get(position));
    }

    @Override
    public int getItemCount() {
        return mHashMap.get("title").size();
    }
}
