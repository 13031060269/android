/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hfax.ucard.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

public class TabStrip extends View implements ViewPager.OnPageChangeListener {
    private final static int HIGH_QUALITY_FLAGS = Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG;
    private final static String PREVIEW_TITLE = "Title";
    private final static int INVALID_INDEX = -1;

    /**
     * 默认配置变量
     */
    private final static int DEFAULT_ANIMATION_DURATION = 200;
    private final static float DEFAULT_STRIP_FACTOR = 2.5F;
    private final static float DEFAULT_CORNER_RADIUS = 5.0F;
    private final static int DEFAULT_INACTIVE_COLOR = Color.GRAY;
    private final static int DEFAULT_ACTIVE_COLOR = Color.WHITE;
    private final static int DEFAULT_STRIP_COLOR = Color.RED;
    private final static int DEFAULT_TITLE_SIZE=0;
    /**
     * 默认文字比例
     */
    private final static float TITLE_SIZE_FRACTION = 0.35F;
    /**
     * 动画执行系数
     */
    private final static float MIN_FRACTION = 0.0F;
    private final static float MAX_FRACTION = 1.0F;

    /**
     * 横条的宽高
     */
    private float mStripHeight, mStripWidth;


    /**
     * 滑动样式
     */
    private final static int MODE_NORMAL = 0;//下方横条
    private final static int MODE_COVER = 1;//包裹
    private int mode = MODE_NORMAL;

    /**
     * 视图及内部组件BOUNDS
     */
    private final RectF mBounds = new RectF();
    private final RectF mStripBounds = new RectF();
    private final Rect mTitleBounds = new Rect();
    /**
     * 标签画笔
     */
    private final Paint mStripPaint = new Paint(HIGH_QUALITY_FLAGS) {
        {
            setStyle(Style.FILL);
        }
    };
    /**
     * 标签文字画笔
     */
    private final Paint mTitlePaint = new TextPaint(HIGH_QUALITY_FLAGS) {
        {
            setTextAlign(Align.CENTER);
        }
    };

    /**
     * 动画变量
     */
    private final ValueAnimator mAnimator = new ValueAnimator();
    private final ArgbEvaluator mColorEvaluator = new ArgbEvaluator();
    private final ResizeInterpolator mResizeInterpolator = new ResizeInterpolator();
    private int mAnimationDuration;
    private final static int ANIM_BITI = 0;//滑动时像鼻涕一样的黏着
    private final static int ANIM_TRANSLATION = 1;//滑动时平移
    private final static int ANIM_NONE = 2;//滑动时无动画
    private int anim = ANIM_BITI;//当前动画

    /**
     * 标签内容
     */
    private String[] mTitles;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    /**
     * viewPager滑动状态
     */
    private int mScrollState;

    /**
     * tab滑动事件
     */
    private OnTabStripSelectedIndexListener mOnTabStripSelectedIndexListener;
    private ValueAnimator.AnimatorListener mAnimatorListener;

    /**
     * 标签宽度
     */
    private float mTabSize;
    /**
     * 文字大小
     */
    private float mTitleSize;

    private float mSingleCharSize = -1F;
    /**
     * 滑块圆角
     */
    private float mCornersRadius;

    /**
     * 滑动索引位置 及 历史索引
     */
    private int mLastIndex = INVALID_INDEX;
    private int mIndex = INVALID_INDEX;
    /**
     * 偏移量百分比
     */
    private float mFraction;

    /**
     * 滑块位置 及 滑动位置
     */
    private float mStartStripX;
    private float mEndStripX;
    private float mStripLeft;
    private float mStripRight;

    /**
     * 是否存在viewpager
     */
    private boolean mIsViewPagerMode;
    /**
     * 检测是否从左向右移动
     */
    private boolean leftToRight;
    private boolean mIsResizeIn;
    /**
     * 检测Action Down
     */
    private boolean mIsActionDown;
    /**
     * 检测Action Down 是否在tab 上
     */
    private boolean mIsTabActionDown;
    /**
     * 检测是滑动响应还是方法调用响应
     */
    private boolean mIsSetIndexFromTabBar;
    /**
     * 状态色值
     */
    private int mInactiveColor;
    private int mActiveColor;

    public TabStrip(Context context) {
        this(context, null);
    }

