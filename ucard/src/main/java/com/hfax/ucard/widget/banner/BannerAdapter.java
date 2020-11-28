package com.hfax.ucard.widget.banner;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hfax.app.utils.ProtocolUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.BannerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongGuangYao on 2018/11/5.
 */

public class BannerAdapter extends PagerAdapter implements View.OnClickListener {
    private List<BannerBean> datas = new ArrayList<>();
    private Context context;

    public BannerAdapter(Context context, List<BannerBean> datas) {
        if (datas == null) {
            throw new RuntimeException("数据集合不能为空");
        }
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (datas.size() > 1) {
            return Integer.MAX_VALUE;
        }
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (container == null) {
            return null;
        }

        BannerBean banner = datas.get(position % datas.size());
        BannerImageView imageView = new BannerImageView(context);
        //初始化数据
        Glide.with(context).load(banner.imageUrl).placeholder(R.drawable.icon_placeholder).into(imageView);

        // 设置点击事件
        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setTag(banner);
        imageView.setOnClickListener(this);

        container.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public void onClick(View v) {
        if (context != null) {
            BannerBean banner = (BannerBean) v.getTag();
            if (banner != null) {
                Intent intent = ProtocolUtils.parseProtocol(context, banner.redirectLink);
                if (intent != null) {
                    context.startActivity(intent);
                }
            }
        }
    }
}
