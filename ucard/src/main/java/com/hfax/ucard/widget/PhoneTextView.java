package com.hfax.ucard.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lwp
 *         检测手机号码变色并点击的textview
 */
public class PhoneTextView extends AppCompatTextView {
    static final String regex = "[0-9-+]{5,}";

    public PhoneTextView(Context context) {
        this(context, null);
    }

    public PhoneTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            Matcher matcher = Pattern.compile(regex).matcher(text);
            if (matcher.find()) {
                final String child = matcher.group();
                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                String str = text.toString();
                int index = str.indexOf(child);
                if (index > 0) {
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(0xFF4285f4);
                            ds.setUnderlineText(true);
                        }

                        @Override
                        public void onClick(View widget) {
                            Uri uri = Uri.parse("tel:" + child);
                            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                getContext().startActivity(intent);
                            }
                        }
                    }, index, index + child.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                setHighlightColor(0);
                text = builder;
            }
        }
        super.setText(text, type);
    }
}
