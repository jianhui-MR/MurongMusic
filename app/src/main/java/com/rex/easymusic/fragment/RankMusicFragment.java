package com.rex.easymusic.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rex.easymusic.Activity.RankActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Rex on 2018/7/13.
 */

public class RankMusicFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.SurgeMusic_text)
    public TextView SurgeMusic_text;
    @BindView(R.id.NewMusic_text)
    public TextView NewMusic_text;
    @BindView(R.id.HotMusic_text)
    public TextView HotMusic_text;
    @BindView(R.id.OriginalMusic_text)
    public TextView  OriginalMusic_text;
    @BindView(R.id.AcgMusic_text)
    public TextView  AcgMusic_text;
    @BindView(R.id.BillboardMusic_text)
    public TextView  BillboardMusic_text;
    @BindView(R.id.SurgeMusic_Rank)
    public LinearLayout Surge;
    @BindView(R.id.Billboard_Rank)
    public LinearLayout Billboard;
    @BindView(R.id.HotMusic_Rank)
    public LinearLayout HotMusic;
    @BindView(R.id.Acg_Rank)
    public LinearLayout AcgMusic;
    @BindView(R.id.OriginalMusic_Rank)
    public LinearLayout OriginalMusic;
    @BindView(R.id.NewMusic_Rank)
    public LinearLayout NewMusic;

    public static final String Surge_Action="bobby.com.Surge";
    public static final String Billboard_Action="bobby.com.Billboard";
    public static final String Acg_Action="bobby.com.Acg";
    public static final String Hot_Action="bobby.com.Hot";
    public static final String Original_Action="bobby.com.Original";
    public static final String New_Action="bobby.com.New";
    public static final String RankActivity_Action="bobby.com.Rank";
    public static final String NewMusic_URL="http://106.13.36.192:3000/top/list?idx=0";
    public static final String HotMusic_URL="http://106.13.36.192:4000/top/list?idx=1";
    public static final String OriginalMusic_URL="http://106.13.36.192:3000/top/list?idx=2";
    public static final String SurgeMusic_URL="http://106.13.36.192:4000/top/list?idx=3";
    public static final String BillboardMusic_URL="http://106.13.36.192:3000/top/list?idx=6";
    public static final String AcgMusic_URL="http://106.13.36.192:3000/top/list?idx=22";
    private Intent intent;
    private View view;
    private Handler handler;
    private String finalMusic;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rank_layout, container, false);
        ButterKnife.bind(this,view);
        initHandler();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Surge.setOnClickListener(this);
        Billboard.setOnClickListener(this);
        HotMusic.setOnClickListener(this);
        AcgMusic.setOnClickListener(this);
        OriginalMusic.setOnClickListener(this);
        NewMusic.setOnClickListener(this);

        LoadMusic(NewMusic_URL);
        LoadMusic(HotMusic_URL);
        LoadMusic(OriginalMusic_URL);
        LoadMusic(SurgeMusic_URL);
        LoadMusic(BillboardMusic_URL);
        LoadMusic(AcgMusic_URL);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.obj.toString()){
                    case NewMusic_URL:
                        NewMusic_text.setText(finalMusic);
                        break;
                    case HotMusic_URL:
                        HotMusic_text.setText(finalMusic);
                        break;
                    case OriginalMusic_URL:
                        OriginalMusic_text.setText(finalMusic);
                        break;
                    case SurgeMusic_URL:
                        SurgeMusic_text.setText(finalMusic);
                        break;
                    case AcgMusic_URL:
                        AcgMusic_text.setText(finalMusic);
                        break;
                    case BillboardMusic_URL:
                        BillboardMusic_text.setText(finalMusic);
                        break;
                }
            }
        };
    }

    private void LoadMusic(final String URL)
    {
        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                String Music="";
                try {
                    JSONArray jsonArray=new JSONObject(body).getJSONObject("playlist").getJSONArray("tracks");
                    for (int i=1;i<4;i++)
                    {
                        JSONObject object = jsonArray.getJSONObject(i-1);
                        if (i==3) {
                            Music = Music+i+"."+object.getString("name");
                            break;
                        }
                        Music=Music+i+"."+object.getString("name")+"\n\n";
                    }
                    finalMusic = Music;
                    handler.obtainMessage(0,URL).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        intent=new Intent(getActivity(),RankActivity.class);
        intent.setAction(RankActivity_Action);
        Bundle bundle=new Bundle();
        switch (v.getId())
        {
            case R.id.SurgeMusic_Rank:
                bundle.putString("RankType",Surge_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.Billboard_Rank:
                bundle.putString("RankType",Billboard_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.Acg_Rank:
                bundle.putString("RankType",Acg_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.HotMusic_Rank:
                bundle.putString("RankType",Hot_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.NewMusic_Rank:
                bundle.putString("RankType",New_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.OriginalMusic_Rank:
                bundle.putString("RankType",Original_Action);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}



