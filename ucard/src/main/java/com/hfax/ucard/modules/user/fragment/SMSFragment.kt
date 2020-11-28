package com.hfax.ucard.modules.user.fragment

import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.hfax.app.h5.H5Activity
import com.hfax.app.utils.EventBusUtils
import com.hfax.lib.BaseApplication
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkActivity
import com.hfax.ucard.base.BaseNetworkFragment
import com.hfax.ucard.bean.ExistMobileBean
import com.hfax.ucard.bean.LoginBean
import com.hfax.ucard.utils.*
import com.hfax.ucard.utils.Constants.UCardConstants
import com.hfax.ucard.utils.Constants.UCardConstants.LOGIN_RESULT
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.RequestMap
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl
import com.hfax.ucard.widget.AuthCodeView
import com.hfax.ucard.widget.CodeListener
import com.hfax.ucard.widget.KeyboardView

class SMSFragment : BaseNetworkFragment<LoginBean>(), CodeListener {
    companion object {
        @JvmStatic
        val PHONE = "phone"

        @JvmStatic
        val CHECKED = "checked"
    }

    @BindView(R.id.keyboard_recycle)
    lateinit var keyboardRecycle: KeyboardView

    @BindView(R.id.auto_code_view)
    lateinit var authCodeView: AuthCodeView

    @BindView(R.id.tv_normal)
    lateinit var tvNormal: TextView

    @BindView(R.id.tv_count)
    lateinit var tvCount: TextView

    @BindView(R.id.tv_count_suffix)
    lateinit var tvCountSuffix: TextView

    @BindView(R.id.tv_phone)
    lateinit var tvPhone: TextView

    @BindView(R.id.tv_submit)
    lateinit var tvSubmit: TextView


    @BindView(R.id.cb_contract)
    lateinit var cbContract: CheckBox
    private var mLocation: LocationUtils? = null
    private var inputs = ""

    override fun getLayoutRes(): Int = R.layout.fragment_sms
    private var count: Int = 60

    override fun initListener() {
        PermissionUtils.initLocationPermission(activity)
        keyboardRecycle.adapter.keyboardListener = authCodeView
        authCodeView.codeListener = this
        tvPhone.text = UCardUtil.formatPhone(arguments?.getString(PHONE))
        cbContract.isChecked = arguments?.getBoolean(CHECKED) ?: false
        startTimer()
        FMIdUtils.init(BaseApplication.getContext())
        mLocation = LocationUtils().apply { requestLocation() }
    }

    @OnClick(R.id.tv_normal, R.id.tv_submit, R.id.tv_contract)
    fun onViewClick(view: View) {
        if (PreventClickUtils.canNotClick(view)) return
        when (view.id) {
            R.id.tv_normal -> {
                getVerify()
            }
            R.id.tv_submit -> {
                submit()
            }
            R.id.tv_contract -> {
                H5Activity.startActivity(activity, String.format(UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT), "", "regist"))
            }
        }
    }

    fun submit() {
        if (!cbContract.isChecked) {
            showToast("请阅读并同意协议")
            return
        }
        FMIdUtils.getFMId(activity, object : FMIdUtils.CallBack {
            override fun callBack(fmId: String) {
                YunPianUtils.getYunPian().requestYunPian(baseActivity) {
                    if (it != null) {
                        showLoadingDialog()
                        val map = RequestMap(NetworkAddress.LOGIN_WITH_CODE)
                        map["fingerPrint"] = fmId
                        map["longitude"] = mLocation?.longitude
                        map["latitude"] = mLocation?.latitude
                        map["mobile"] = UCardUtil.parsePhone(tvPhone.text.toString())
                        map["wifiInfo"] = MacUtils.getWifi()
                        map["code"] = inputs
                        map.putAll(it)
                        mNetworkAdapter.request(map, MVPUtils.Method.POST)
                    }
                }
            }

            override fun error(msg: String) {
                dismissLoadingDialog()
                showToast("登录失败，请重试")
            }
        })
    }

    private fun getVerify() {
        showLoadingDialog()
        YunPianUtils.getYunPian().requestYunPian(activity) {
            it?.apply {
                val requestMap = RequestMap(NetworkAddress.GET_LOGIN_CODE)
                requestMap.put("mobile", UCardUtil.parsePhone(tvPhone.text.toString()))
                requestMap.put("check-mobile", false)
                requestMap.putAll(it)
                mNetworkAdapter.request(requestMap, MVPUtils.Method.POST, object : SimpleViewImpl<ExistMobileBean>() {
                    override fun onSuccess(t: ExistMobileBean?) {
                        startTimer()
                        dismissLoadingDialog()
                    }

                    override fun onFail(code: Int, msg: String?) {
                        showToast(msg)
                        dismissLoadingDialog()
                    }
                })
            }
        }
    }

    override fun onSuccess(bean: LoginBean?) {
        dismissLoadingDialog()
        if (bean != null && !TextUtils.isEmpty(bean.accessToken)) {
            bean.save()
            showToast("登录成功")
            GrowingIOUtils.setUserId()
            EventBusUtils.post(BaseNetworkActivity.ACTION_LOGIN)
            GrowingIOUtils.trackSDA(UCardConstants.SYS_LOGIN_CLICK, LOGIN_RESULT())
            finish()
        } else {
            showToast("登录失败")
            val login_result = LOGIN_RESULT()
            login_result.login_result = UCardConstants.UCARD_SDA_FAILED
            login_result.error_type = "返回的accessToken为空"
            GrowingIOUtils.trackSDA(UCardConstants.SYS_LOGIN_CLICK, login_result)
        }
    }

    override fun onFail(code: Int, msg: String?) {
        val login_result = LOGIN_RESULT()
        login_result.login_result = UCardConstants.UCARD_SDA_FAILED
        login_result.error_type = msg
        GrowingIOUtils.trackSDA(UCardConstants.SYS_LOGIN_CLICK, login_result)
        showToast(msg)
        dismissLoadingDialog()
    }

    override fun inputFull(inputs: String) {
        this.inputs = inputs
        tvSubmit.isEnabled = true
    }

    override fun fullToNot() {
        tvSubmit.isEnabled = false
    }

    private fun startTimer() {
        tvNormal.visibility = View.GONE
        tvCount.visibility = View.VISIBLE
        tvCountSuffix.visibility = View.VISIBLE
        count = 60
        runnable()
    }

    fun runnable() {
        if (isDetached) return
        if (--count < 0) {
            tvNormal.visibility = View.VISIBLE
            tvCount.visibility = View.GONE
            tvCountSuffix.visibility = View.GONE
            return
        }
        tvCount.text = "$count"
        mHandler.postDelayed({ runnable() }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocation?.removeListener()
    }
}