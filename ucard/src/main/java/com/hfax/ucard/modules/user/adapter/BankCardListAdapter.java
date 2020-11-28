package com.hfax.ucard.modules.user.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class BankCardListAdapter extends BaseAdapter {
    private List<BankCardBean> bankCardBeans;

    public BankCardListAdapter(List<BankCardBean> bankCardBeans) {
        this.bankCardBeans = bankCardBeans;
    }

    @Override
    public int getCount() {
        return bankCardBeans.size();
    }

    @Override
    public BankCardBean getItem(int position) {
        return bankCardBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_bank_card, parent, false);
            vh = new ViewHold(convertView);
            convertView.setTag(vh);

        } else {
            vh = (ViewHold) convertView.getTag();
        }
        vh.update(position);
        return convertView;
    }

    class ViewHold {
        @BindView(R.id.bank_logo)
        ImageView bank_logo;
        @BindView(R.id.bank_logo_bg)
        ImageView bank_logo_bg;
        @BindView(R.id.bank_name)
        TextView bank_name;
        @BindView(R.id.bank_num)
        TextView bank_num;
        @BindView(R.id.tv_card_type)
        TextView tv_card_type;

        ViewHold(View view) {
            ButterKnife.bind(this, view);
        }

        void update(int position) {
            BankCardBean item = getItem(position);
            if (item == null) {
                return;
            }
            switch (item.cardType) {//卡类型 DEBIT：储蓄卡 CREDIT：信用卡
                default:
                case "DEBIT":
                    tv_card_type.setText("储蓄卡");
                    break;
                case "CREDIT":
                    tv_card_type.setText("信用卡");
                    break;
            }
            bank_name.setText(item.bankName);
            int length = item.cardNo.length();
            bank_num.setText(item.cardNo.substring(length - 4, length));
            GlideUtils.requestImageCode(bank_name.getContext(), UCardUtil.getBankCardLogo(item.bankCode, false), bank_logo);
            GlideUtils.requestImageCode(bank_name.getContext(), UCardUtil.getBankCardLogo(item.bankCode, true), bank_logo_bg);
        }
    }
}
