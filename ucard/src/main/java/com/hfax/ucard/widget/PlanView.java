package com.hfax.ucard.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.utils.UCardUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * 还款计划
 */

public class PlanView extends LinearLayout {
    @BindView(R.id.ll_open)
    LinearLayout ll_open;
    @BindView(R.id.cb_repayment_plan)
    CheckBox cb_repayment_plan;

    public PlanView(Context context) {
        super(context);
    }

    public PlanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(View.GONE);
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.layout_repay_plan, this);
        ButterKnife.bind(this, this);
        cb_repayment_plan.setChecked(false);
        ll_open.setVisibility(View.GONE);
    }

    @OnCheckedChanged({R.id.cb_repayment_plan})
    void onCheckChange(CompoundButton button, boolean isCheck) {
        if (isCheck) {
            ll_open.setVisibility(View.VISIBLE);
        } else {
            ll_open.setVisibility(View.GONE);
        }
    }

    public void setPlan(List<BorrowDetails.OrderInfoVo> plans) {
        setVisibility(View.VISIBLE);
        ll_open.removeAllViews();
        if (UCardUtil.isCollectionEmpty(plans)) return;
        int index = 1;
        for (BorrowDetails.OrderInfoVo plan : plans) {
            if (plan != null) {
                View view = inflate(getContext(), R.layout.item_plan, null);
                new ViewHold(view).update(plan, index);
                index++;
            }
        }
    }

    class ViewHold {
        @BindView(R.id.tv_plan_name)
        TextView tv_plan_name;
        @BindView(R.id.tv_plan_money)
        TextView tv_plan_money;
        @BindView(R.id.tv_plan_date)
        TextView tv_plan_date;
        @BindView(R.id.tv_plan_money_month)
        TextView tv_plan_money_month;

        ViewHold(View view) {
            ll_open.addView(view);
            ButterKnife.bind(this, view);
        }

        void update(BorrowDetails.OrderInfoVo plan, int index) {
            tv_plan_name.setText("订单" + index);
            tv_plan_money.setText(UCardUtil.formatAmount(plan.loanAmount));
            tv_plan_date.setText(plan.periods + "");
            tv_plan_money_month.setText(UCardUtil.formatAmount(plan.repayMonth));
        }
    }
}
