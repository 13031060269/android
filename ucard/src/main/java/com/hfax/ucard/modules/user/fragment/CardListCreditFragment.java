package com.hfax.ucard.modules.user.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkFragment;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.modules.user.adapter.BankCardListAdapter;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.DataChange;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class CardListCreditFragment extends BaseNetworkFragment implements DataChange<List<BankCardBean>> {
    @BindView(R.id.lv_card)
    ListView lvCard;
    @BindView(R.id.tv_msg)
    TextView tv_msg;
    @BindView(R.id.tv_title1)
    TextView tv_title1;
    @BindView(R.id.tv_title2)
    TextView tv_title2;
    @BindView(R.id.ll_content)
    View ll_content;
    List<BankCardBean> bankCardBeans = new ArrayList<>();

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_card_list_credit;
    }

    static final String title1 = "title1";
    static final String title2 = "title2";
    BankCardListAdapter adapter = new BankCardListAdapter(bankCardBeans);

    public static CardListCreditFragment create(String t1, String t2) {
        CardListCreditFragment cardListCreditFragment = new CardListCreditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(title1,t1);;
        bundle.putString(title2,t2);;
        cardListCreditFragment.setArguments(bundle);
        return cardListCreditFragment;
    }

    @Override
    protected void initData() {
        super.initData();
        lvCard.setAdapter(adapter);
        if(getArguments()!=null){
            String t1 = getArguments().getString(title1, null);
            String t2 = getArguments().getString(title2, null);
            if (!TextUtils.isEmpty(t1)) {
                tv_title1.setText(t1);
            }
            if (!TextUtils.isEmpty(t2)) {
                tv_title2.setText(t2);
            }
        }

//        footView = LayoutInflater.from(getActivity()).inflate(R.layout.card_list_foot_add, lvCardCredit, false);
//        lvCardCredit.addFooterView(footView);
//        footView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AddBankCardActivity.start(getActivity(),BankCardBean.TYPE_CREDIT);
//            }
//        });
    }

    @Override
    public void onChange(List<BankCardBean> bankCardBean) {
        bankCardBeans.clear();
        if (UCardUtil.isCollectionEmpty(bankCardBean)) {
            tv_msg.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
        } else {
            bankCardBeans.addAll(bankCardBean);
            tv_msg.setVisibility(View.GONE);
            ll_content.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
