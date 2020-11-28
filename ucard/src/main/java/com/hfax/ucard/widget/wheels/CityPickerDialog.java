package com.hfax.ucard.widget.wheels;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.bean.GlobalConfigBean;
import com.hfax.ucard.utils.UCardUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 省市区三级选择
 * 作者：liji on 2015/12/17 10:40
 * 邮箱：lijiwork@sina.com
 */
public class CityPickerDialog extends Dialog implements ScrollPickerView.OnSelectedListener {


    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.csp_province)
    CityScrollPicker cspProvince;
    @BindView(R.id.csp_city)
    CityScrollPicker cspCity;
    @BindView(R.id.csp_district)
    CityScrollPicker cspDistrict;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.ll_title_background)
    LinearLayout llTitleBackground;
    private OnCityItemClickListener mBaseListener;
    private PickConfig config;


    private Context context;
    private GlobalConfigBean globalConfigBean;
    private GlobalConfigBean.CityUnit proUnit;//当前省
    private GlobalConfigBean.CityUnit cityUnit;//当前市
    private GlobalConfigBean.CityUnit areaUnit;

    public CityPickerDialog(@NonNull Context context, GlobalConfigBean bean) {
        super(context, R.style.BaseCustomAlertDialog);
        this.context = context;
        globalConfigBean = bean;
    }


    public void setOnCityItemClickListener(OnCityItemClickListener listener) {
        mBaseListener = listener;
    }


    /**
     * 设置配置
     */
    public void setConfig(PickConfig config) {
        this.config = config;
    }


    /**
     * 加载数据
     */
    private void setUpData() {

        int provinceDefault = 0;
        //设置默认加载显示
        if (config != null && config.province != null) {
            List<GlobalConfigBean.CityUnit> city = globalConfigBean.getCity("0");
            if (!UCardUtil.isCollectionEmpty(city)) {
                for (int i = 0; i < city.size(); i++) {
                    if (config.province.key.equals(city.get(i).key)) {
                        provinceDefault = i;
                        break;
                    }
                }
            }
        }

        cspProvince.setData(globalConfigBean.getCity("0"));

        //获取所设置的省的位置，直接定位到该位置
        if (-1 != provinceDefault) {
            cspProvince.setSelectedPosition(provinceDefault);
        }

        updateCities();

        updateAreas();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        //省份滚轮滑动的当前位置
        int pCurrent = cspProvince.getSelectedPosition();
        int pCityCurrent = 0;

        List<GlobalConfigBean.CityUnit> city1 = globalConfigBean.getCity("0");
        if (UCardUtil.isCollectionEmpty(city1)) return;
        proUnit = city1.get(pCurrent);
        if (proUnit == null) return;
        if (config != null && config.city != null) {
            List<GlobalConfigBean.CityUnit> city = globalConfigBean.getCity(proUnit.key);
            if (city != null) {
                for (int i = 0; i < city.size(); i++) {
                    if (config.city.key.equals(city.get(i).key)) {
                        pCityCurrent = i;
                        break;
                    }
                }
            }
        }

        cspCity.setData(globalConfigBean.getCity(proUnit.key));
        cspCity.setSelectedPosition(pCityCurrent);
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {

        int pCurrent = cspCity.getSelectedPosition();

        int pAreaCurrent = 0;

        //获取市列表
        List<GlobalConfigBean.CityUnit> cityList = globalConfigBean.getCity(proUnit.key);

        if (UCardUtil.isCollectionEmpty(cityList) || pCurrent >= cityList.size()) {
            return;
        }
        cityUnit = cityList.get(pCurrent);

        List<GlobalConfigBean.CityUnit> city = globalConfigBean.getCity(cityUnit.key);
        if (city == null || city.size() == 0) {
            areaUnit = null;
        }

        if (config != null && config.area != null) {
            for (int i = 0; i < city.size(); i++) {
                if (config.area.key.equals(city.get(i).key)) {
                    pAreaCurrent = i;
                    break;
                }
            }
        }
        cspDistrict.setData(city);
        cspDistrict.setSelectedPosition(pAreaCurrent);

    }


    @Override
    public void onSelected(ScrollPickerView scrollPickerView, int position) {
        if (scrollPickerView == cspProvince) {
            updateCities();
        } else if (scrollPickerView == cspCity) {
            updateAreas();
        } else if (scrollPickerView == cspDistrict) {
            //存储
            List<GlobalConfigBean.CityUnit> city = globalConfigBean.getCity(this.cityUnit.key);
            int selectedPosition = cspDistrict.getSelectedPosition();

            if (UCardUtil.isCollectionEmpty(city) || selectedPosition >= city.size()) {
                return;
            }
            areaUnit = city.get(selectedPosition);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_citypicker);
        ButterKnife.bind(this);

        cspCity.setVisibility(View.VISIBLE);
        cspDistrict.setVisibility(View.VISIBLE);
        cspProvince.setVisibility(View.VISIBLE);

        // 添加change事件
        cspCity.setOnSelectedListener(this);
        // 添加change事件
        cspDistrict.setOnSelectedListener(this);
        // 添加change事件
        cspProvince.setOnSelectedListener(this);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }


    @Override
    public void show() {
        super.show();
        setUpData();
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                if (mBaseListener != null) {
                    mBaseListener.onCancel();
                }
                dismiss();
                break;
            case R.id.tv_confirm:
                if (mBaseListener != null) {
                    mBaseListener.onSelected(proUnit, cityUnit, areaUnit);
                }
                dismiss();
                break;
        }
    }

}
