package com.rex.easymusic.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Enum.LoadStateEnum;
import com.rex.easymusic.Popup.MusicMoreInfoPopup;
import com.rex.easymusic.R;
import com.rex.easymusic.util.ViewHandler;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.adapter.OnlineMusicAdapter;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ViewUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class FavouriteMusicActivity extends ToolbarActivity {

    private Handler handler;

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;

    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;

    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;

    private OnlineMusicAdapter adapter;

    private final String getFavouriteMusicUrl= ipAddressUtil.serviceIp+"/favouriteMusic/getFavouriteMusic";

    private List<OnlineMusic> onlineMusicList=new ArrayList<>();

    private FormBody body;
    private PlayerService service;

    @Override
    public int setLayoutId() {
        return R.layout.activity_favourite_music;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=((MusicApplication)getApplication()).getPlayerService();
        getSupportActionBar().setTitle("我喜欢的音乐");
        initRecyclerView();
        handler=new ViewHandler(recyclerView,llLoading,llLoadFail,adapter);
        loadFavouriteMusic();
    }


    /**
     * 初始化recyclerView
     */
    private void initRecyclerView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        adapter=new OnlineMusicAdapter(onlineMusicList);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                service.position=position;
                service.onlineMusicList=onlineMusicList;
                Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                sendBroadcast(intent);
            }
        });

        adapter.setClickMoreListener(new OnClickMoreListener() {
            @Override
            public void onMoreClick(int position) {
                MusicMoreInfoPopup musicMoreInfoPopup =new MusicMoreInfoPopup(FavouriteMusicActivity.this);
                musicMoreInfoPopup.setMorePopUp();
                musicMoreInfoPopup.showMorePopUp(onlineMusicList.get(position));
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    /**
     *加载我喜欢的音乐
     */
    private void loadFavouriteMusic(){
        body=new FormBody.Builder()
                .add("userAccount", LoginActivity.userAccount)
                .build();
        ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        HttpUtil.sendOkHttpRequest(getFavouriteMusicUrl,body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("songList");
                    for (int i=0;i<jsonArray.length();i++){
                        OnlineMusic onlineMusic=new OnlineMusic();
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        onlineMusic.setId(jsonObject.getInt("id"));
                        onlineMusic.setName(jsonObject.getString("songName"));
                        onlineMusic.setAlbum(jsonObject.getString("album"));
                        onlineMusic.setPicUrl(jsonObject.getString("coverUrl"));
                        onlineMusic.setSinger(jsonObject.getString("singer"));
                        onlineMusic.setLrcUrl(jsonObject.getString("lrcUrl"));
                        onlineMusic.setAudio(jsonObject.getString("audioUrl"));
                        onlineMusicList.add(onlineMusic);
                    }
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
