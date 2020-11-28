package com.hfax.ucard.modules.loan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfax.facelib.liveness.DFActionLivenessActivity;
import com.hfax.facelib.liveness.util.Constants;
import com.hfax.facelib.liveness.util.LivenessUtils;
import com.hfax.facelib.util.Constant;
import com.hfax.facelib.util.Util;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.IDTypeBean;
import com.hfax.ucard.bean.UserStatusBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.bean.FaceBean;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.HfaxScrollView;
import com.hfax.ucard.widget.RectangleView;
import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.LivenessLicenseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.M;
import static com.hfax.ucard.utils.Constants.UCardConstants.FACE_LIVE_REQUEST;

/**
 * 活体认证
 *
 * @author SongGuangyao
 * @date 2018/5/2
 */

public class FaceActivity extends BaseNetworkActivity<FaceBean> {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.v_title_divider_)
    View vTitleDivider;
    @BindView(R.id.rlv_bg)
    RectangleView rlvBg;
    @BindView(R.id.view_dot)
    View viewDot;
    @BindView(R.id.iv_scan_id)
    ImageView ivScanId;
    @BindView(R.id.rl_complete)
    RelativeLayout rlComplete;
    @BindView(R.id.tv_info_tip)
    TextView tv_info_tip;

    public static final int EXTERNAL_STORAGE_REQ_CAMERA_CODE = 10;

    private String uuid;
    private MediaPlayer mMediaPlayer;

    //是否授权完成
    private boolean isAuthed = false;
    //授权来源  初始化
    private final String SOURCE_ONCREATE = "SOURCE_ONCREATE";
    //下一步按钮
    private final String SOURCE_BTN = "SOURCE_BTN";


    @Override
    public void onSuccess(FaceBean bean) {
        dismissLoadingDialog();
        UserStatusBean.getBean().setFace();
        nextPage();
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }

    @Override
    public void initData() {
        //用于意外关闭时候，状态数据未保存，恢复到首页
        if (UserStatusBean.getBean() == null) {
            MainActivity.start(this);
            finish();
            return;
        }
        tvTitle.setText("身份认证");
        vTitleDivider.setVisibility(View.GONE);
        viewDot.setVisibility(View.VISIBLE);
        String text = getString(R.string.identity_authorize_tip);
        String colorText = getString(R.string.identity_authorize_tip_color);
        int start = text.indexOf(colorText);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#7B7BAD")), start, start + colorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, start + colorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_info_tip.setText(spannableString);
        initView();

        //Face++活体授权
        requestAuth(SOURCE_ONCREATE);

        //完成授权
        if (UserStatusBean.getBean().getFace()) {
            GrowingIOUtils.track(UCardConstants.ANDR_FACE_FINISH_PAGE);
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_FACE_FINISH_PAGE);
        } else {//未完成
            GrowingIOUtils.track(UCardConstants.ANDR_FACE_PAGE);
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_FACE_PAGE, null);
        }
    }

    /**
     * 请求授权
     *
     * @param source 来源
     */
    private void requestAuth(final String source) {
        uuid = Util.getUUIDString(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Manager manager = new Manager(getApplicationContext());
                LivenessLicenseManager licenseManager = new LivenessLicenseManager(getApplicationContext());
                manager.registerLicenseManager(licenseManager);
                manager.takeLicenseFromNetwork(uuid);
                //授权成功xx
                UIAuthState(licenseManager.checkCachedLicense() > 0, source);

            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (UserStatusBean.getBean() == null) {
            showErrorView();
            return;
        }
        //已经认证
        if (UserStatusBean.getBean().getFace()) {
            rlComplete.setVisibility(View.VISIBLE);
        } else {
            rlComplete.setVisibility(View.GONE);
        }
    }

    /**
     * 授权提示
     *
     * @param isSuccess 是否联网授权成功
     * @param source
     */
    private void UIAuthState(final boolean isSuccess, final String source) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isAuthed = isSuccess;
                if (isAuthed) {
                    if (TextUtils.equals(source, SOURCE_ONCREATE)) {
                        LogUtil.e("info---", "活体识别授权成功");
                    } else if (TextUtils.equals(source, SOURCE_BTN)) {
                        requestCameraPerm();
                    }
                } else {
                    showToast("授权失败,请退出重试");
                }
            }
        });
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
    protected int getLayoutRes() {
        return R.layout.activity_identity_authorize;
    }


    @OnClick({R.id.iv_title_return, R.id.tv_next, R.id.tv_complete_next})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                goBack();
                break;
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_FACE_VIDEO_CLICK);
                requestCameraPerm();
                break;
            case R.id.tv_complete_next:
                GrowingIOUtils.track(UCardConstants.ANDR_FACE_VIDEO_FINISH_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_FACE_VIDEO_FINISH_CLICK);
                nextPage();
                break;
        }
    }


    /**
     * 返回键
     */
    private void goBack() {
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, FaceActivity.class);
        UCardUtil.startActivity(context, intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_LIVE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String resultOBJ = bundle.getString(Constant.KEY_RESULT_DATA);
            try {
                JSONObject result = new JSONObject(resultOBJ);
                int resID = result.getInt("resultcode");
                checkID(resID);
                boolean isSuccess = resID == R.string.verify_success;
                UCardConstants.UCARD_FACE_VIDEO_FINISH face_video_finish = new UCardConstants.UCARD_FACE_VIDEO_FINISH();
                if (isSuccess) {
                    face_video_finish.face_result = UCardConstants.UCARD_SDA_SUCCEED;
                    saveImg(result);
                } else {
                    face_video_finish.face_result = UCardConstants.UCARD_SDA_FAILED;
                    face_video_finish.error_type = getString(resID);
                    showToast(face_video_finish.error_type);
                }
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_FACE_VIDEO_FINISH, face_video_finish);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片
     *
     * @param jsonObject 数据
     */
    private void saveImg(JSONObject jsonObject) {
        showLoadingDialog();
        try {
            String delta = jsonObject.getString("delta");
            String bestImage = null;
            String envImage = null;
            String[] options = new String[3];
            JSONArray images = jsonObject.getJSONArray("result");
            for (int i = 0; i < images.length(); i++) {
                JSONObject json = images.getJSONObject(i);
                String data = json.getString("data");
                switch (json.getString("type")) {
                    case "image_best":
                        bestImage = data;
                        break;
                    case "image_env":
                        envImage = data;
                        break;
                    case "image_action1":
                        options[0] = data;
                        break;
                    case "image_action2":
                        options[1] = data;
                        break;
                    case "image_action3":
                        options[2] = data;
                        break;
                }
            }
            RequestMap map = new RequestMap(NetworkAddress.FACE_IMG);
            map.put("bestImage", bestImage);
            map.put("huoTi", envImage);
            map.put("delta", delta);
            map.put("options", options);
            mNetworkAdapter.request(map, MVPUtils.Method.POST);
        } catch (Exception e) {
            showToast("活体检测异常");
            dismissLoadingDialog();
            e.printStackTrace();
        }
    }

    private void checkID(int resID) {
        if (resID == R.string.verify_success) {
            doPlay(R.raw.meglive_success);
        } else if (resID == R.string.liveness_detection_failed_not_video) {
            doPlay(R.raw.meglive_failed);
        } else if (resID == R.string.liveness_detection_failed_timeout) {
            doPlay(R.raw.meglive_failed);
        } else if (resID == R.string.liveness_detection_failed) {
            doPlay(R.raw.meglive_failed);
        } else {
            doPlay(R.raw.meglive_failed);
        }
    }

    private void doPlay(int rawId) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        mMediaPlayer.reset();
        try {
            AssetFileDescriptor localAssetFileDescriptor = getResources().openRawResourceFd(rawId);
            mMediaPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor(), localAssetFileDescriptor.getStartOffset(), localAssetFileDescriptor.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    private void requestCameraPerm() {
        if (!isAuthed) {
            requestAuth(SOURCE_BTN);
            return;
        }
        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, EXTERNAL_STORAGE_REQ_CAMERA_CODE);
            } else {
                enterLivePage();
            }
        } else {
            enterLivePage();
        }
    }

    /**
     * 进入活体授权
     */
    private void enterLivePage() {
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.IDENTITY_TYPE, MVPUtils.Method.GET, new SimpleViewImpl<IDTypeBean>() {
            @Override
            public void onSuccess(IDTypeBean idTypeBean) {
                if (idTypeBean.type == 1) {
                    LivenessUtils.startFaceLivenessActivity(getThis(), FACE_LIVE_REQUEST, new Bundle());
                } else {
                    Bundle bundle = new Bundle();
                    List<String> sequenceDataList = new ArrayList<>();
                    sequenceDataList.add(Constants.BLINK);
                    sequenceDataList.add(Constants.MOUTH);
                    sequenceDataList.add(Constants.NOD);
                    sequenceDataList.add(Constants.YAW);
                    Collections.shuffle(sequenceDataList);
                    sequenceDataList.set(0, Constants.HOLD_STILL);
                    bundle.putString(DFActionLivenessActivity.EXTRA_MOTION_SEQUENCE, LivenessUtils.getActionSequence(sequenceDataList));
                    LivenessUtils.startDFLivenessActivity(getThis(), FACE_LIVE_REQUEST, bundle);
                }
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
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showToast("获取相机权限失败");
            } else {
                enterLivePage();
            }
        }
    }


    /**
     * 调转下一页
     */
    public void nextPage() {
        PersonInfoActivity.start(this);
    }
}
