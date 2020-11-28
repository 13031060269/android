package com.hfax.ucard.modules.borrow.adapter;

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
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.widget.CardChangeDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class AffirmBankCardAdapter extends BaseAdapter {
    public enum Type {
        none, repay, borrow
    }

    Type type = Type.none;
    DataChange<BankCardBean> changeListener;
    final private List<BankCardBean> bankCardBeans;
    int curPosition = 0;

    public AffirmBankCardAdapter(List<BankCardBean> bankCardBeans) {
        this(bankCardBeans, Type.none);
    }

    public AffirmBankCardAdapter(List<BankCardBean> bankCardBeans, Type type) {
        if (bankCardBeans == null) {
            bankCardBeans = new ArrayList<>();
        }
        this.bankCardBeans = bankCardBeans;
        this.type = type;
    }

    public AffirmBankCardAdapter(Type type) {
        this(null, type);
    }

    public BankCardBean getCurBankCard() {
        if (UCardUtil.isCollectionEmpty(bankCardBeans) || curPosition >= bankCardBeans.size()) {
            return null;
        }
        return getItem(curPosition);
    }

    public void setData(List<BankCardBean> bankCardBeans) {
        this.bankCardBeans.clear();
        curPosition=0;
        if (!UCardUtil.isCollectionEmpty(bankCardBeans)) {
            this.bankCardBeans.addAll(bankCardBeans);
            for (int i = 0; i < bankCardBeans.size(); i++) {
                if (bankCardBeans.get(i).currentChoose) {
                    curPosition = i;
                    break;
                }
            }
        }
        if (changeListener != null) {
            changeListener.onChange(getCurBankCard());
        }
        notifyDataSetChanged();
    }

    public void setChangeListener(DataChange<BankCardBean> changeListener) {
        this.changeListener = changeListener;
        if (!UCardUtil.isCollectionEmpty(bankCardBeans)) {
            for (int i = 0; i < bankCardBeans.size(); i++) {
                if (bankCardBeans.get(i).currentChoose) {
                    curPosition = i;
                    break;
                }
            }
            if (changeListener != null) {
                notifyDataSetChanged();
                changeListener.onChange(getCurBankCard());
            }
        }
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
        ViewHold viewHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_card, parent, false);
            viewHold = new ViewHold(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.update(position);
        return convertView;
    }

    class ViewHold {
        @BindView(R.id.cb)
        CheckBox cb;
        @BindView(R.id.bank_logo)
        ImageView bank_logo;
        @BindView(R.id.bank_name)
        TextView bank_name;
        @BindView(R.id.bank_num)
        TextView bank_num;
        @BindView(R.id.ll_content)
        View ll_content;

        ViewHold(View view) {
            ButterKnife.bind(this, view);
        }

        void update(final int position) {
            final BankCardBean item = getItem(position);
            cb.setVisibility(View.GONE);
            if (curPosition == position) {
                cb.setChecked(true);
                ll_content.setOnClickListener(null);
                ll_content.setVisibility(View.GONE);
            } else {
                ll_content.setVisibility(View.VISIBLE);
                cb.setChecked(false);
                ll_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setChange(type,position);
                    }
                });
            }
            bank_name.setText(item.bankName);
            bank_num.setText("尾号" + UCardUtil.getBankNumLast(item.cardNo));
            GlideUtils.requestImageCode(bank_logo.getContext(), UCardUtil.getBankCardLogo(item.bankCode, false), bank_logo);
        }

        private void setChange(Type type, final int position) {
            BankCardBean item1 = getItem(curPosition);
            final CardChangeDialog cardChangeDialog;
            switch (type) {
                case none:
                    curPosition = position;
                    notifyDataSetChanged();
                    if (changeListener != null) {
                        changeListener.onChange(getItem(position));
                    }
                    break;

                case borrow:
                    cardChangeDialog =
                            new CardChangeDialog(ll_content.getContext()
                                    , "确认更换代还信用卡吗"
                                    , "现为" + item1.bankName + "\n" + item1.cardNo + "卡");
                    cardChangeDialog.setCanceledOnTouchOutside(true);
                    cardChangeDialog.setListener(new CardChangeDialog.OnClickListen() {
                        @Override
                        public void onClick() {
                            cardChangeDialog.dismiss();
                            setChange(Type.none, position);
                        }
                    });
                    cardChangeDialog.show();
                    break;
                case repay:
                    cardChangeDialog = new CardChangeDialog(ll_content.getContext(),ll_content.getContext().getString(R.string.change_card) );
                    cardChangeDialog.setCanceledOnTouchOutside(true);
                    cardChangeDialog.setListener(new CardChangeDialog.OnClickListen() {
                        @Override
                        public void onClick() {
                            cardChangeDialog.dismiss();
                            setChange(Type.none, position);
                        }
                    });
                    cardChangeDialog.show();
                    break;
            }
        }
    }
}
