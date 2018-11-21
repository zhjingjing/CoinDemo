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

    }



    //盘口
    public void onTradeListClick(){

    }


    //深度图
    public void onDepthViewClick(){
        DepthActivity.launchActivity(this);
    }

}
