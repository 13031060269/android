package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Scroller;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.modules.entrance.UCardApplication;
import com.hfax.ucard.utils.UCardUtil;


public class ScaleView extends View{

    private RectF mBorderRectF = new RectF();
    private RectF scaleRectF = new RectF();
    private Paint mCurrentMarkPaint = new Paint();
    private Paint mScaleMarkPaint = new Paint();

    int offsetY = -Utils.dip2px(BaseApplication.getContext(), 38);
    private static final int ONEDP = Utils.dip2px(BaseApplication.getContext(), 1);

    private int mTextHeight = Utils.dip2px(BaseApplication.getContext(), 12); //数字字体大小

    private int offset = 0;
    private int dis, maxDis;  //刻度间距   由mWidth 和 allBlockNum计算得到
    private float mWidth;

    private int maxNum = 100; //最大数字
    private int minNum = -100; //最小数字
    private int minScale = 1000;
    private int scaleNum = minScale/4; //每一个刻度间相差数
    private int curNum;
    ScaleScroller mScroller = new ScaleScroller(getContext(), new ScrollingListener() {
        @Override
        public void onScroll(int distance) {
            offset += distance;
            if (offset > 0) {
                offset = 0;
            }
            if (offset < -maxDis) {
                offset = -maxDis;
            }
            postInvalidate();
        }

        @Override
        public void onFinished() {
            if (!canStop()) {
                Scroller scroller = mScroller.scroller;
                offset = -(int) (((getCurNum() - minNum) / (float) (maxNum - minNum)) * maxDis);
            }
            postInvalidate();
            if(numberListener!=null){
                numberListener.onFinished();
            }
        }
    });
    private ScrollingListener numberListener;

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaints();
    }

    private void initPaints() {

        mScaleMarkPaint.setColor(0xFFD9D6D9);
        mScaleMarkPaint.setStyle(Paint.Style.FILL);
        mScaleMarkPaint.setStrokeWidth(ONEDP);
        mScaleMarkPaint.setTextSize(mTextHeight);
//        mScaleMarkPaint.setTypeface(UCardApplication.getAlternateBoldTtf());

        mCurrentMarkPaint.setColor(0xFFF59B4E);
        mCurrentMarkPaint.setStyle(Paint.Style.FILL);
        mCurrentMarkPaint.setStrokeWidth(ONEDP);

    }

    //刷新视图
    private void refreshCanvas(Canvas canvas) {
        if (mBorderRectF.isEmpty()) {
            return;
        }
        if (canvas != null) {
            canvas.save();

            canvas.drawColor(Color.WHITE);
            drawScaleMark(canvas);//刻度
            drawMarkPoint(canvas);//中间的高亮竖线

            canvas.restore();
        }
    }

    private void drawScaleMark(Canvas canvas) {
        canvas.save();
        Paint p = new Paint();
        p.setColor(0xFFF7F6F9);
        if (curNum >= minNum && curNum <= maxNum) {
            int num=(curNum+minScale/2)/minScale;
            offset = -(int) ((num*minScale - minNum) / (float) (maxNum - minNum) * maxDis);
        }
        canvas.drawRect(0, 0, mWidth, getHeight() - offsetY, p);
        canvas.translate(mWidth / 2 + offset, -offsetY);
        if (numberListener != null) {
            if(curNum!=0){
                numberListener.onScroll(curNum);
            }else{
                numberListener.onScroll(getCurNum());
            }
        }
        curNum=0;
        int left = 0, bottom = canvas.getHeight() - 1;
        for (int i = minNum; i <= maxNum; i += scaleNum) {
            scaleRectF.set(left, bottom - Utils.dip2px(BaseApplication.getContext(), 8), left + 2, bottom);
            String text = i + "";
            mScaleMarkPaint.setColor(0xFFCDCACD);
            if ((i == minNum) || (i == maxNum)) {
                mScaleMarkPaint.setColor(0xFF929096);
                scaleRectF.top = scaleRectF.bottom - Utils.dip2px(BaseApplication.getContext(), 40);
                canvas.drawText(text, scaleRectF.centerX() - mScaleMarkPaint.measureText(text) / 2, canvas.getHeight() + mTextHeight+Utils.dip2px(BaseApplication.getContext(), 8), mScaleMarkPaint);
            } else if (i % (2 * minScale) == 0) {
                scaleRectF.top = scaleRectF.bottom - Utils.dip2px(BaseApplication.getContext(), 26);
                canvas.drawText(text, scaleRectF.centerX() - mScaleMarkPaint.measureText(text) / 2, canvas.getHeight() + mTextHeight+Utils.dip2px(BaseApplication.getContext(), 8), mScaleMarkPaint);
            } else if (i % minScale == 0) {
                scaleRectF.top = scaleRectF.bottom - Utils.dip2px(BaseApplication.getContext(), 16);
                mScaleMarkPaint.setColor(0xFFCDCACD);
            }
            canvas.drawRect(scaleRectF, mScaleMarkPaint);
            left += dis;
        }
        drawBorder(canvas);//最下端的线
        canvas.restore();
    }

    private int getCurNum() {
        int cN = getCN();
        if (cN % minScale != 0) {
            cN = (cN + minScale / 2) / 1000 * 1000;
        }
        return cN;
    }
    public int getValidNum(int num){
        if(num<minNum){
            num=minNum;
        }
        if(num>maxNum){
            num=maxNum;
        }
        if (num % minScale != 0) {
            num = (num + minScale / 2) / 1000 * 1000;
        }
        return num;
    }

    int getCN() {
        return (int) (minNum + (-offset / (float) maxDis) * (maxNum - minNum));
    }

    private boolean canStop() {
        return getCN() % minScale == 0;
    }

    public void setCurNumber(int curNum) {
        if (curNum >= minNum && curNum <= maxNum) {
            this.curNum = curNum;
            postInvalidate();
        }
    }


    private void drawBorder(Canvas canvas) {//虚线
        DashPathEffect pathEffect = new DashPathEffect(new float[]{6, 2}, 1);
        Paint paint = new Paint(mScaleMarkPaint);
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(0xFFD9D6D9);
        paint.setAntiAlias(true);
        paint.setPathEffect(pathEffect);
        Path path = new Path();
        path.moveTo(-mWidth / 2, getHeight() - Utils.dip2px(BaseApplication.getContext(), 1));
        path.lineTo(maxDis + mWidth / 2, getHeight() - Utils.dip2px(BaseApplication.getContext(), 1));
        canvas.drawPath(path, paint);
    }


    private void drawMarkPoint(Canvas canvas) {
        int centerX = (int) mBorderRectF.centerX();
        RectF rf = new RectF(centerX - mCurrentMarkPaint.getStrokeWidth() / 2, canvas.getHeight() / 4, centerX + mCurrentMarkPaint.getStrokeWidth() / 2, canvas.getHeight() - 1 - offsetY);
        rf.offset(rf.width()/2,0);
        canvas.drawRect(rf, mCurrentMarkPaint);
        rf.left -= ONEDP * 2;
        rf.right += ONEDP * 2;
        rf.bottom = rf.top;
        rf.top = rf.bottom - ONEDP * 5;
        canvas.drawRect(rf, mCurrentMarkPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBorderRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mWidth = mBorderRectF.width();
        dis = (int) (mWidth / 30);
        maxDis = (maxNum - minNum) / scaleNum * dis;
        offsetY = (int) mBorderRectF.height() / 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        refreshCanvas(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mScroller.onTouchEvent(event, this);
    }


    public void setListener(ScrollingListener listener) {
        this.numberListener = listener;
    }

    public void setTextSize(int textSize) {
        this.mTextHeight = textSize;
    }

    public void setMaxNumber(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setMinNumber(int minNum) {
        this.minNum = minNum;
    }

    public void setScaleNumber(int minScale) {
        this.minScale = minScale;
        scaleNum = minScale/4;
    }

    //滑动回调
    public interface ScrollingListener {
        void onScroll(int distance);

        void onFinished();
    }

    static class ScaleScroller {
        private Context context;
        private GestureDetector gestureDetector; //滑动手势
        private Scroller scroller; //滑动辅助类
        private ScrollingListener listener;
        private int lastX;

        private Handler handler = new Handler();

        private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                final int minX = -0x7fffffff;
                final int maxX = 0x7fffffff;
                scroller.fling(0, 0, (int) -velocityX, 0, minX, maxX, 0, 0);
                startScroll();
                return true;
            }

        };

        void startScroll() {
            lastX = 0;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isFinished = scroller.computeScrollOffset();
                    int curX = scroller.getCurrX();
                    int delta = lastX - curX;
                    if (listener == null) return;
                    listener.onScroll(delta);
                    lastX = curX;
                    if (isFinished) {
                        handler.post(this);
                    } else {
                        listener.onFinished();
                    }
                }
            });
        }

        ScaleScroller(Context context, ScrollingListener listener) {
            this.context = context;
            this.listener = listener;
            init();
        }

        private void init() {
            gestureDetector = new GestureDetector(context, simpleOnGestureListener);
            gestureDetector.setIsLongpressEnabled(false);
            scroller = new Scroller(context);
        }

        //由外部传入event事件
        boolean onTouchEvent(MotionEvent event, View view) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    scroller.forceFinished(true);
                    lastX = (int) event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int distanceX = (int) (event.getX() - lastX);
                    if (distanceX != 0) {
                        listener.onScroll(distanceX);
                        lastX = (int) event.getX();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    listener.onFinished();
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
    }
}
