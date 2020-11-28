package com.hfax.ucard.modules.debug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.BaseActivity;
import com.hfax.app.lock.LockScreenHelper;
import com.hfax.lib.network.RetrofitUtil;
import com.hfax.lib.utils.SPUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.R;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.widget.ClearEditText;
import com.hfax.ucard.widget.SimpleDialog;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by eson on 2017/8/3.
 */

public class HostSettingActivity extends BaseActivity {

    @BindView(R.id.iv_title_return)
    ImageView back;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.host)
    ClearEditText host;

    @BindView(R.id.et_auto)
    ClearEditText etAuto;
    @BindView(R.id.et_bg)
    ClearEditText etBg;
    @BindView(R.id.et_interval)
    ClearEditText etInterval;//去好评的时间设置输入框

    @BindView(R.id.env_title)
    TextView mEnvTitle;
    @BindView(R.id.hfax_debubg)
    CheckBox cb_debug;
    @BindView(R.id.rv_address)
    RecyclerView mRvAddress;
    @BindView(R.id.spinner_url_history)
    AppCompatSpinner mSpUrlHistory;

    private boolean mIsNeedCer = true; // 是否需要证书
    private List<HostSettingBean> mAddressUrl = new ArrayList<>();
    private ArrayList<String> mSpinnerList = new ArrayList<>();
    private MyHostSettingAdapter mHostSettingAdapter;
    private SharedPreferences mSp;
    private ArrayAdapter<String> mSpinnerAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_host_setting;
    }

    @Override
    public void initData() {
        tvTitle.setText("初始化设置");
        back.setVisibility(View.GONE);
        String s = SPUtils.getString(getApplicationContext(), "_host_", "");
        if (!TextUtils.isEmpty(s)) {
            host.setText(s);
        } else {
            host.setText(NetworkAddress.BASE_URL);
        }

        mEnvTitle.setText(mEnvTitle.getText().toString() + "(" + NetworkAddress.BASE_URL + ")");
//        etInterval.setText(FavorableCommentActivity.getInterval() + "");
        etInterval.setSelection(etInterval.length());
        etBg.setSelection(etBg.getText().toString().length());
        etAuto.setSelection(etAuto.getText().toString().length());

        initRecyclerView();
        initUrlHistory();
    }

    /**
     * 手动输入过的url历史记录初始化
     */
    private void initUrlHistory() {
        mSp = getSharedPreferences("spinnerBaseUrl", MODE_PRIVATE);

        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sharedPreferencesGetAllUrl());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpUrlHistory.setAdapter(mSpinnerAdapter);
    }

    /**
     * 清除所有
     */
    private void sharedPreferencesClear() {
        mSpinnerList.clear();
        mSpinnerAdapter.notifyDataSetChanged();
        mSp.edit().clear().apply();
    }

    /**
     * 获取所有的url
     *
     * @return
     */
    private ArrayList<String> sharedPreferencesGetAllUrl() {
        Map<String, String> all = (Map<String, String>) mSp.getAll();
        mSpinnerList.clear();
        if (all != null) {
            Collection<String> values = all.values();
            mSpinnerList.addAll(values);
        }
        return mSpinnerList;
    }

    /**
     * 保存url
     *
     * @param key
     * @param value
     */
    private void sharedPreferencesSaveUrl(String key, String value) {
        Map<String, String> spAll = (Map<String, String>) mSp.getAll();
        if (spAll != null) {
            spAll.remove(key);
            Map<String, String> map = new LinkedHashMap<>();
            map.put(key, value);

            map.putAll(spAll);
            Set<Map.Entry<String, String>> entrySet = map.entrySet();

            SharedPreferences.Editor edit = mSp.edit();
            edit.clear();
            for (Map.Entry<String, String> entry : entrySet) {
                edit.putString(entry.getKey(), entry.getValue());
            }
            edit.apply();
        }
    }

    private void initRecyclerView() {
        mAddressUrl = HostSettingBean.getAddressUrl();
        mRvAddress.setNestedScrollingEnabled(false);
        mRvAddress.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        mRvAddress.setLayoutManager(layoutManager);
        mHostSettingAdapter = new MyHostSettingAdapter(mAddressUrl);
        mRvAddress.setAdapter(mHostSettingAdapter);
    }

    @Override
    public void initListener() {
        if (!BuildConfig.DEBUG) {
            finish();
            return;
        }
        cb_debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsNeedCer = !isChecked;
            }
        });
        mHostSettingAdapter.setOnItemClickListener(new OnHostItemClickListener() {

            @Override
            public void onItemClick(View view, Object o) {
                if (o instanceof HostSettingBean) {
                    HostSettingBean hostSettingBean = (HostSettingBean) o;
                    goHome(hostSettingBean.url);
                }
            }
        });
        mSpUrlHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                host.setText(mSpinnerList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.sure, R.id.hfax_yanzheng, R.id.tv_spinner_clear})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                String h = host.getText().toString();
                if (TextUtils.isEmpty(h)) {
                    showToast("请输入地址");
                    return;
                }
                SPUtils.putString(getApplicationContext(), "_host_", h);
                if (!h.startsWith("http")) {
                    h = "https://" + h;
                } else if (!h.endsWith("/")) {
                    h += "/";
                }
                Utils.hideInputMethod(this);
                goHome(h);
                break;
            case R.id.hfax_yanzheng:
                goHome("https://172.16.5.68/");
                break;
