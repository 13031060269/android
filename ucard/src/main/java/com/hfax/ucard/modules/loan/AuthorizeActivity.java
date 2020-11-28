package com.hfax.ucard.modules.loan;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.UserStatusBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.loan.adapter.AuthAdapter;
import com.hfax.ucard.bean.AuthBean;
import com.hfax.ucard.bean.AuthUrlBean;
import com.hfax.ucard.modules.user.AddBankCardActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.HfaxScrollView;
import com.hfax.ucard.widget.RectangleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 授权 Activity
 *
 * @author SongGuangyao
 * @date 2018/5/2
 */

public class AuthorizeActivity extends BaseNetworkActivity<AuthUrlBean> {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.v_title_divider_)
    View vTitleDivider;
    @BindView(R.id.rlv_bg)
    RectangleView rlvBg;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.iv_info)
    ImageView ivInfo;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.hsl_content)
    HfaxScrollView hslContent;


    @BindView(R.id.ll_need)
    LinearLayout llNeed;
    @BindView(R.id.ll_choose)
    LinearLayout llChoose;
    @BindView(R.id.bankcard_haseno)
    View bankcard_haseno;//无信用卡的ui
    @BindView(R.id.bankcard_hase)
    View bankcard_hase;//有信用卡的ui
    @BindView(R.id.bank_head)
    ImageView bank_head;
    @BindView(R.id.bank_name)
    TextView bank_name;
    @BindView(R.id.bank_num)
    TextView bank_num;
    BankCardBean bankCard;


    //用于标识请求最新数据的时候来源是否为下一步
    private boolean isToNext = false;


    //授权list
    private List<AuthBean.ItemsBean> authList = new ArrayList<>();
    //必须授权list
    private List<AuthBean.ItemsBean> needList = new ArrayList<>();
    //选填授权list
    private List<AuthBean.ItemsBean> chooseList = new ArrayList<>();


    private AuthAdapter authAdapter;

    public static final String MOBILE = "MOBILE";

    @Override
    public void initData() {
        GrowingIOUtils.track(UCardConstants.ANDR_AUZ_PAGE);
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_AUZ_PAGE, null);
        tvTitle.setText("个人信息");
        vTitleDivider.setVisibility(View.GONE);
        ivInfo.setImageResource(R.drawable.icon_steps2_pressed);
        tvInfo.setTextColor(getResources().getColor(R.color.person_info_selected_color));

        //用于意外关闭时候，状态数据未保存，恢复到首页
        if (UserStatusBean.getBean() == null) {
            MainActivity.start(this);
            finish();
            return;
        }

        //设置头部背景
        View view = findViewById(R.id.ic_tip);
        view.setBackgroundResource(R.drawable.icon_selectpoint2);

        ViewGroup.LayoutParams params = rlvBg.getLayoutParams();
        int screenWidth = Utils.getScreenWidth(this);
        //每个格的宽度  = （屏幕宽度 - 边缘宽度*2 - 中间空格宽度*2）/3;
        int unit = (screenWidth - Utils.dip2px(this, 16 * 2 + 18 * 2)) / 3;
        params.width = unit * 2 + Utils.dip2px(this, 18 + 8);
        rlvBg.setLayoutParams(params);

        authAdapter = new AuthAdapter(this);
        authAdapter.addOnItemClickListener(new AuthAdapter.OnItemClickListener() {
            @Override
            public void onClick(String type) {
                if (MOBILE.equals(type)) {
                    GrowingIOUtils.track(UCardConstants.ANDR_AUZ_PHONE_CLICK);
                    GrowingIOUtils.trackSDA(UCardConstants.UCARD_PHONENUMBER_CLICK);
                }
                queryUrl(type);
            }
        });
    }


    @Override
    protected void onLoad() {
        super.onLoad();
        showLoadingDialog();
        loadData();
    }

    /**
     * 加载应用授权数据
     */
    private void loadData() {
        mNetworkAdapter.request(NetworkAddress.QUERY_AUTH_STATUS, MVPUtils.Method.GET, new SimpleViewImpl<AuthBean>() {

            @Override
            public void onSuccess(AuthBean authBean) {
                dismissLoadingDialog();
                processData(authBean);
            }

            @Override
            public void onFail(int code, String msg) {
                dismissLoadingDialog();
                showErrorView();
            }
        });
    }

    /**
     * 处理数据
     *
     * @param authBean
     */
    private void processData(AuthBean authBean) {
        List<AuthBean.ItemsBean> items = authBean.items;
        if (items != null) {
            authList.clear();
            needList.clear();
            chooseList.clear();
            authList.addAll(items);
            //进行分类
            for (AuthBean.ItemsBean bean : items) {
                if (bean.required) {
                    needList.add(bean);
                } else {
                    chooseList.add(bean);
                }
            }
            authAdapter.configViews(needList, llNeed);
            authAdapter.configViews(chooseList, llChoose);
            llChoose.setVisibility(View.VISIBLE);
            bankCard = authBean.creditCard;
            if (bankCard != null) {
                bankcard_haseno.setVisibility(View.GONE);
                bankcard_hase.setVisibility(View.VISIBLE);
                GlideUtils.requestImageCode(this, UCardUtil.getBankCardLogo(bankCard.bankCode, false), bank_head);
                bank_name.setText(bankCard.bankName);
                int length = bankCard.cardNo.length();
                if (length >= 4) {
                    bank_num.setText("尾号" + bankCard.cardNo.substring(length - 4, length));
                }
            } else {
                bankcard_haseno.setVisibility(View.VISIBLE);
                bankcard_hase.setVisibility(View.GONE);
            }

        }
        hslContent.setVisibility(View.VISIBLE);
        showContentView();
        checkData();
        checkToNext();
    }


    /**
     * 如果是来自下一步点击的时候
     */
    private void checkToNext() {
        if (isToNext) {
            int isComplete = 0;
            int size = needList.size();
            for (int i = 0; i < size; i++) {
                if (TextUtils.equals(UCardConstants.DONE, needList.get(i).status)) {
                    isComplete++;
                }
            }
            if (isComplete == size) {
                LoanActivity.start(AuthorizeActivity.this, bankCard);
            } else {
                showToast("授权处理中，请稍后重试");
            }
            isToNext = false;
        }
    }


    @Override
    public void onSuccess(AuthUrlBean bean) {
        dismissLoadingDialog();
        UserStatusBean.getBean().setCreditAuth();
        //如果是需要授权
        if (bean.needAuth && !TextUtils.isEmpty(bean.url)) {
            H5Activity.startActivity(this, bean.url);
        } else {//不需要授权 或者Url为空
            loadData();
        }
    }


    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_authorzie;
    }

    @OnClick({R.id.iv_title_return, R.id.tv_next, R.id.bankcard_haseno, R.id.bank_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.bankcard_haseno:
            case R.id.bank_update:
                AddBankCardActivity.start(this, BankCardBean.TYPE_CREDIT);
                break;
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_AUZ_NEXT_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_AUZ_NEXT_CLICK, null);
                nextToPage();
                break;
        }
    }

    /**
     * 去下一页
     */
    private void nextToPage() {
        isToNext = true;
        onLoad();
    }


    /**
     * 查询验证
     *
     * @return
     */
    private void checkData() {
        int isComplete = 0;
        int size = needList.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(UCardConstants.DONE, needList.get(i).status) || TextUtils.equals(UCardConstants.PROCESSING, needList.get(i).status)) {
                isComplete++;
            }
        }
        //2 说明 状态为 done 、 processing 、 两者混合
        if (isComplete == size) {
            tvNext.setEnabled(true);
        } else {
            tvNext.setEnabled(false);
        }

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AuthorizeActivity.class);
        UCardUtil.startActivity(context, intent);
    }

    /**
     * 开启activity
     *
     * @param clazz
     */
    private void startActivityForResults(Class clazz) {
        if (clazz == null) {
            return;
        }
        startActivity(new Intent(AuthorizeActivity.this, clazz));
    }

    /**
     * 查询对应路径
     *
     * @param type
     */
    public void queryUrl(String type) {
        showLoadingDialog();
        RequestMap map = new RequestMap(NetworkAddress.QUERY_AUTH_URL);
        map.put("auth-type", type);
        mNetworkAdapter.request(map, MVPUtils.Method.GET);

    }
}
