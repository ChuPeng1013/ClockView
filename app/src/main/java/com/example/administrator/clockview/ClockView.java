package com.example.administrator.clockview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by ChuPeng on 2016/10/28.
 */
public class ClockView extends View
{
    private Paint dialPaint;//表盘
    private Paint centerPaint;//圆心
    private Paint calibrationPaint;//刻度
    private Paint numberPaint;//数字
    private Paint secondPaint;//秒针
    private Paint minutePaint;//分针
    private Paint hourPaint;//时针
    private int centerX;
    private int centerY;
    private int radius;//半径
    private int width;//当前屏幕的宽
    private int height;//当前屏幕的高
    private int statusBarHeight;//状态栏的高度
    private float degreeLength;//刻度的长度
    private int hourLength;//时针的长度
    private int minuteLength;//分针的长度
    private int secondLength;//秒针的长度
    private int hourReverseLength;//时针反向超过圆点的长度
    private int minuteReverseLength;//分针针反向超过圆点的长度
    private int secondReverseLength;//秒针反向超过圆点的长度
    private static final float DEFAULT_LONG_DEGREE_LENGTH = 40f;//长刻度线
    private static final float DEFAULT_SHORT_DEGREE_LENGTH = 30f;//短刻度线
    private int hour = 7;//初始化小时
    private int minute = 30;//初始化分钟
    private int second = 45;//初始化秒

    public ClockView(Context context)
    {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void drawHour(Canvas canvas, int hourTime, int minuteTime)
    {
        //绘制时针
        hourPaint = new Paint();
        hourPaint.setStrokeWidth(8);
        hourPaint.setAntiAlias(true);
        hourPaint.setColor(Color.GREEN);
        hourPaint.setStyle(Paint.Style.FILL);
        hourReverseLength = radius/8;
        hourLength = 2*radius/5;
        float hourDegrees = hourTime * 30 + (minuteTime/60f)*30;
        canvas.rotate(hourDegrees, centerX, centerY);
        canvas.drawLine(centerX, centerY + hourReverseLength, centerX, centerY - hourLength, hourPaint);
    }

    private void drawMinute(Canvas canvas, int minuteTime)
    {
        //绘制分针
        minutePaint = new Paint();
        minutePaint.setStrokeWidth(6);
        minutePaint.setAntiAlias(true);
        minutePaint.setColor(Color.RED);
        minutePaint.setStyle(Paint.Style.FILL);
        minuteReverseLength = radius/6;
        minuteLength = 3*radius/5;
        canvas.restore();
        canvas.rotate(minuteTime * 6, centerX, centerY);
        canvas.drawLine(centerX, centerY + minuteReverseLength, centerX, centerY - minuteLength, minutePaint);
    }

    private void drawSecond(Canvas canvas, int secondTime)
    {
        //绘制秒针
        secondPaint = new Paint();
        secondPaint.setStrokeWidth(4);
        secondPaint.setAntiAlias(true);
        secondPaint.setColor(Color.BLACK);
        secondPaint.setStyle(Paint.Style.FILL);
        secondReverseLength = radius/4;
        secondLength = 4*radius/5;
        canvas.restore();
        canvas.rotate(secondTime * 6, centerX, centerY);
        canvas.drawLine(centerX, centerY + secondReverseLength, centerX, centerY - secondLength, secondPaint);
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what == 1234)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                second = calendar.get(Calendar.SECOND);
                Log.d("time", hour + ":" + minute + ":" + second);
                invalidate();
            }

        }
    };

    //设置控件的尺寸
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕窗口
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //获取当前屏幕的宽
        width = windowManager.getDefaultDisplay().getWidth();
        //获取当前屏幕的高
        height = windowManager.getDefaultDisplay().getHeight();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    //设置控件的宽
    private int measureWidth(int widthMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }
        else if(widthMode == MeasureSpec.AT_MOST)
        {
            width = Math.min(width, widthSize);
        }
        return width;
    }

    //设置控件的高
    private int measureHeight(int heightMeasureSpec)
    {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        }
        else if(heightMode == MeasureSpec.AT_MOST)
        {
            height = Math.min(height, heightSize);
        }
        return height;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //获取状态栏的高度
        Rect statusBar = new Rect();
        getWindowVisibleDisplayFrame(statusBar);
        statusBarHeight = statusBar.top;
        //初始化圆心使圆心在屏幕的中心位置
        centerX = width/2;
        centerY = height/2 - statusBarHeight;
        //计算圆的半径
        radius = Math.min(centerX, centerY) - 30;
        //初始化画笔
        dialPaint = new Paint();
        dialPaint.setColor(Color.BLACK);
        dialPaint.setAntiAlias(true);
        dialPaint.setStyle(Paint.Style.STROKE);
        dialPaint.setStrokeWidth(5);
        //以当前屏幕的中心为原点画圆，绘制表盘
        canvas.drawCircle(centerX, centerY, radius, dialPaint);
        //绘制圆心
        canvas.drawPoint(centerX, centerY, dialPaint);
        //绘制刻度
        calibrationPaint = new Paint();
        calibrationPaint.setColor(Color.BLACK);
        calibrationPaint.setAntiAlias(true);
        /*//绘制数字
        numberPaint = new Paint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setAntiAlias(true);
        numberPaint.setTextAlign(Paint.Align.CENTER);//以数字的中心对齐
        numberPaint.setStrokeWidth(3);
        numberPaint.setTextSize(40);*/
        for(int i = 0; i < 60; i++)
        {
            if(i % 5 == 0)
            {
                calibrationPaint.setStrokeWidth(6);
                degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
                /*String num = String.valueOf(i / 5 == 0 ? 12 : i / 5);
                canvas.drawText(num, centerX, centerY - radius + degreeLength + 40, numberPaint);*/
            }
            else
            {
                calibrationPaint.setStrokeWidth(3);
                degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
            }
            canvas.drawLine(centerX, centerY - radius, centerX, centerY - radius + degreeLength, calibrationPaint);
            canvas.rotate(360 / 60, centerX, centerY);
        }
        //绘制数字
        numberPaint = new Paint();
        numberPaint.setStrokeWidth(3);
        numberPaint.setAntiAlias(true);
        numberPaint.setTextSize(40);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextAlign(Paint.Align.CENTER);//以数字的中心对齐
        float textSize = numberPaint.getFontMetrics().bottom - numberPaint.getFontMetrics().top;
        float distance = radius - DEFAULT_SHORT_DEGREE_LENGTH - 30;
        float a, b;//坐标
        for(int i = 0; i < 12; i++)
        {
            a = (float) (distance * Math.sin(i * 30 * Math.PI / 180));
            b = (float) (distance * Math.cos(i*30*Math.PI/180));
            if(i == 0)
            {
                canvas.drawText("12", centerX + a, centerY - b + textSize / 3, numberPaint);
            }
            else
            {
                canvas.drawText(String.valueOf(i), centerX + a, centerY - b + textSize / 3, numberPaint);
            }
        }
        canvas.save();
        //绘制指针
        drawHour(canvas, hour, minute);
        drawMinute(canvas, minute);
        drawSecond(canvas, second);
        //绘制圆心
        centerPaint = new Paint();
        centerPaint.setStrokeWidth(20);
        centerPaint.setAntiAlias(true);
        centerPaint.setColor(Color.BLACK);
        centerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, 10, centerPaint);
        handler.sendEmptyMessageDelayed(1234, 1000);
    }
}
