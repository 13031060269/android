package com.hfax.ucard.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @author lwp
 *         四位一分格的输入框
 */
public class BankCardEditText extends EditText {
    private static String cut = " ";

    public BankCardEditText(Context context) {
        this(context, null);
    }

    public BankCardEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BankCardEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    {
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        String before = text.toString().trim().replace(cut, "");
        int length = before.length();
        StringBuilder sb = new StringBuilder();
        int end;
        for (int i = 0; i < length; i += 4) {
            end = i + 4;
            if (end > length) {
                end=length;
            }
            sb.append(before.substring(i, end));
            if(end!=length){
                sb.append(cut);
            }
        }
        if (!TextUtils.equals(text, sb.toString())) {
            setText(sb.toString());
            setSelection(length());
        }
    }

    public String getCardNum() {
        return getText().toString().trim().replaceAll(cut, "");
    }
}
