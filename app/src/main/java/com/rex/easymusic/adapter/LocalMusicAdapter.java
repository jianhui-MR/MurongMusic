package com.rex.easymusic.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;
import com.rex.easymusic.Bean.LocalMusic;

import java.util.List;

/**
 * Created by Bobby on 2018/7/14.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    private List<LocalMusic> songList;
    private Context context;

    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    private OnClickMoreListener mclickMoreListener;
    public void setClickMoreListener(OnClickMoreListener clickMoreListener){
        mclickMoreListener=clickMoreListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView singer;
        ImageView img_more;
        ImageView img_cover;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.song_name);
            singer=itemView.findViewById(R.id.song_singer);
            img_more=itemView.findViewById(R.id.more);
            img_cover=itemView.findViewById(R.id.img_cover);
        }
    }
    public LocalMusicAdapter(List<LocalMusic> msongList)
    {
        songList=msongList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context = parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.rcv_localmusic,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){
                    mItemClickListener.onItemClick((Integer)v.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocalMusic song=songList.get(position);
        holder.name.setText(song.getName());
        holder.singer.setText(song.getSinger());
        holder.itemView.setTag(position);
        if (song.albumArt!=null)
            holder.img_cover.setImageBitmap(BitmapFactory.decodeFile(song.albumArt));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
