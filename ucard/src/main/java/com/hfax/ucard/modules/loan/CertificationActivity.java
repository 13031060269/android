package com.hfax.ucard.modules.loan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfsdk.card.classify.sdk.DFCardSide;
import com.hfax.facelib.IDCardScanActivity;
import com.hfax.facelib.util.Constant;
import com.hfax.facelib.util.Util;
import com.hfax.facelib.utils.IDCardUtils;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.IDCardBean;
import com.hfax.ucard.bean.IDTypeBean;
import com.hfax.ucard.bean.UserStatusBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.bean.IdCardInfoBean;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.ConfirmPersonInfoDialog;
import com.hfax.ucard.widget.RectangleView;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.M;

/**
 * OCR认证
 *
 * @author SongGuangYao
 */
public class CertificationActivity extends BaseNetworkActivity<IDCardBean> {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rlv_bg)
    RectangleView rlvBg;
    @BindView(R.id.tv_subTitle)
    TextView tvSubTitle;
    @BindView(R.id.tv_sub_tip)
    TextView tvSubTip;
    @BindView(R.id.ll_tip_1)
    LinearLayout llTip1;
    @BindView(R.id.tv_info_tip)
    TextView tvInfoTip;
    @BindView(R.id.ll_top_2)
    LinearLayout llTop2;
    @BindView(R.id.iv_front)
    ImageView ivFront;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.iv_front_replay)
    ImageView ivFrontReplay;
    @BindView(R.id.iv_back_replay)
    ImageView ivBackReplay;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.fl_front)
    FrameLayout flFront;
    @BindView(R.id.fl_back)
    FrameLayout flBack;
    @BindView(R.id.tv_next)
    TextView tvNext;

    private static final int INTO_IDCARDSCAN_PAGE = 100;
    public static final int EXTERNAL_STORAGE_REQ_CAMERA_CODE = 10;
    private final int TYPE_FRONT = 0;
    private final int TYPE_BACK = 1;
    int mSide = TYPE_FRONT;
    @BindView(R.id.view_dot)
    View viewDot;
    private String uuid;
    boolean isVertical;
    /**
     * 入口来源
     */
    private String source;
    private String backSource;
    private String frontSource;
    private String portraitSource;
    //身份证验证返回实体
    private IDCardBean idCardBean;
    private View includeView;

    //是否授权完成
    private boolean isAuthed = false;
    //授权来源  初始化
    private final String SOURCE_ONCREATE = "SOURCE_ONCREATE";
    //下一步按钮
    private final String SOURCE_BTN = "SOURCE_BTN";
    IDTypeBean idType;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_certification;
    }

    @Override
    public void initData() {
        GrowingIOUtils.track(UCardConstants.ANDR_OCR_PAGE);
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_PAGE, null);
        tvTitle.setText("身份认证");
        viewDot.setVisibility(View.VISIBLE);
        initView();

        //获取来源
        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getStringExtra("source");
        }
        if (TextUtils.equals(source, UCardConstants.IDCARD_LOAN) && UserStatusBean.getBean() == null) {
            MainActivity.start(this);
            return;
        }
        includeView = findViewById(R.id.ic_tip);
        if (TextUtils.equals(source, UCardConstants.IDCARD_PERSON_CENTEL)) {
            includeView.setVisibility(View.GONE);
        }
        requestAuth(SOURCE_ONCREATE);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (idCardBean != null) {
                    if (TextUtils.isEmpty(s)) {
                        idCardBean.name = "";
                    } else {
                        idCardBean.name = s.toString();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    GrowingIOUtils.track(UCardConstants.ANDR_OCR_NAME_CHANGE);
                }
            }
        });
    }

    /**
     * 请求授权
     *
     * @param source 来源
     */
    private void requestAuth(final String source) {
        //OCR授权
        uuid = Util.getUUIDString(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Manager manager = new Manager(getApplicationContext());
                IDCardQualityLicenseManager idCardLicenseManager = new IDCardQualityLicenseManager(getApplicationContext());
                manager.registerLicenseManager(idCardLicenseManager);
                manager.takeLicenseFromNetwork(uuid);
                if (idCardLicenseManager.checkCachedLicense() > 0) {
                    //授权成功
                    UIAuthState(true, source);
                } else {
                    //授权失败
                    UIAuthState(false, source);
                }
            }
        }).start();
    }

    /**
     * 加载 View
     */
    private void initView() {
        //设置头部背景
        ViewGroup.LayoutParams params = rlvBg.getLayoutParams();
        int screenWidth = Utils.getScreenWidth(this);
        //每个格的宽度  = （屏幕宽度 - 边缘宽度*2 - 中间空格宽度*2）/3;
        int unit = (screenWidth - Utils.dip2px(this, 16 * 2 + 18 * 2)) / 3;
        params.width = unit;
        rlvBg.setLayoutParams(params);
    }

    @Override
    public void onSuccess(IDCardBean bean) {
        dismissLoadingDialog();
        if (!TextUtils.isEmpty(bean.idNo) && !TextUtils.isEmpty(bean.name)) {
            idCardBean = bean;
            etName.setText(bean.name);
            tvId.setText(bean.idNo);
            tvNext.setEnabled(true);
            UCardConstants.UCARD_OCR_UPLOAD ocr = new UCardConstants.UCARD_OCR_UPLOAD();
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_UPLOAD, ocr);
            ivFrontReplay.setVisibility(View.GONE);
            ivBackReplay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
        UCardConstants.UCARD_OCR_UPLOAD ocr = new UCardConstants.UCARD_OCR_UPLOAD();
        ocr.result = UCardConstants.UCARD_SDA_FAILED;
        ocr.error_type = msg;
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_UPLOAD, ocr);
    }


    /**
     * @param context 上下文
     * @param source  来源 UCardConstants   IDCARD_LOAN  IDCARD_PERSON_CENTEL
     */
    public static void start(Context context, String source) {
        Intent intent = new Intent(context, CertificationActivity.class);
        intent.putExtra("source", source);
        UCardUtil.startActivity(context, intent);
    }


    private void UIAuthState(final boolean isSuccess, final String source) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isAuthed = isSuccess;
                if (isAuthed) {
                    if (TextUtils.equals(source, SOURCE_ONCREATE)) {
                        LogUtil.e("info----", "OCR授权成功");
                    } else if (TextUtils.equals(source, SOURCE_BTN)) {
                        requestCameraPerm(mSide);
                    }
                } else {
                    showToast("授权失败,请退出重试");
                }
            }
        });
    }

    @OnClick({R.id.iv_title_return, R.id.fl_front, R.id.fl_back, R.id.tv_next, R.id.iv_front_replay, R.id.iv_back_replay})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.fl_front:
                GrowingIOUtils.track(UCardConstants.ANDR_OCR_FRONT_BUTTON_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_FRONT_BUTTON_CLICK);
                requestCameraPerm(TYPE_FRONT);
                break;
            case R.id.fl_back:
                GrowingIOUtils.track(UCardConstants.ANDR_OCR_CON_BUTTON_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_CON_BUTTON_CLICK);
                requestCameraPerm(TYPE_BACK);
                break;
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_OCR_NEXT_BUTTON_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_OCR_NEXT_BUTTON_CLICK, null);
                if (idCardBean == null) {
                    showToast("请上传身份证");
                } else if (TextUtils.isEmpty(idCardBean.name)) {
                    showToast("请填写姓名");
                } else {
                    //弹框
                    GrowingIOUtils.track(UCardConstants.ANDR_INSUREID_PAGE);
                    GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSUREID_PAGE, null);
                    ConfirmPersonInfoDialog infoDialog = new ConfirmPersonInfoDialog(this, idCardBean.name, idCardBean.idNo);
                    infoDialog.setOnClickListener(new ConfirmPersonInfoDialog.OnClickListen() {
                        @Override
                        public void cancel() {
                            GrowingIOUtils.track(UCardConstants.ANDR_INSUREID_N_CLICK);
                            GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSUREID_N_CLICK);
                        }

                        @Override
                        public void next() {
                            GrowingIOUtils.track(UCardConstants.ANDR_INSUREID_Y_CLICK);

                            uploadIdCardInfo();
                        }
                    });
                    infoDialog.show();
                    infoDialog.setCancelable(false);
                    infoDialog.setCanceledOnTouchOutside(false);
                }
                break;
            case R.id.iv_front_replay:
                requestCameraPerm(TYPE_FRONT);
                break;
            case R.id.iv_back_replay:
                requestCameraPerm(TYPE_BACK);
                break;
        }

    }

    /**
     * 更新用户信息
     */
    private void uploadIdCardInfo() {
        showLoadingDialog();
        RequestMap map = new RequestMap(NetworkAddress.ID_INFO);
        map.put("idCardNo", idCardBean.idNo);
        map.put("name", idCardBean.name);
        mNetworkAdapter.request(map, MVPUtils.Method.POST, new SimpleViewImpl<IdCardInfoBean>() {
            @Override
            public void onSuccess(IdCardInfoBean bean) {
                dismissLoadingDialog();
                if (UserStatusBean.getBean() != null) {
                    UserStatusBean.getBean().setIdCard();
                }
                if (TextUtils.equals(source, UCardConstants.IDCARD_LOAN)) {
                    FaceActivity.start(CertificationActivity.this);
                } else {
                    showToast("实名认证成功");
                    ActivityCallbackUtils.getInstance().execute(getIntent(), true);
                }
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSUREID_Y_CLICK);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                UCardConstants.UCARD_INSUREID_Y_CLICK click = new UCardConstants.UCARD_INSUREID_Y_CLICK();
                click.error_type = msg;
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSUREID_Y_CLICK, click);
                dismissLoadingDialog();
                showToast(msg);
            }
        });
    }

    /**
     * 检测是否上传完毕
     */
    public void checkFinish() {
        if (backSource != null && frontSource != null) {
            showLoadingDialog();
            RequestMap map = new RequestMap(NetworkAddress.ID_IMG);
            map.put("back", backSource);
            map.put("front", frontSource);
            map.put("header", portraitSource);
            mNetworkAdapter.request(map, MVPUtils.Method.POST);
        }
    }


    /**
     * 请求摄像头
     *
     * @param side TYPE_FRONT   TYPE_BACK
     */
    private void requestCameraPerm(int side) {
        if(idCardBean!=null)return;
        mSide = side;
        //未授权情况下请求授权
        if (!isAuthed) {
            requestAuth(SOURCE_BTN);
            return;
        }
        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, EXTERNAL_STORAGE_REQ_CAMERA_CODE);
            } else {
                enterNextPage(side);
            }
        } else {
            enterNextPage(side);
        }
    }

    /**
     * 身份证扫描页面
     *
     * @param side TYPE_FRONT   TYPE_BACK
     */
    private void enterNextPage(final int side) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("isvertical", isVertical + "");
                bundle.putInt("side", side);
                if (idType.type == 1) {
                    IDCardUtils.startFaceIDCard(getThis(), side, INTO_IDCARDSCAN_PAGE, bundle);
                } else {
                    IDCardUtils.startDFIDCard(getThis(), side, INTO_IDCARDSCAN_PAGE, bundle);
                }
            }
        };
        if (idType != null) {
            runnable.run();
            return;
        }
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.IDENTITY_TYPE, MVPUtils.Method.GET, new SimpleViewImpl<IDTypeBean>() {
            @Override
            public void onSuccess(IDTypeBean idTypeBean) {
                idType = idTypeBean;
                runnable.run();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                    }
                }, 1000);
            }

            @Override
            public void onFail(int code, String msg) {
                dismissLoadingDialog();
                showToast(msg);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                    Util.showToast(this, "获取相机权限失败");
                } else {
                    enterNextPage(mSide);
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == INTO_IDCARDSCAN_PAGE && resultCode == RESULT_OK) {

                String stringExtra = data.getStringExtra(Constant.KEY_RESULT_DATA);
                JSONObject jsonObject = new JSONObject(stringExtra);
                Object side = data.getExtras().get("side");
                String idcardImg = jsonObject.getString("thumb");
                byte[] imgShow = Base64.decode(idcardImg, Base64.NO_WRAP);
                //身份证正面
                if (side.toString().equals(String.valueOf(TYPE_FRONT))) {
                    String portraitImg = jsonObject.optString("header");
                    portraitSource = portraitImg;
                    ivFront.setImageBitmap(BitmapFactory.decodeByteArray(imgShow, 0, imgShow.length));
                    frontSource = idcardImg;
                    ivFrontReplay.setVisibility(View.VISIBLE);
                    flFront.setClickable(false);
                } else {
                    //身份背面
                    ivBack.setImageBitmap(BitmapFactory.decodeByteArray(imgShow, 0, imgShow.length));
                    backSource = idcardImg;
                    ivBackReplay.setVisibility(View.VISIBLE);
                    flBack.setClickable(false);
                }
                checkFinish();
            }
        } catch (Exception e) {
            showToast("身份证错误");
            e.printStackTrace();
        }
    }
}
