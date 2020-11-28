package com.hfax.ucard.modules.loan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.bean.AuthBean;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;

import java.util.List;

/**
 * 授权adapter
 * Created by SongGuangYao on 2018/8/28.
 */

public class AuthAdapter {

    private final LayoutInflater inflater;
    private Context context;
    private OnItemClickListener listener;

    public AuthAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 配置显示控件
     */
    public void configViews(List<AuthBean.ItemsBean> list, ViewGroup viewGroup) {
        int size = list.size();
        if (size == 0) {
            viewGroup.setVisibility(View.GONE);
            return;
        } else {
            viewGroup.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < size; i++) {

            AuthBean.ItemsBean itemsBean = list.get(i);
            //去除重复显示
            View viewWithTag = viewGroup.findViewWithTag(itemsBean.type);
            if (viewWithTag != null) {
                viewGroup.removeView(viewWithTag);
            }

            View view = inflater.inflate(R.layout.item_auth_content, viewGroup, false);
            view.setTag(itemsBean.type);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(v.getTag().toString());
                    }
                }
            });

            LinearLayout group = (LinearLayout) view.findViewById(R.id.ll_content);
            ImageView ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
            GlideUtils.requestImageCode(context, UCardUtil.getAuthLogo(itemsBean.iconId), ivLogo);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvTitle.setText(itemsBean.name);

            viewGroup.addView(view);

            showStatus(itemsBean.status, group);

            //添加分割线
            View lineView = viewGroup.findViewWithTag(itemsBean.type + "line");
            if (lineView != null) {
                viewGroup.removeView(lineView);
            }
            if (i != size - 1) {
                View inflate = inflater.inflate(R.layout.item_auth_segment, viewGroup, false);
                inflate.setTag(itemsBean.type + "line");
                viewGroup.addView(inflate);
            } else {
                View inflate = inflater.inflate(R.layout.item_auth_segment_line, viewGroup, false);
                inflate.setTag(itemsBean.type + "line");
                viewGroup.addView(inflate);
            }
        }
    }

    /**
     * 显示当前授权状态
     *
     * @param status    状态
     * @param viewGroup 对应条目布局
     */
    public void showStatus(String status, LinearLayout viewGroup) {
        //默认可以点击
        viewGroup.setClickable(true);
        if (viewGroup.getChildCount() == 4) {
            viewGroup.removeViewAt(2);
        }
        switch (status) {
            case UCardConstants.NOTAUTH:
                break;
            case UCardConstants.CREATETOKEN:
                break;
            case UCardConstants.SUSPENDED:
                break;
            case UCardConstants.PROCESSING://授权处理中
                viewGroup.setClickable(false);
                viewGroup.addView(inflater.inflate(R.layout.layout_authorized_processing, null), 2);
                break;
            case UCardConstants.DONE://完成
                //完成授权后不可以点击
                viewGroup.setClickable(false);
                viewGroup.addView(inflater.inflate(R.layout.layout_authorized_valid, null), 2);
                if (viewGroup.getChildCount() == 4) {
                    viewGroup.removeViewAt(3);
                }
                break;
            case UCardConstants.EXPIRED://失效
                viewGroup.addView(inflater.inflate(R.layout.layout_authorized_invalid, null), 2);
                break;
            case UCardConstants.FAILD:
                break;
        }
    }


    public interface OnItemClickListener {
        void onClick(String type);
    }

}
