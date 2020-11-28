package com.hfax.ucard.modules.borrow;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hfax.app.BaseFragment;
import com.hfax.ucard.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 打款中状态
 *
 * @author SongGuangYao
 */
public class MakeMoneyFragment extends BaseFragment {
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_make_money;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.iv_return, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                getActivity().finish();
                break;
            case R.id.tv_next:
                getActivity().finish();
                if(getArguments()!=null){
                    String applyNo = getArguments().getString("applyNo");
                    if(!TextUtils.isEmpty(applyNo)){
                        BorrowDetailsActivity.start(getActivity(),applyNo);
                    }
                }
                break;
        }
    }
}
