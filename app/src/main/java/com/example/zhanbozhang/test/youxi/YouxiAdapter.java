package com.example.zhanbozhang.test.youxi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zhanbozhang.test.R;

import java.util.ArrayList;

public class YouxiAdapter extends RecyclerView.Adapter<YouxiAdapter.MyViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private Context mContext;

    private ArrayList<String> mDatas;

    private View mHeaderView;

    public YouxiAdapter(Context context, ArrayList<String> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new MyViewHolder(mHeaderView);
        }

        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.squar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        int realPos = getRealPosition(holder);
        holder.msgText.setText(mDatas.get(realPos));
    }

    private int getRealPosition(MyViewHolder holder) {
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        return position == 0 ? TYPE_HEADER : TYPE_NORMAL;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        Button msgText;

        public MyViewHolder(View itemView) {
            super(itemView);

            if (itemView == mHeaderView) {
                return;
            }

            msgText = itemView.findViewById(R.id.msg);
        }
    }
}