//            case R.id.tv_reset_favorable://清除卡券过期缓存
////                AgreementActivty.clear();
//                initData();//清理之后重新刷新界面的默认值
//                showToast("已清除！");
//                break;
            case R.id.tv_spinner_clear://清除url历史记录
                SimpleDialog hfaxDialog = new SimpleDialog(this);
                hfaxDialog.setLeftButton("取消", null);
                hfaxDialog.setRightButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesClear();
                        showToast("已清除！");
                    }
                });
                hfaxDialog.setTitle("警告");
                hfaxDialog.setMessage("你要删除Url历史记录吗？");
                hfaxDialog.show();
                break;
            default:
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    String host = tv.getText().toString();
                    if (!TextUtils.isEmpty(host)) {
                        goHome("https://" + host.toLowerCase() + ".hfax.com/");
                    }
                }
        }
    }

    private void goHome(String s) {
        updateTime();
        if (s.startsWith("http://") || s.startsWith("https://")) {
            if (!s.endsWith("/")) {
                s += "/";
            }
            sharedPreferencesSaveUrl(s, s);
            NetworkAddress.BASE_URL = s;
            RetrofitUtil.resetBaseUrl(NetworkAddress.BASE_URL, s.contains("beneucard.com") && mIsNeedCer);
            String url = DebugUtil.getLocalUrl();
            if (!NetworkAddress.BASE_URL.equals(url)) {
                UserModel.logout();
            }
            DebugUtil.saveLocalUrl(NetworkAddress.BASE_URL);
            showToast("选择环境：" + NetworkAddress.BASE_URL);
            try {
                JSONObject properties = new JSONObject();
                properties.put("location_host", NetworkAddress.BASE_URL);
                SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Utils.hideInputMethod(this);
            finish();
        } else {
            showToast("请输入地址请以http:// 或者 https:// 开头");
        }

    }

    private void updateTime() {
        String auto = etAuto.getText().toString();
        if (!TextUtils.isEmpty(auto)) {
            if (Utils.isNumeric(auto)) {
                LockScreenHelper.setAutoLockTime(Integer.valueOf(auto) * 1000);
            }
        }
        String bg = etBg.getText().toString();
        if (!TextUtils.isEmpty(bg)) {
            if (Utils.isNumeric(bg)) {
                LockScreenHelper.setAppInBackgroundLockTime(Integer.valueOf(bg) * 1000);
            }
        }

        //修改去好评弹窗的默认系数
//        FavorableCommentActivity.setInterval(this, etInterval.getText().toString());
    }

    @Override
    protected boolean isStartSupportGestureFinish() {
        return false;
    }

    @Override
    protected boolean isNeedNoNetworkProcess() {
        return false;
    }

    static class MyHostSettingAdapter extends RecyclerView.Adapter<MyHostSettingAdapter.HostSettingViewHolder> implements View.OnClickListener {
        private List<HostSettingBean> mAddressList = new ArrayList<>();

        MyHostSettingAdapter(List<HostSettingBean> mAddressList) {
            this.mAddressList = mAddressList;
        }

        @Override
        public HostSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setBackgroundResource(R.drawable.shape_homepage_recommand_wealth_textview_bg);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            textView.setTextColor(Color.parseColor("#333333"));
            textView.setPadding(10, Utils.dip2px(parent.getContext(), 10), 10, Utils.dip2px(parent.getContext(), 10));
            textView.setOnClickListener(this);
            return new HostSettingViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(HostSettingViewHolder holder, int position) {
            HostSettingBean hostSettingBean = mAddressList.get(position);
            if (holder != null && hostSettingBean != null) {
                holder.itemView.setTag(position);
                holder.mTvName.setText(hostSettingBean.name);
            }
        }

        @Override
        public int getItemCount() {
            return mAddressList.size();
        }

        private OnHostItemClickListener mOnItemClickListener;

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, mAddressList.get((int) v.getTag()));
            }
        }

        public void setOnItemClickListener(OnHostItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        static class HostSettingViewHolder extends RecyclerView.ViewHolder {
            TextView mTvName;

            HostSettingViewHolder(TextView textView) {
                super(textView);
                mTvName = textView;
            }
        }
    }

    public interface OnHostItemClickListener {
        void onItemClick(View view, Object o);
    }

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, HostSettingActivity.class));

    }

}
