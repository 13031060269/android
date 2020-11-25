package com.lwp.lib.host;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HostLayoutInflater extends LayoutInflater {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    protected HostLayoutInflater(Context context) {
        super(context);
    }

    HashMap<String, Constructor<? extends View>> sConstructorMap;

    protected HostLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new HostLayoutInflater(this, newContext);
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        clear();
        View inflate = super.inflate(parser, root, attachToRoot);
        clear();
        return inflate;
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return super.onCreateView(name, attrs);
    }


    void clear() {
        try {
            synchronized (this) {
                if (sConstructorMap == null) {
                    Field sConstructorMap = LayoutInflater.class.getDeclaredField("sConstructorMap");
                    sConstructorMap.setAccessible(true);
                    this.sConstructorMap = (HashMap<String, Constructor<? extends View>>) sConstructorMap.get(null);
                }
                Iterator<Map.Entry<String, Constructor<? extends View>>> iterator = sConstructorMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next().getValue().getName();
                    if (!name.startsWith("android.widget.")
                            && !name.startsWith("android.view.")
                            && !name.startsWith("android.webkit.")
                    ) {
                        iterator.remove();
                    }
                }
//                iterator = sConstructorMap.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    System.out.println("===================" + iterator.next().getValue().getName());
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
