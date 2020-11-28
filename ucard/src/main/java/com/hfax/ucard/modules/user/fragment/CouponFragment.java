package com.hfax.ucard.modules.user.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import butterknife.BindView;
import com.hfax.app.BaseFragment;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.CouponDetailBean;
import com.hfax.ucard.modules.user.adapter.CouponAdapter;
import com.hfax.ucard.utils.UCardUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡券
 */
public class CouponFragment extends BaseFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_coupon;
    }

    @BindView(R.id.ll_empty)
    View ll_empty;
    @BindView(R.id.lv_borrow)
    ListView lv_borrow;

    @Override
    protected void initData() {
    }

    private static final String LIST = "list";
    List<CouponDetailBean.CouponDetail> list;

    @Override
    protected void initListener() {
        list = (List<CouponDetailBean.CouponDetail>) getArguments().getSerializable(LIST);
        if (!UCardUtil.isCollectionEmpty(list)) {
            ll_empty.setVisibility(View.GONE);
            lv_borrow.setAdapter(new CouponAdapter(list));
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    public static CouponFragment create(List<CouponDetailBean.CouponDetail> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        CouponFragment result = new CouponFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LIST, new ArrayList<>(list));
        result.setArguments(bundle);
        return result;
    }
}
