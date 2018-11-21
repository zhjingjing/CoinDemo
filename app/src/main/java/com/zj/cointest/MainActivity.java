package com.zj.cointest;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zj.cointest.bean.DepthBean;
import com.zj.cointest.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this,R.layout.activity_main);
        binding.setPresenter(this);

        binding.depthView.setData(getBuyDepthList(),getSellDepthList());

    }



    //盘口
    public void onTradeListClick(){

    }


    //模拟深度数据
    private List<DepthBean> getBuyDepthList(){
        List<DepthBean> depthList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 40; i++) {
            depthList.add(new DepthBean(100 - random.nextDouble() * 10,
                    random.nextInt(10) * random.nextInt(10) * random.nextInt(10) + random.nextDouble(), 0,"BTC"));
        }
        return depthList;
    }

    //模拟深度数据
    private List<DepthBean> getSellDepthList(){
        List<DepthBean> depthList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 40; i++) {
            depthList.add(new DepthBean(100 + random.nextDouble() * 10,
                    random.nextInt(10) * random.nextInt(10) * random.nextInt(10) + random.nextDouble(), 1,"BTC"));
        }
        return depthList;
    }
}
