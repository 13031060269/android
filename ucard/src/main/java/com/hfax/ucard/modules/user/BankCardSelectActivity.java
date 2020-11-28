package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hfax.app.BaseActivity;
import com.hfax.lib.utils.GsonUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.GlobalConfigBean.Unit;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GlobalConfigUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class BankCardSelectActivity extends BaseNetworkActivity {
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.tv_title)
    TextView tv_title;
    private String from = BankCardBean.TYPE_DEBIT;
    private static final String KEY_FROM = "from";
    public static final int KEY_REQUEST_CODE = 100;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_select_bank_card;
    }

    @Override
    public void initData() {
        tv_title.setText("选择银行卡");
        if (getIntent().hasExtra(KEY_FROM)) {
            from = getIntent().getStringExtra(KEY_FROM);
        }
        final List<Unit> banks;
        switch (from) {
            case BankCardBean.TYPE_CREDIT:
                banks = GlobalConfigUtils.getGlobalConfig().creditBank;
                break;
            default:
            case BankCardBean.TYPE_DEBIT:
                banks = GlobalConfigUtils.getGlobalConfig().debitBank;
                break;
        }
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return banks.size();
            }

            @Override
            public Unit getItem(int position) {
                return banks.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHold vh;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_select_bank_card, null);
                    vh = new ViewHold(convertView);
                    convertView.setTag(vh);
                } else {
                    vh = (ViewHold) convertView.getTag();
                }
                vh.update(position);
                return convertView;
            }

            class ViewHold {
                ImageView iv_logo;
                TextView tv_bank_name;

                ViewHold(View view) {
                    iv_logo = (ImageView) view.findViewById(R.id.iv_logo);
                    tv_bank_name = (TextView) view.findViewById(R.id.tv_bank_name);
                }

                void update(int position) {
                    Unit item = getItem(position);
                    tv_bank_name.setText(item.value);
                    GlideUtils.requestImageCode(BankCardSelectActivity.this, UCardUtil.getBankCardLogo(item.key, false), iv_logo);
                }

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent resultData = new Intent();
                resultData.putExtra(UCardConstants.COMMON_RESULT_DATA, GsonUtils.bean2Json(banks.get(position)));
                setResult(UCardConstants.COMMON_RESULT_CODE, resultData);
                finish();
            }
        });

    }

    public static void start(Context context, String from) {
        Intent intent = new Intent(context, BankCardSelectActivity.class).putExtra(KEY_FROM, from);
        UCardUtil.startActivity(context, intent, KEY_REQUEST_CODE);

    }

    @Override
    public void initListener() {

    }

    public void start() {

    }


    @OnClick({R.id.iv_title_return, R.id.tv_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.tv_title:
                break;
        }
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
