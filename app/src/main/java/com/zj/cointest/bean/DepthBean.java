package com.zj.cointest.bean;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.PropertyResourceBundle;

/**
 * create by zj on 2018/11/20
 */
public class DepthBean implements Comparable<DepthBean> {

    private double price;//委托价
    private double volume;//委托量
    private int tradeType;
    private String coinName;



    public DepthBean(double price, double volume, int tradeType, String coinName) {
        this.price = price;
        this.volume = volume;
        this.tradeType = tradeType;
        this.coinName = coinName;
    }



    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }


    @Override
    public int compareTo(@NonNull DepthBean o) {
        double diff=this.price-o.price;
        if (diff>0){

            return 1;
        }else if (diff<0){

            return -1;
        }else{

            return 0;
        }
    }

    @Override
    public String toString() {
        return "DepthBean{price="+price+",volume="+volume+",coinName="+coinName+",tradeType="+tradeType+"}";
    }
}