    public TabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(isInEditMode())return;
        setWillNotDraw(false);
        // 17 API 会出现花屏问题
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabStrip);
        try {
            setStripColor(
                    typedArray.getColor(R.styleable.TabStrip_ts_color, DEFAULT_STRIP_COLOR)
            );
            setTitleSize(
                    typedArray.getDimension(R.styleable.TabStrip_ts_size, DEFAULT_TITLE_SIZE)
            );
            setTypeface(typedArray.getString(R.styleable.TabStrip_ts_typeface));
            setInactiveColor(
                    typedArray.getColor(
                            R.styleable.TabStrip_ts_inactive_color, DEFAULT_INACTIVE_COLOR
                    )
            );
            setActiveColor(
                    typedArray.getColor(
                            R.styleable.TabStrip_ts_active_color, DEFAULT_ACTIVE_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.TabStrip_ts_animation_duration, DEFAULT_ANIMATION_DURATION
                    )
            );
            setCornersRadius(
                    typedArray.getDimension(
                            R.styleable.TabStrip_ts_corners_radius, DEFAULT_CORNER_RADIUS
                    )
            );
            setMode(typedArray.getInt(R.styleable.TabStrip_ts_strip_mode, mode));
            setAnim(typedArray.getInt(R.styleable.TabStrip_ts_strip_anim, anim));
            float  fact =1.0f;
            if(anim==ANIM_BITI){
                fact=typedArray.getFloat(R.styleable.TabStrip_ts_factor, DEFAULT_STRIP_FACTOR);
            }
            setStripFactor(fact);
            mStripHeight = typedArray.getDimension(R.styleable.TabStrip_ts_strip_height, Utils.dip2px(BaseApplication.getContext(), 4));
            mStripWidth = typedArray.getDimension(R.styleable.TabStrip_ts_strip_width, 0);
            // 获取标题
            String[] titles = null;
            try {
                final int titlesResId = typedArray.getResourceId(
                        R.styleable.TabStrip_ts_titles, 0
                );
                titles = titlesResId == 0 ? null :
                        typedArray.getResources().getStringArray(titlesResId);
            } catch (Exception exception) {
                titles = null;
                exception.printStackTrace();
            } finally {
                if (titles == null) {
                    if (isInEditMode()) {
                        titles = new String[new Random().nextInt(5) + 1];
                        Arrays.fill(titles, PREVIEW_TITLE);
                    } else titles = new String[0];
                }

                setTitles(titles);
            }

            // 初始化动画
            mAnimator.setFloatValues(MIN_FRACTION, MAX_FRACTION);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    updateIndicatorPosition((Float) animation.getAnimatedValue());
                }
            });
        } finally {
            typedArray.recycle();
        }
        mTitlePaint.setFakeBoldText(true);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isInEditMode())return;
        // 获取测量值
        final float width = MeasureSpec.getSize(widthMeasureSpec);
        final float height = MeasureSpec.getSize(heightMeasureSpec);
        // 设置控件边界值
        mBounds.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        mBounds.offset(-getPaddingLeft(), -getPaddingTop());
        if (mTitles.length == 0 || width == 0 || height == 0) return;

        // 获取标签宽度
        mTabSize = mBounds.width() / (float) mTitles.length;
        if ((int) mTitleSize == DEFAULT_TITLE_SIZE)
            setTitleSize((mBounds.height() - mStripHeight) * TITLE_SIZE_FRACTION);
        if (isInEditMode() || !mIsViewPagerMode) {
            mIsSetIndexFromTabBar = true;

            if (isInEditMode()) mIndex = new Random().nextInt(mTitles.length);

            mStartStripX =
                    mIndex * mTabSize;
            mEndStripX = mStartStripX;
            updateIndicatorPosition(MAX_FRACTION);
        }

    }

    public void setAnimationDuration(final int animationDuration) {
        mAnimationDuration = animationDuration;
        mAnimator.setDuration(mAnimationDuration);
        resetScroller();
    }

    public void setTitles(final int... titleResIds) {
        final String[] titles = new String[titleResIds.length];
        for (int i = 0; i < titleResIds.length; i++)
            titles[i] = getResources().getString(titleResIds[i]);
        setTitles(titles);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    public void setAnim(int anim) {
        this.anim = anim;
    }

    public void setTitles(final String... titles) {
        for (int i = 0; i < titles.length; i++) titles[i] = titles[i].toUpperCase();
        mTitles = titles;
        requestLayout();
    }

    public void setStripColor(final int color) {
        mStripPaint.setColor(color);
        postInvalidate();
    }

    public void setStripFactor(final float factor) {
        mResizeInterpolator.setFactor(factor);
    }

    public void setTypeface(final String typeface) {
        if (TextUtils.isEmpty(typeface)) return;

        Typeface tempTypeface;
        try {
            tempTypeface = Typeface.createFromAsset(getContext().getAssets(), typeface);
        } catch (Exception e) {
            tempTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
            e.printStackTrace();
        }

        setTypeface(tempTypeface);
    }

    public void setTypeface(final Typeface typeface) {
        mTitlePaint.setTypeface(typeface);
        postInvalidate();
    }

    public void setActiveColor(final int activeColor) {
        mActiveColor = activeColor;
        postInvalidate();
    }

    public void setInactiveColor(final int inactiveColor) {
        mInactiveColor = inactiveColor;
        postInvalidate();
    }

    public void setCornersRadius(final float cornersRadius) {
        mCornersRadius = cornersRadius;
        postInvalidate();
    }

    public void setTitleSize(final float titleSize) {
        mTitleSize = titleSize;
        mTitlePaint.setTextSize(titleSize);
        postInvalidate();
    }

    public OnTabStripSelectedIndexListener getOnTabStripSelectedIndexListener() {
        return mOnTabStripSelectedIndexListener;
    }

    /**
     * 设置滑块Start 和 end 方法
     *
     * @param onTabStripSelectedIndexListener
     */
    public void setOnTabStripSelectedIndexListener(final OnTabStripSelectedIndexListener onTabStripSelectedIndexListener) {
        mOnTabStripSelectedIndexListener = onTabStripSelectedIndexListener;

        if (mAnimatorListener == null)
            mAnimatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(final Animator animation) {
                    if (mOnTabStripSelectedIndexListener != null)
                        mOnTabStripSelectedIndexListener.onStartTabSelected(mTitles[mIndex], mIndex);

                    animation.removeListener(this);
                    animation.addListener(this);
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    if (mIsViewPagerMode) return;

                    animation.removeListener(this);
                    animation.addListener(this);

                    if (mOnTabStripSelectedIndexListener != null)
                        mOnTabStripSelectedIndexListener.onEndTabSelected(mTitles[mIndex], mIndex);
                }
            };
        mAnimator.removeListener(mAnimatorListener);
        mAnimator.addListener(mAnimatorListener);
    }

    public void setViewPager(final ViewPager viewPager) {
        if (viewPager == null) {
            mIsViewPagerMode = false;
            return;
        }

        if (viewPager.equals(mViewPager)) return;
        if (mViewPager != null)
            mViewPager.setOnPageChangeListener(null);
        if (viewPager.getAdapter() == null)
            throw new IllegalStateException("ViewPager does not provide adapter instance.");

        mIsViewPagerMode = true;
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);

        resetScroller();
        postInvalidate();
    }

    public void setViewPager(final ViewPager viewPager, int index) {
        setViewPager(viewPager);

        mIndex = index;
        if (mIsViewPagerMode) mViewPager.setCurrentItem(index, true);
        postInvalidate();
    }

    /**
     * 重置滑动及动画持续时间
     */
    private void resetScroller() {
        if (mViewPager == null) return;
        try {
            final Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            final ResizeViewPagerScroller scroller = new ResizeViewPagerScroller(getContext());
            scrollerField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public int getTabIndex() {
        return mIndex;
    }

    public void setTabIndex(int index) {
        setTabIndex(index, false);
    }

    /**
     * 设置Tab索引
     *
     * @param tabIndex
     * @param isForce
     */
    public void setTabIndex(int tabIndex, boolean isForce) {
        if (mAnimator.isRunning()) return;
        if (mTitles.length == 0) return;

        int index = tabIndex;
        boolean force = isForce;

        // 初始化，选中默认
        if (mIndex == INVALID_INDEX) force = true;

        // 判断历史索引和当前索引是否相同
        if (index == mIndex) return;

        // 索引定为标签大小
        index = Math.max(0, Math.min(index, mTitles.length - 1));
        mIsResizeIn = index < mIndex;
        mLastIndex = mIndex;
        mIndex = index;

        mIsSetIndexFromTabBar = true;
        if (mIsViewPagerMode) {
            if (mViewPager == null) throw new IllegalStateException("ViewPager is null.");
            mViewPager.setCurrentItem(index, !force);
        }

        //设置开始和结束位置
        mStartStripX = mStripLeft;
        mEndStripX = (mIndex * mTabSize);

        // 是否需要立即更新，还是动画执行更新
        if (force) {
            updateIndicatorPosition(MAX_FRACTION);
            // 包含viewpager则刷新
            if (mIsViewPagerMode) {
                if (!mViewPager.isFakeDragging()) mViewPager.beginFakeDrag();
                if (mViewPager.isFakeDragging()) {
                    mViewPager.fakeDragBy(0.0F);
                    mViewPager.endFakeDrag();
                }
            }
        } else mAnimator.start();
    }

    /**
     * 复位滑块
     */
    private void deselect() {
        mLastIndex = INVALID_INDEX;
        mIndex = INVALID_INDEX;
        mStartStripX = INVALID_INDEX * mTabSize;
        mEndStripX = mStartStripX;
        updateIndicatorPosition(MIN_FRACTION);
    }

    /**
     * 更新滑块位置
     *
     * @param fraction 偏移量
     */
    private void updateIndicatorPosition(final float fraction) {
        mFraction = fraction;

        // 计算左侧坐标
        mStripLeft =
                mStartStripX + (mResizeInterpolator.getResizeInterpolation(fraction, mIsResizeIn) *
                        (mEndStripX - mStartStripX));
        // 计算右侧坐标
        mStripRight =
                (mStartStripX + mTabSize) +
                        (mResizeInterpolator.getResizeInterpolation(fraction, !mIsResizeIn) *
                                (mEndStripX - mStartStripX));

        // 更新界面
        if (anim!= ANIM_NONE || (fraction == MAX_FRACTION || fraction == MIN_FRACTION)) {//有动画或者滑到边界则更新界面
            postInvalidate();
        }
    }

    /**
     * 更新界面
     */
    private void notifyDataSetChanged() {
        requestLayout();
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // 动画正在执行 不响应
        if (mAnimator.isRunning()) return true;
        // ViewPager 不处于IDLE 不响应
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsActionDown = true;
                if (!mIsViewPagerMode) break;
                // 是否点击了标签并滑动
                mIsTabActionDown = (int) (event.getX() / mTabSize) == mIndex;
                break;
            case MotionEvent.ACTION_MOVE:
                // 点击并滑动
                if (mIsTabActionDown) {
                    mViewPager.setCurrentItem((int) ((event.getX() - getPaddingLeft()) / mTabSize), false);
                    break;
                }
                if (mIsActionDown) break;
            case MotionEvent.ACTION_UP:
                // 选中指定标签
                if (mIsActionDown)
                    setTabIndex((int) ((event.getX() - getPaddingLeft()) / mTabSize), true);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                // 重置状态
                mIsTabActionDown = false;
                mIsActionDown = false;
                break;
        }

        return true;
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        if(isInEditMode())return;
        canvas.translate(getPaddingLeft(), getPaddingTop());
        // 绘制标签文字
        for (int i = 0; i < mTitles.length; i++) {
            final String title = mTitles[i];
            final float leftTitleOffset = (mTabSize * i) + (mTabSize * 0.5F);
            mTitlePaint.getTextBounds(title, 0, title.length(), mTitleBounds);
            if (i == 0) {
                drawStrip(canvas);
            }
            final float topTitleOffset = mBounds.height() * 0.5F +
                    mTitleBounds.height() * 0.5F - mTitleBounds.bottom;
            if (mSingleCharSize <= 0) {
                mSingleCharSize = mTitleBounds.width() / title.length();
            }

            // 获取当前和历史位置
            final float interpolation = mResizeInterpolator.getResizeInterpolation(mFraction, true);
            final float lastInterpolation = mResizeInterpolator.getResizeInterpolation(mFraction, false);

            if (mIsSetIndexFromTabBar) {
                if (mIndex == i) updateCurrentTitle(interpolation);
                else if (mLastIndex == i) updateLastTitle(lastInterpolation);
                else updateInactiveTitle();
            } else {
                if (i != mIndex && i != mIndex + 1) updateInactiveTitle();
                else if (i == mIndex + 1) updateCurrentTitle(interpolation);
                else if (i == mIndex) updateLastTitle(lastInterpolation);
            }
            //绘制文字
            canvas.drawText(
                    title, leftTitleOffset,
                    topTitleOffset,
                    mTitlePaint
            );
        }
    }

    private void drawStrip(Canvas canvas) {
        mStripBounds.set(
                mStripLeft + (mBounds.width() / mTitles.length - mTitleBounds.width()) * 0.5F,
                mBounds.height() - mStripHeight,
                mStripRight - (mBounds.width() / mTitles.length - mTitleBounds.width()) * 0.5F,
                mBounds.height()
        );
        switch (mode) {
            case MODE_NORMAL:
                if (mStripWidth != 0) {
                    mStripBounds.inset((mTitleBounds.width() - mStripWidth) / 2, 0);
                }
                if (anim==ANIM_TRANSLATION) {
                    resetStripBounds(mStripWidth == 0 ? mTitleBounds.width() : mStripWidth);
                }
                break;
            case MODE_COVER:
                mStripBounds.set(mStripBounds.left, 0, mStripBounds.right, mTitleBounds.height());
                mStripBounds.offset(0, mBounds.centerY() - mStripBounds.centerY());
                mStripBounds.inset(-mTitleBounds.height() / 2, -mTitleBounds.height() / 3);
                if (anim==ANIM_TRANSLATION) {
                    resetStripBounds(mTitleBounds.width() +mTitleBounds.height());
                }
                break;
        }
        if (mCornersRadius == 0) {
            canvas.drawRect(mStripBounds, mStripPaint);
        } else {
            canvas.drawRoundRect(mStripBounds, mCornersRadius, mCornersRadius, mStripPaint);
        }
    }

    /**
     * 重新设置Strip的宽度
     * @param windth
     */
    private void resetStripBounds(float windth) {
        float left = mStripBounds.centerX() - windth / 2;
        float right = mStripBounds.centerX() + windth / 2;
        mStripBounds.left = left;
        mStripBounds.right = right;
    }


    /**
     * 更新当前标签文学色值
     */
    private void updateCurrentTitle(final float interpolation) {
        mTitlePaint.setColor(
                Integer.parseInt(String.valueOf(mColorEvaluator.evaluate(interpolation, mInactiveColor, mActiveColor)))
        );
    }

    /**
     * 更新历史标签文学色值
     */
    private void updateLastTitle(final float lastInterpolation) {
        mTitlePaint.setColor(
                Integer.parseInt(String.valueOf(mColorEvaluator.evaluate(lastInterpolation, mActiveColor, mInactiveColor)))
        );
    }

    /**
     * 更新正常状态标签色值
     */
    private void updateInactiveTitle() {
        mTitlePaint.setColor(mInactiveColor);
    }


    private float startOffset = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, final int positionOffsetPixels) {
        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);

        if (startOffset == -1) {
            startOffset = positionOffset;
        }

        leftToRight = positionOffset <= startOffset;

        if (!mIsSetIndexFromTabBar) {
            mIsResizeIn = position < mIndex;
            mLastIndex = mIndex;
            mIndex = position;

            mStartStripX =
                    (position * mTabSize);
            mEndStripX = mStartStripX + mTabSize;
            updateIndicatorPosition(positionOffset);
        }

        if (!mAnimator.isRunning() && mIsSetIndexFromTabBar) {
            mFraction = MIN_FRACTION;
            mIsSetIndexFromTabBar = false;
        }
    }

    @Override
    public void onPageSelected(final int position) {
        // onPageScrollStateChanged 中判断选中状态
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        mScrollState = state;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mOnPageChangeListener != null) mOnPageChangeListener.onPageSelected(mIndex);
            if (mIsViewPagerMode && mOnTabStripSelectedIndexListener != null)
                mOnTabStripSelectedIndexListener.onEndTabSelected(mTitles[mIndex], mIndex);
            startOffset = -1;
        }

        if (mOnPageChangeListener != null) mOnPageChangeListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mIndex = savedState.index;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.index = mIndex;
        return savedState;
    }

    /**
     * 保存选中状态
     */
    private static class SavedState extends BaseSavedState {

        private int index;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            index = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(index);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        requestLayout();

        final int tempIndex = mIndex;
        deselect();
        post(new Runnable() {
            @Override
            public void run() {
                setTabIndex(tempIndex, true);
            }
        });
    }

    /**
     * 自定义Scroller 修改滑动时间
     */
    private class ResizeViewPagerScroller extends Scroller {

        public ResizeViewPagerScroller(Context context) {
            super(context, new AccelerateDecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mAnimationDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mAnimationDuration);
        }
    }

    /**
     * 自定义差值器
     * 获取动画执行变量
     */
    private static class ResizeInterpolator implements Interpolator {

        // 弹性系数
        private float mFactor;
        // 反向正向弹性操作
        private boolean mResizeIn;

        public void setFactor(final float factor) {
            mFactor = factor;
        }

        @Override
        public float getInterpolation(final float input) {
            if (mResizeIn) return (float) (1.0F - Math.pow((1.0F - input), 2.0F * mFactor));
            else return (float) (Math.pow(input, 2.0F * mFactor));
        }

        public float getResizeInterpolation(final float input, final boolean resizeIn) {
            mResizeIn = resizeIn;
            return getInterpolation(input);
        }
    }


    /**
     * 滑块移动 开始 结束事件
     */
    public interface OnTabStripSelectedIndexListener {
        void onStartTabSelected(final String title, final int index);

        void onEndTabSelected(final String title, final int index);
    }
}
