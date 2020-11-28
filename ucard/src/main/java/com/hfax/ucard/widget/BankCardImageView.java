package com.hfax.ucard.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;

/**
 * @author lwp
 *         银行卡图标展示
 */
@SuppressLint("AppCompatCustomView")
public class BankCardImageView extends ImageView {
    Paint paint = new Paint();
    float margin = Utils.dip2px(BaseApplication.getContext(), 5);

    public BankCardImageView(Context context) {
        this(context, null);
    }

    public BankCardImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BankCardImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BankCardImageView, defStyleAttr, 0);
        margin = typedArray.getDimension(R.styleable.BankCardImageView_widthRound, margin);
        paint.setColor(typedArray.getColor(R.styleable.BankCardImageView_widthRound_color, Color.WHITE));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        canvas.save();
        canvas.clipRect(margin, margin, width - margin, width - margin);
        Drawable drawable = getDrawable();
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable == null) {
            drawable = BaseApplication.getContext().getResources().getDrawable(R.drawable.icon_default);
        }
        if (drawable != null) {
            drawable.setBounds(canvas.getClipBounds());
            drawable.draw(canvas);
        }
        canvas.restore();
    }
}
