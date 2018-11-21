package com.zj.cointest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.zj.cointest.R;
import com.zj.cointest.bean.DepthBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * create by zj on 2018/11/20
 * 深度图
 */
public class DepthView  extends View{

    //是否显示详情
    private boolean isShowinfos;

    //单击后是否隐藏
    private boolean isClickHide;

    //是否长按
    private boolean isLongPress = false;

    //长按后是否隐藏
    private boolean isLongClickHide;

    //横坐标中间值
    private double abscissaCenterPrice;


    private List<DepthBean> buyList,sellList;

    private Paint strokePaint, fillPaint;
    private Rect textRect;
    private Path linePath,bgPath;

    private float leftStart, topStart, rightEnd, bottomEnd;

    private int buyLineColor,buyBgColor,buyLineStokeWidth,sellLineColor,sellBgColor,sellLineStokeWidth,
            abscissaColor,ordinateColor,abscissaTextSize,
            ordinateTextSize,ordinateNum,infoBgCol,infoTextCol,infoTextSize,infoLineCol,infoLineWidth,
            infoPointRadius;
    private String infoPriceTitle,infoVolumeTitle;

    public DepthView(Context context) {
        this(context,null);
    }

    public DepthView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public DepthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs){
        if (attrs!=null){
            TypedArray typedArray=context.obtainStyledAttributes(R.styleable.DepthView);
             buyLineColor=typedArray.getColor(R.styleable.DepthView_dvBuyLineColor,0xff03C087);
             buyBgColor=typedArray.getColor(R.styleable.DepthView_dvBuyBGColor,0x6603C087);
             buyLineStokeWidth=typedArray.getInteger(R.styleable.DepthView_dvBuyLineStrokeWidth,4);

             sellLineColor=typedArray.getColor(R.styleable.DepthView_dvSellLineColor,0xffFF6969);
             sellBgColor=typedArray.getColor(R.styleable.DepthView_dvSellBGColor,0x66FF6969);
             sellLineStokeWidth=typedArray.getInteger(R.styleable.DepthView_dvSellLineStrokeWidth,2);



            abscissaColor=typedArray.getColor(R.styleable.DepthView_dvAbscissaColor,0xff24256e);
            abscissaTextSize=typedArray.getInteger(R.styleable.DepthView_dvAbscissaTextSize,12);
            ordinateColor=typedArray.getColor(R.styleable.DepthView_dvOrdinateColor,0xff2BB8AB);
            ordinateTextSize=typedArray.getInteger(R.styleable.DepthView_dvAbscissaTextSize,12);
            ordinateNum=typedArray.getInteger(R.styleable.DepthView_dvOrdinateNum,10);


            infoBgCol = typedArray.getColor(R.styleable.DepthView_dvInfoBgColor, 0x99F3F4F6);
            infoTextCol = typedArray.getColor(R.styleable.DepthView_dvInfoTextColor, 0xff294058);
            infoTextSize = typedArray.getInt(R.styleable.DepthView_dvInfoTextSize, 10);
            infoLineCol = typedArray.getColor(R.styleable.DepthView_dvInfoLineCol, 0xff828EA2);
            infoLineWidth = typedArray.getInteger(R.styleable.DepthView_dvInfoLineWidth, 0);
            infoPointRadius = typedArray.getInt(R.styleable.DepthView_dvInfoPointRadius, 3);
            infoPriceTitle = typedArray.getString(R.styleable.DepthView_dvInfoPriceTitle);
            infoVolumeTitle = typedArray.getString(R.styleable.DepthView_dvInfoVolumeTitle);
            typedArray.recycle();
        }
        buyList=new ArrayList<>();
        sellList=new ArrayList<>();

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);

        textRect = new Rect();
        linePath = new Path();
        bgPath=new Path();


    }


    //set数据，买卖两个集合
    public void setData(List<DepthBean> buy,List<DepthBean> sell){
        buyList.clear();
        buyList.addAll(buy);
        Collections.sort(buyList);
        //计算累积交易量
        for (int i = buyList.size() - 1; i >= 0; i--) {
            if (i <buyList.size()-1) {
                buyList.get(i).setVolume(buyList.get(i).getVolume() + buyList.get(i + 1).getVolume());
            }
        }




        sellList.clear();
        sellList.addAll(sell);
        Collections.sort(sellList);

        //计算累积交易量
        for (int i = 0; i < sellList.size(); i++) {
            if (i > 0) {
                sellList.get(i).setVolume(sellList.get(i).getVolume() + sellList.get(i - 1).getVolume());
            }
        }



        invalidate();
    }

    private double maxVolume,minVolume,avgVolumeSpace, avgOrdinateSpace, depthImgHeight;

    private String leftPriceStr;
    private String rightPriceStr;
    private int priceScale=4;
    private int volumeScale=4;
    //x轴
    double avgWidthPerSize;
    //y轴刻度
    double avgHeightPerVolume;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        leftStart=getPaddingLeft()+10;
        rightEnd=getMeasuredWidth()-getPaddingRight()-1;
        topStart=getPaddingTop()+20;
        bottomEnd=getMeasuredHeight() - getPaddingBottom() - 1;


        double maxBuyVolume;
        double minBuyVolume;
        double maxSellVolume;
        double minSellVolume;

        if (!buyList.isEmpty()){
            maxBuyVolume=buyList.get(0).getVolume();
            minBuyVolume=buyList.get(buyList.size()-1).getVolume();
        }else{
            maxBuyVolume=minBuyVolume=0;
        }

        if (!sellList.isEmpty()){
            maxSellVolume=sellList.get(sellList.size()-1).getVolume();
            minSellVolume=sellList.get(0).getVolume();

        }else{
            maxSellVolume=minSellVolume=0;
        }
        maxVolume=Math.max(maxBuyVolume,maxSellVolume);
        minVolume=Math.min(minBuyVolume,minSellVolume);

        //buylist 不空，取买最低价 ，否则取卖最高价
        if (!buyList.isEmpty()){
            leftPriceStr=setPrecision(buyList.get(0).getPrice(),priceScale);
        }else if (!sellList.isEmpty()){
            leftPriceStr=setPrecision(sellList.get(0).getPrice(),priceScale);
        }else{

        }


        //selllist 不空 取卖最低价， 否则取买最高价
        if (!sellList.isEmpty()){
            rightPriceStr=setPrecision(sellList.get(sellList.size()-1).getPrice(),priceScale);
        }else if (!buyList.isEmpty()){
            rightPriceStr=setPrecision(buyList.get(buyList.size()-1).getPrice(),priceScale);
        }

        strokePaint.getTextBounds(leftPriceStr,0,leftPriceStr.length(),textRect);
        //深度图除文字外的高度，
        depthImgHeight = bottomEnd - topStart - textRect.height() - 18;

        avgHeightPerVolume= depthImgHeight / (maxVolume - minVolume);
        avgWidthPerSize= (rightEnd - leftStart) / (buyList.size() + sellList.size());
        avgVolumeSpace = maxVolume / ordinateNum;
        avgOrdinateSpace = depthImgHeight / ordinateNum;





    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (buyList.isEmpty()&&sellList.isEmpty()){
            return;
        }

        drawDepthTitle(canvas);
        drawLineAndBg(canvas);
        drawCoordinate(canvas);
    }

    //深度图标题
    public void drawDepthTitle(Canvas canvas){
        fillPaint.setColor(buyBgColor);
        canvas.drawRect(getMeasuredWidth()/2-65,topStart,getMeasuredWidth()/2-35,topStart+30,fillPaint);

        strokePaint.setColor(buyBgColor);
        strokePaint.setTextSize(30);
        strokePaint.setStrokeWidth(1);
        //x,y左下角位置
        canvas.drawText("买",getMeasuredWidth()/2-30,topStart+25,strokePaint);


        fillPaint.setColor(sellBgColor);
        canvas.drawRect(getMeasuredWidth()/2+20,topStart,getMeasuredWidth()/2+50,topStart+30,fillPaint);

        strokePaint.setColor(sellBgColor);
        strokePaint.setTextSize(30);
        strokePaint.setStrokeWidth(1);
        //x,y左下角位置
        canvas.drawText("卖",getMeasuredWidth()/2+55,topStart+25,strokePaint);
    }


    //绘制边线以及背景
    private void drawLineAndBg(Canvas canvas){
        //左侧买
        if (buyList!=null){
            linePath.reset();
            bgPath.reset();
            for (int i=0;i<buyList.size();i++){
                if (i == 0) {
                    linePath.moveTo(leftStart + (float) avgWidthPerSize * i,topStart + (float) ((maxVolume - buyList.get(i).getVolume()) * avgHeightPerVolume));
                    bgPath.moveTo(leftStart + (float) avgWidthPerSize * i,topStart + (float) ((maxVolume - buyList.get(i).getVolume()) * avgHeightPerVolume));
                }else{
                    linePath.lineTo(leftStart + (float) avgWidthPerSize * i,topStart + (float) ((maxVolume - buyList.get(i).getVolume()) * avgHeightPerVolume));
                    bgPath.lineTo(leftStart + (float) avgWidthPerSize * i,topStart + (float) ((maxVolume - buyList.get(i).getVolume()) * avgHeightPerVolume));
                }
            }

            if (!buyList.isEmpty() && topStart + (float) ((maxVolume - buyList.get(buyList.size()-1).getVolume()) * avgHeightPerVolume) < topStart + depthImgHeight) {
                bgPath.lineTo(leftStart + (float) avgWidthPerSize * (buyList.size()-1), (float) (topStart + depthImgHeight));
            }
            bgPath.lineTo(leftStart, (float) (topStart + depthImgHeight));
            bgPath.close();

            //整个买部分的范围
            fillPaint.setColor(buyBgColor);
            canvas.drawPath(bgPath, fillPaint);


            //边线
            strokePaint.setColor(buyLineColor);
            strokePaint.setTextSize(20);
            strokePaint.setStrokeWidth(4);
            canvas.drawPath(linePath,strokePaint);


        }

        //右侧卖

        if (sellList!=null){
            linePath.reset();
            bgPath.reset();

            for (int i=0;i<sellList.size();i++){
                if (i == 0) {
                    linePath.moveTo(leftStart + (float) avgWidthPerSize * (i+buyList.size()),topStart + (float) ((maxVolume - sellList.get(i).getVolume()) * avgHeightPerVolume));
                    bgPath.moveTo(leftStart + (float) avgWidthPerSize * (i+buyList.size()),topStart + (float) ((maxVolume - sellList.get(i).getVolume()) * avgHeightPerVolume));
                }else{
                    linePath.lineTo(leftStart + (float) avgWidthPerSize * (i+buyList.size()),topStart + (float) ((maxVolume - sellList.get(i).getVolume()) * avgHeightPerVolume));
                    bgPath.lineTo(leftStart + (float) avgWidthPerSize * (i+buyList.size()),topStart + (float) ((maxVolume - sellList.get(i).getVolume()) * avgHeightPerVolume));
                }
            }

            bgPath.lineTo(leftStart + (float) avgWidthPerSize * (buyList.size()+sellList.size()-1), (float) (topStart + depthImgHeight));
            if (!sellList.isEmpty() && topStart + (float) ((maxVolume - sellList.get(0).getVolume()) * avgHeightPerVolume)< (float) (topStart + depthImgHeight)) {
                bgPath.lineTo(leftStart + (float) avgWidthPerSize * (buyList.size()), (float) (topStart + depthImgHeight));
            }
            bgPath.close();
            fillPaint.setColor(sellBgColor);
            canvas.drawPath(bgPath, fillPaint);



            strokePaint.setColor(sellLineColor);
            strokePaint.setTextSize(20);
            strokePaint.setStrokeWidth(4);
            canvas.drawPath(linePath,strokePaint);
        }

    }

    //坐标轴，横纵坐标的值
    public void drawCoordinate(Canvas canvas){

        //横坐标
        strokePaint.setStrokeWidth(1);
        strokePaint.setColor(abscissaColor);
        strokePaint.setTextSize(20);

        strokePaint.getTextBounds(rightPriceStr,0,rightPriceStr.length(),textRect);
        canvas.drawText(leftPriceStr,leftStart+2,bottomEnd-5,strokePaint);
        canvas.drawText(rightPriceStr,rightEnd-textRect.width(),bottomEnd-5,strokePaint);


        double centerPrice=0;
        if (!buyList.isEmpty()&&!buyList.isEmpty()){
            centerPrice= (buyList.get(buyList.size()-1).getPrice()+sellList.get(0).getPrice())/2;
        }else if (!buyList.isEmpty()){
            centerPrice=buyList.get(buyList.size()-1).getPrice();
        }else if (!sellList.isEmpty()){
            centerPrice=sellList.get(buyList.size()-1).getPrice();
        }

        canvas.drawText(setPrecision(centerPrice,priceScale),getMeasuredWidth()/2-30,bottomEnd-5,strokePaint);


        //纵坐标
        strokePaint.setStrokeWidth(0);
        strokePaint.setColor(ordinateColor);
        strokePaint.setTextSize(20);

        strokePaint.getTextBounds(maxVolume+"",0,(maxVolume+"").length(),textRect);

        for (int i=0;i<ordinateNum;i++){
            String text=setPrecision(maxVolume-avgVolumeSpace*(i),volumeScale);
            canvas.drawText(text,leftStart+2,(float) (topStart+textRect.height()+i*avgOrdinateSpace),strokePaint);
        }

    }


    /**
     * 设置小数位精度
     *
     * @param num
     * @param scale 保留几位小数
     */
    private String setPrecision(Double num, int scale) {
        BigDecimal bigDecimal = new BigDecimal(num);
        return bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
    }



}
