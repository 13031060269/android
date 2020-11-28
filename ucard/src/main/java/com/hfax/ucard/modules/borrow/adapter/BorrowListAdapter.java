package com.hfax.ucard.modules.borrow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.BaseActivity;
import com.hfax.app.h5.H5DepositActivity;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.BorrowBean;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.RepetitionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class BorrowListAdapter extends BaseAdapter {
    private List<BorrowBean> borrowBeans;
    private BaseActivity activity;

    public BorrowListAdapter(List<BorrowBean> borrowBeans) {
        this.borrowBeans = borrowBeans;
    }

    @Override
    public int getCount() {
        return borrowBeans.size();
    }

    @Override
    public BorrowBean getItem(int position) {
        return borrowBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHold vh;
        if (activity == null) {
            activity = (BaseActivity) parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_borrow_borrow, parent, false);
            vh = new ViewHold();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHold) convertView.getTag();
        }
        vh.update(position);
        return convertView;
    }

    class ViewHold {
        @BindView(R.id.tv_goto_repayment)
        TextView tv_goto_repayment;
        @BindView(R.id.tv_withdraw)
        TextView tv_withdraw;
        @BindView(R.id.tv_withdrawing)
        TextView tv_withdrawing;
        @BindView(R.id.tv_state)
        TextView tv_state;
        @BindView(R.id.tv_value)
        TextView tv_value;
        @BindView(R.id.tv_msg)
        TextView tv_msg;
        @BindView(R.id.tv_value_text)
        TextView tv_value_text;
        @BindView(R.id.iv_state)
        ImageView iv_state;
        @BindView(R.id.root)
        View root;
        @BindView(R.id.overdue)
        View overdue;
        @BindView(R.id.root_state)
        View root_state;
        BorrowBean borrowBean;

        void update(int position) {
            borrowBean = getItem(position);
            root_state.setVisibility(View.VISIBLE);
            tv_goto_repayment.setVisibility(View.GONE);
            tv_withdraw.setVisibility(View.GONE);
            tv_withdrawing.setVisibility(View.GONE);
            if (borrowBean.isOverdueCheck) {//逾期
                overdue.setVisibility(View.VISIBLE);
            } else {
                overdue.setVisibility(View.GONE);
            }
            tv_value.setText("¥ " + UCardUtil.formatAmount(borrowBean.loanAmount));
            int color = 0xff363349;
            int drawableId = R.drawable.icon_circle_state_4;
            String titleName = "借款金额";
            switch (borrowBean.orderStatus) {
                default:
                case BorrowBean.STATE_REPAYMENT:
                    drawableId = R.drawable.icon_circle_state_1;
                    break;
                case BorrowBean.STATE_WITHDRAW:
                    drawableId = R.drawable.icon_circle_state_1;
                    if (borrowBean.isWithdraw == 997 && borrowBean.amountToBeWithdrawn > 0) {
                        tv_withdraw.setVisibility(View.VISIBLE);
                        tv_withdraw.setText("您还有 " + UCardUtil.formatAmount(borrowBean.amountToBeWithdrawn) + " 可提现   马上提现 ");
                    } else if (borrowBean.isWithdraw == 998) {
                        tv_withdrawing.setVisibility(View.VISIBLE);
                    }
                    break;
                case BorrowBean.STATE_AFFIRM_NEED:
                    root_state.setVisibility(View.GONE);
                    tv_goto_repayment.setVisibility(View.VISIBLE);
                    break;
                case BorrowBean.STATE_PAYING:
                    drawableId = R.drawable.icon_circle_state_2;
                    break;
                case BorrowBean.STATE_CHECKING:
                    titleName = "申请金额";
                    drawableId = R.drawable.icon_circle_state_3;
                    tv_value.setText("¥ " + UCardUtil.formatAmount(borrowBean.applyAmount));
                    break;
                case BorrowBean.STATE_NOPASS:
                    titleName = "申请金额";
                    color = 0xffB1B1B6;
                    tv_value.setText("¥ " + UCardUtil.formatAmount(borrowBean.applyAmount));
                    break;
                case BorrowBean.STATE_RETURN:
                    color = 0xffB1B1B6;
                    if (borrowBean.isWithdraw == 997 && borrowBean.amountToBeWithdrawn > 0) {
                        tv_withdraw.setVisibility(View.VISIBLE);
                        tv_withdraw.setText("您还有 " + UCardUtil.formatAmount(borrowBean.amountToBeWithdrawn) + " 可提现   马上提现 ");
                    } else if (borrowBean.isWithdraw == 998) {
                        tv_withdrawing.setVisibility(View.VISIBLE);
                    }
                    break;
                case BorrowBean.STATE_TIMEOUT:
                    color = 0xffB1B1B6;
                    break;
                case BorrowBean.STATE_FAIL:
                    color = 0xffB1B1B6;
                    break;
            }
            tv_state.setText(borrowBean.orderStatusName);
            tv_state.setTextColor(color);
            iv_state.setBackgroundResource(drawableId);
            tv_msg.setText("申请借款日 " + borrowBean.applyDate);
            tv_value_text.setText(titleName);
        }

        @OnClick({R.id.root, R.id.tv_withdraw})
        void onClick(View view) {
            if(PreventClickUtils.canNotClick(view))return;
            switch (view.getId()) {
                case R.id.root:
                    BorrowDetailsActivity.start(activity, borrowBean.applyNo + "");
                    break;
                case R.id.tv_withdraw:
                    final RequestMap map = new RequestMap(NetworkAddress.WITHDRAW);
                    map.put("applyNo", borrowBean.applyNo);
                    map.put("amountToBeWithdraw", borrowBean.amountToBeWithdrawn);
                    activity.showLoadingDialog();
                    RepetitionUtils.getRepetition().submit(map, activity, new DataChange<Boolean>() {
                        @Override
                        public void onChange(Boolean aBoolean) {
                            activity.dismissLoadingDialog();
                            if (aBoolean) {
                                H5DepositActivity.startDepositActivity(activity, Utils.getApiURL(map.getPath()), "", Utils.getRequestParams(map));
                            }
                        }
                    });
                    break;
            }
        }
    }
}
