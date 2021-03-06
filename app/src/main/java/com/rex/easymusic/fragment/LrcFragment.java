package com.rex.easymusic.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.rex.easymusic.Activity.ServiceActivity;
import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.R;
import com.rex.easymusic.Bean.Lrc;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.LrcUtil;
import com.rex.easymusic.widget.LrcView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Rex on 2018/9/13.
 */
public class LrcFragment extends Fragment implements Runnable {

    @BindView(R.id.lrc_view)
    public  LrcView lrcView;
    @BindView(R.id.ScrollLrc)
    public ScrollView scrollView;


    private int currentPosition = 0;
    private  List<Lrc> mLrcList=new ArrayList<>();
    private Handler handler;
    private playerRecevier playerRecevier;
    private Unbinder unbinder;
    private final String TAG="LrcFragment";
    private PlayerService service;
    public final int LOCAL=0;
    public final int ONLINE=1;

    /* 生命周期 */
    @Nullable

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=((MusicApplication)getActivity().getApplication()).getPlayerService();
        setHandler();
        registerBroadcast();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lrc_layout,container,false);
        unbinder=ButterKnife.bind(this,view);
        setScrollViewTouch();
        setLrc();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(playerRecevier);
    }
    /* 生命周期 */


    /**
     * Handler设置
     */
    @SuppressLint("HandlerLeak")
    private void setHandler() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        setCurrentPosition();
                        break;
                    case 2:
                        lrcView.setLrcList(mLrcList);
                        break;
                }
            }
        };
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        playerRecevier=new playerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(ServiceActivity.BindSuccess);
        getContext().registerReceiver(playerRecevier,filter);
    }


    /**
     * 内部广播类，监听歌曲播放
     */
    class playerRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action=intent.getAction();
            switch (action){
                case PlayerService.plyingAction:
                    setLrc();
                    break;
            }
        }
    }

    /**
     * 对scrollView进行手势监听，监听按下后停止滚动，松手2.5秒后恢复自动滚动
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setScrollViewTouch(){
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action=motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacks(LrcFragment.this);
                        handler.postDelayed(LrcFragment.this,2500);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置歌词
     */
    private void setLrc()
    {
        if (service.musicType==service.ONLINE)
        {
            HttpUtil.sendOkHttpRequest(service.onlineMusicList.get(service.position).getLrcUrl(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    lrcView.setLrcList(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String LrcContent=jsonObject.getJSONObject("lrc").getString("lyric");
                        mLrcList= LrcUtil.ParseLrc(LrcContent);
                        if (mLrcList.size()==0)
                            mLrcList.clear();
                        handler.sendEmptyMessage(2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            //开启线程滚动
            handler.post(this);
        }
        else{
            mLrcList.clear();
            handler.sendEmptyMessage(2);
            handler.removeCallbacks(this);
        }

    }

    /**
     * 获取当前歌词正确位置
     */
    public void setCurrentPosition()
    {
        if (mLrcList.size()==0)
            return;
        int currentMillis = service.playerEngine.getCurrentPosition();
        if (currentMillis < mLrcList.get(0).time) {
            currentPosition = 0;
        }
        else if (currentMillis> mLrcList.get(mLrcList.size()-1).time)
        {
            currentPosition=mLrcList.size()-1;
        }
        else {
            for (int i=0;i<mLrcList.size();i++)
            {
                if (currentMillis>=mLrcList.get(i).time&&currentMillis<=mLrcList.get(i+1).time)
                {
                    currentPosition=i;
                }
            }
        }
        lrcView.setCurrentPosition(currentPosition);
        //移动歌词
        scrollView.smoothScrollTo(0,(currentPosition)*150+20);
    }

    @Override
    public void run() {
        try {
            if (service.playerEngine.isPlaying())
            {
                handler.sendEmptyMessage(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        handler.postDelayed(this, 600);
    }
}
