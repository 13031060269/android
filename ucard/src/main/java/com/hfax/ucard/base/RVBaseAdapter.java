package com.hfax.ucard.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * RecycleView 万能适配器
 * Created by sgy on 2017/1/13.
 */
public abstract class RVBaseAdapter<T> extends RecyclerView.Adapter<RVBaseAdapter.VH> {

    private List<T> data;

    public RVBaseAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return VH.get(parent, getLayoutId(viewType));
    }

    public abstract int getLayoutId(int viewType);

    public abstract void convert(VH holder, T data, int position);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        convert(holder, data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mContentView;
        private Context mContext;

        public VH(View itemView, Context context) {
            super(itemView);
            mContentView = itemView;
            mViews = new SparseArray<>();
            mContext = context;

        }

        public static VH get(ViewGroup parent, int id) {
            View view = LayoutInflater.from(parent.getContext()).inflate(id, parent, false);
            Log.e("info", view.toString());
            return new VH(view, parent.getContext());
        }

        public <T extends View> T getView(int id) {
            View v = mViews.get(id);
            if (v == null) {
                v = mContentView.findViewById(id);
                mViews.put(id, v);
            }
            return (T) v;
        }

        public VH setText(int id, String text) {
            TextView tv = getView(id);
            tv.setText(text);
            return this;
        }

        public VH setText(int viewId, CharSequence text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }

        public VH setTextSize(int viewId, int size) {
            TextView view = getView(viewId);
            view.setTextSize(size);
            return this;
        }

        public VH setTextColor(int viewId, int color) {
            TextView view = getView(viewId);
            view.setTextColor(color);
            return this;
        }

        public VH setTextColorResource(int viewId, int colorRes) {
            TextView view = getView(viewId);
            view.setTextColor(mContext.getResources().getColor(colorRes));
            return this;
        }

        public VH setBackgroundColor(int viewId, int color) {
            getView(viewId).setBackgroundColor(color);
            return this;
        }

        public VH setBackgroundResource(int viewId, int resId) {
            getView(viewId).setBackgroundResource(resId);
            return this;
        }

        public VH setImageDrawable(int viewId, Drawable drawable) {
            ImageView view = getView(viewId);
            view.setImageDrawable(drawable);
            return this;
        }

        public VH setImageResource(int viewId, int imageRes) {
            ImageView view = getView(viewId);
            view.setImageResource(imageRes);
            return this;
        }


        public VH setImageBitmap(int viewId, Bitmap bitmap) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bitmap);
            return this;
        }

        public VH setAlpha(int viewId, float value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getView(viewId).setAlpha(value);
            } else {
                // Pre-honeycomb hack to set Alpha value
                AlphaAnimation alpha = new AlphaAnimation(value, value);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                getView(viewId).startAnimation(alpha);
            }
            return this;
        }

        public VH setVisible(int viewId, boolean visible) {
            View view = getView(viewId);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }

        public VH setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            view.setOnClickListener(listener);
            return this;
        }

        public VH setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            view.setOnLongClickListener(listener);
            return this;
        }

        public VH setTag(int viewId, Object tag) {
            View view = getView(viewId);
            view.setTag(tag);
            return this;
        }

        public VH setTag(int viewId, int key, Object tag) {
            View view = getView(viewId);
            view.setTag(key, tag);
            return this;
        }

        public VH setChecked(int viewId, boolean checked) {
            Checkable view = getView(viewId);
            view.setChecked(checked);
            return this;
        }

    }
}
