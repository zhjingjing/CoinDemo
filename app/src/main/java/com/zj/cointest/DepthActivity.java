package com.zj.cointest;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zj.cointest.bean.DepthBean;
import com.zj.cointest.databinding.ActivityDepthBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DepthActivity extends AppCompatActivity {
    private ActivityDepthBinding binding;

    public static void launchActivity(Context context){
        Intent intent=new Intent(context,DepthActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this,R.layout.activity_depth);
        binding.setPresenter(this);
        binding.depthView.setData(getBuyDepthList(),getSellDepthList());
    }

    //模拟深度数据
    private List<DepthBean> getBuyDepthList(){
        List<DepthBean> depthList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            depthList.add(new DepthBean(100 - random.nextDouble() * 10,
                    random.nextInt(10) * random.nextInt(10) * random.nextInt(10) + random.nextDouble(), 0,"BTC"));
        }
        return depthList;
    }

    //模拟深度数据
    private List<DepthBean> getSellDepthList(){
        List<DepthBean> depthList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            depthList.add(new DepthBean(100 + random.nextDouble() * 10,
                    random.nextInt(10) * random.nextInt(10) * random.nextInt(10) + random.nextDouble(), 1,"BTC"));
        }
        return depthList;
    }
}
