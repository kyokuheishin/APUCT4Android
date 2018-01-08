package com.kyokuheishin.android.apucampusterminalforandroid.RecyclerViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyokuheishin.android.apucampusterminalforandroid.DetailActivity;
import com.kyokuheishin.android.apucampusterminalforandroid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by qbx on 2017/7/7.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder> {
    private HashMap<String, ArrayList<String>> mHashMap;
    private ArrayList<String> mtitleList;
    private ArrayList<String> mreadingTimeList;
    private ArrayList<String> msendingTimeList;
    private ArrayList<String> msourceList;
    private int mSize;
    private Context mContext;

    public RecyclerViewAdapter(HashMap mHashmap,Context mContext){
        this.mHashMap = mHashmap;
        this.mContext = mContext;
        this.mtitleList = mHashMap.get("title");
        this.mSize = mtitleList.size();
        this.mreadingTimeList = mHashMap.get("dateReading");
        this.msendingTimeList = mHashMap.get("dateSending");
        this.msourceList = mHashMap.get("source");

    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{
        CardView mCardView;
        TextView mTitleTextView;
        TextView mSendingTimeTextView;
        TextView mReadingTimeTextView;
        TextView mSourceTextView;


        MessageViewHolder(View itemView) {
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
        /*CardViewのレイアウトを読み込むための処理*/

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_card,viewGroup,false);
        return new  MessageViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        try {
            /*原因不明かつ無視しても無害のIndexOutOfBoundsExceptionが出るため、try　catchを使っている。*/

            holder.mTitleTextView.setText(this.mtitleList.get(position));
            holder.mReadingTimeTextView.setText(this.mreadingTimeList.get(position));
            if (Objects.equals(this.mreadingTimeList.get(position), "")){
                holder.mReadingTimeTextView.setVisibility(View.GONE);
            }else {
                holder.mReadingTimeTextView.setVisibility(View.GONE);
            }
            holder.mSendingTimeTextView.setText(this.msendingTimeList.get(position));
            holder.mSourceTextView.setText(this.msourceList.get(position));
        }catch (IndexOutOfBoundsException e){
            System.out.println("IndexOutOfBoundsException");
        }


        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*二つのTextViewの連携アニメーションを実装*/

                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation
                        ((Activity)mContext,holder.mTitleTextView,"title");
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("selectedNo.",position);
                intent.putExtra("title",mHashMap.get("title").get(position));
                ActivityCompat.startActivity((Activity)mContext,intent,compat.toBundle());
            }
        });
    }



    @Override
    public int getItemCount() {
        return this.mSize;
    }

    public void swap(HashMap<String,ArrayList<String>> hashMap){


            this.mHashMap = hashMap;
            this.mSize = mHashMap.get("title").size();


        notifyDataSetChanged();
    }
}
