package com.hfax.ucard.modules.borrow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.utils.UCardUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class PaymentDetailsAdapter extends BaseAdapter {
    private List<BorrowDetails.RepayPlanVo> beans = new ArrayList<>();
    private Context context;
    public List<BorrowDetails.Coupon> coupon;

    public PaymentDetailsAdapter() {
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public BorrowDetails.RepayPlanVo getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHold vh;
        if (context == null) {
            context = parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_payment_details, parent, false);
            vh = new ViewHold();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHold) convertView.getTag();
        }
        vh.update(position);
        return convertView;
    }

    public void resetDate(List<BorrowDetails.RepayPlanVo> repayPlanVos, List<BorrowDetails.Coupon> coupon) {
        this.coupon = coupon;
        beans.clear();
        beans.addAll(repayPlanVos);
        notifyDataSetChanged();
    }

    class ViewHold {
        @BindView(R.id.root)
        View root;
        @BindView(R.id.tv_fine_0)
        TextView tv_fine_0;
        @BindView(R.id.tv_fine_1)
        TextView tv_fine_1;
        @BindView(R.id.repayStatusName)
        TextView repayStatusName;
        @BindView(R.id.repayPlanDate)
        TextView repayPlanDate;
        @BindView(R.id.repayTotal)
        TextView repayTotal;
        BorrowDetails.RepayPlanVo bean;

        void update(int position) {
            tv_fine_0.setVisibility(View.GONE);
            tv_fine_1.setVisibility(View.GONE);
            bean = getItem(position);
            if (bean.selective) {
                root.setBackgroundResource(R.drawable.repaying_nored_pic);
            } else {
                root.setBackgroundResource(R.drawable.repaid_pic);
            }
            repayStatusName.setText(bean.repayStatusName);
            repayStatusName.setBackgroundColor(getColor(bean.repayStatus));
            if (bean.overdueTotal > 0) {
                tv_fine_0.setVisibility(View.VISIBLE);
                root.setBackgroundResource(R.drawable.repaid_pic2);
                String suffix = "";
                if (bean.deRateTotal > 0) {
                    suffix = ",减免" + UCardUtil.formatAmount(bean.deRateTotal);
                }
                tv_fine_0.setText("（含罚息 " + UCardUtil.formatAmount(bean.overdueTotal) + suffix + "）");
            } else if (bean.selective && !UCardUtil.isCollectionEmpty(coupon)) {
                tv_fine_1.setVisibility(View.VISIBLE);
                root.setBackgroundResource(R.drawable.repaying_pic);
                tv_fine_1.setText("红包已优惠" + UCardUtil.formatAmount(coupon.get(0).derateAmount) + "元");
            }
            repayPlanDate.setText(bean.repayPlanDate);
            if (bean.repayStatus == 2000) {
                repayPlanDate.setTextColor(0xFF98989E);
                repayTotal.setTextColor(0xFF98989E);
                repayTotal.setText(UCardUtil.formatAmount(bean.repayTotal));
            } else {
                repayPlanDate.setTextColor(0xFF363349);
                repayTotal.setTextColor(0xFF363349);
                repayTotal.setText(UCardUtil.formatAmount(bean.planRepayTotal));
            }
        }

        private int getColor(int state) {
            int color;
            switch (state) {//还款状态 1000-还款中 2000-已还款 3000-逾期中 1-还款处理中
                case 1:
                    color = 0xFFA696FF;
                    break;
                default:
                case 1000:
                    color = 0xFF6A91FE;
                    break;
                case 2000:
                    color = 0xFF97979C;
                    break;
                case 3000:
                    color = 0xFFF67958;
                    break;
            }
            return color;
        }
    }
}
