package com.hfax.ucard.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfax.ucard.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 导航头
 */
public class NavigationView extends RelativeLayout {
    @BindView(R.id.tv_title)
    TextView tv_title;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.base_title, this, true);
        ButterKnife.bind(this, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationView);
        String string = typedArray.getString(R.styleable.NavigationView_title);
        if (!TextUtils.isEmpty(string)) {
            tv_title.setText(string);
        }
        typedArray.recycle();
    }

    @OnClick({R.id.iv_title_return})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_return:
                finishActivity();
                break;
        }
    }

    private void finishActivity() {
        Context context = getContext();
        if (context != null && context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
