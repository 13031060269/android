package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hfax.ucard.R;

/**
 * 自动排版 ---
 *
 * @author SongGuangyao
 * @date 2018/4/27
 */

public class AutoSplitTextView extends AppCompatTextView {
    /**
     * 是否自动排版
     */
    private boolean mEnable = true;

    public AutoSplitTextView(Context context) {
        super(context);
        init();
    }

    public AutoSplitTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoSplitTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 设置是否自动排版
     *
     * @param mEnable
     */
    public void setSplitEnable(boolean mEnable) {
        this.mEnable = mEnable;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            String mobileStr = String.valueOf(text);
            int start = mobileStr.indexOf("www");
            int end = mobileStr.lastIndexOf("cn");
            SpannableString spanMobile = new SpannableString(mobileStr);
            spanMobile.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getContext().getResources().getColor(R.color.authorize_url_color));
                    ds.setUnderlineText(false);
                }


                @Override
                public void onClick(View widget) {
                    Log.e("info", "跳转");

                }
            }, start, end + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setHighlightColor(Color.TRANSPARENT);
            text = spanMobile;
        }
        super.setText(text, type);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mEnable
                && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                && getWidth() > 0
                && getHeight() > 0) {
            String str = autoSplitText();
            setText(str);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 排版文本
     *
     * @return
     */
    public String autoSplitText() {
        //原始文本
        String originText = getText().toString();
        //画笔
        TextPaint paint = getPaint();
        //可用宽度
        int useAbleWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        String[] split = originText.replaceAll("\r", "").split("\n");
        StringBuilder builder = new StringBuilder();
        for (String str :
                split) {
            if (paint.measureText(str) < useAbleWidth) {
                builder.append(str);
            } else {
                //行的宽度
                int lineWidth = 0;
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    lineWidth += paint.measureText(String.valueOf(c));
                    if (lineWidth <= useAbleWidth) {
                        builder.append(c);
                    } else {
                        builder.append("\n");
                        --i;
                        lineWidth = 0;
                    }

                }
            }
            builder.append("\n");

        }

        //把结尾多余的\n去掉
        if (!originText.endsWith("\n")) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
}
