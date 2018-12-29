package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.OnlineMusic;

import java.util.List;

public class RankMusicAdapter extends RecyclerView.Adapter<RankMusicAdapter.ViewHolder> {
    private List<OnlineMusic> mrankMusicList;
    private Context context;
    private OnItemClickListener mItemClickListener;
    private OnClickMoreListener mclickMoreListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView RankMusicName;
        TextView RankMusicSinger;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            rank=itemView.findViewById(R.id.rank);
            RankMusicName=itemView.findViewById(R.id.Music_name);
            RankMusicSinger=itemView.findViewById(R.id.Singer);
            imageView=itemView.findViewById(R.id.more);
        }
    }

    public RankMusicAdapter(List<OnlineMusic> rankMusicList)
    {
        mrankMusicList=rankMusicList;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setClickMoreListener(OnClickMoreListener clickMoreListener){
        mclickMoreListener=clickMoreListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context = parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.rankmusic_item,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){
                    mItemClickListener.onItemClick((Integer)v.getTag());
                }
            }
        });
        ViewHolder holder=new ViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mclickMoreListener!=null)
                {
                    mclickMoreListener.onMoreClick((Integer)v.getTag());
                }
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(RankMusicAdapter.ViewHolder holder, int position) {
        holder.rank.setText(String.valueOf(position+1));
        holder.RankMusicName.setText(mrankMusicList.get(position).name);
        holder.RankMusicSinger.setText(mrankMusicList.get(position).artistsList.get(0).singer);
        holder.imageView.setTag(position);
        holder.itemView.setTag(position);
    }



    @Override
    public int getItemCount() {
        return mrankMusicList.size();
    }
}