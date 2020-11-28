package com.hfax.ucard.modules.loan;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.ContactInfo;
import com.hfax.ucard.bean.GlobalConfigBean;
import com.hfax.ucard.bean.UserStatusBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.bean.PersonInfoBean;
import com.hfax.ucard.utils.*;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.FlowLayout;
import com.hfax.ucard.widget.HfaxScrollView;
import com.hfax.ucard.widget.PicketDialog;
import com.hfax.ucard.widget.RectangleView;
import com.hfax.ucard.widget.SimpleDialog;
import com.hfax.ucard.widget.wheels.CityPickerDialog;
import com.hfax.ucard.widget.wheels.OnCityItemClickListener;
import com.hfax.ucard.widget.wheels.PickConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人信息
 *
 * @author SongGuangyao
 * @date 2018/5/2
 */

public class PersonInfoActivity extends BaseNetworkActivity<PersonInfoBean> implements TextWatcher {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.v_title_divider_)
    View vTitleDivider;
    @BindView(R.id.rlv_bg)
    RectangleView rlvBg;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.flowLayout_education)
    FlowLayout flowLayoutEducation;
    @BindView(R.id.flowLayout_marriage)
    FlowLayout flowLayoutMarriage;
    @BindView(R.id.et_company)
    EditText etCompany;
    @BindView(R.id.et_qq)
    EditText etQq;
    @BindView(R.id.tv_user_1)
    TextView tvUser1;
    @BindView(R.id.tv_user_2)
    TextView tvUser2;
    @BindView(R.id.iv_info)
    ImageView ivInfo;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_position)
    TextView tvPosition;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.tv_usefor)
    TextView tv_usefore;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.hsl_content)
    HfaxScrollView hslContent;
    @BindView(R.id.ll_company)
    LinearLayout llCompany;
    @BindView(R.id.fl_company_segment)
    FrameLayout flCompanySegment;
    UCardConstants.UCARD_PERSON_NEXT_CLICK person_next_click = new UCardConstants.UCARD_PERSON_NEXT_CLICK();
    /**
     * 职业类型
     */
    private List<String> professionList = new ArrayList<>();
    /**
     * 收入区间
     */
    private List<String> incomeList = new ArrayList<>();
    /**
     * 借款用途
     */
    private List<String> useForList = new ArrayList<>();

    /**
     * 家人手机号请求码
     */
    private final int REQUEST_PHONE_0 = 1000;
    /**
     * 朋友手机号请求码
     */
    private final int REQUEST_PHONE_1 = 1001;
    /**
     * 获取联系人列表请求码
     */
    private final int REQUEST_CONTACTS = 1002;

    /**
     * 联系人列表
     */
    private List<ContactInfo> contactList = new ArrayList<>();

    /**
     * 经纬度
     */
    private LocationUtils location;

    //教育
    private List<GlobalConfigBean.Unit> educations;
    //收入
    private List<GlobalConfigBean.Unit> incomes;
    //婚姻
    private List<GlobalConfigBean.Unit> marriages;
    //职业
    private List<GlobalConfigBean.Unit> professions;
    //个人信息数据集合
    private PersonInfoBean mPersonBean = new PersonInfoBean();
    //配置
    private GlobalConfigBean bean;
    //选择器配置
    private PickConfig config = new PickConfig();

    @Override
    public void initData() {
        GrowingIOUtils.track(UCardConstants.ANDR_PERSON_PAGE);
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_PERSON_PAGE, null);
        PermissionUtils.initLocationPermission(this);
        loadConfig();
        initLayout();

        //用于意外关闭时候，状态数据未保存，恢复到首页
        if (UserStatusBean.getBean() == null) {
            MainActivity.start(this);
            finish();
            return;
        }
        //如果已经认证过，则显示旧版信息
        if (UserStatusBean.getBean().getProfile()) {
            loadOldInfo();
        } else {//默认没有上传过联系人列表
            requestPhone(REQUEST_CONTACTS);
        }
        location = new LocationUtils();
    }

    /**
     * 加载布局样式
     */
    private void initLayout() {
        tvTitle.setText("个人信息");
        ivInfo.setImageResource(R.drawable.icon_steps2_pressed);
        tvInfo.setTextColor(getResources().getColor(R.color.person_info_selected_color));

        //设置头部背景
        View view = findViewById(R.id.ic_tip);
        view.setBackgroundResource(R.drawable.icon_selectpoint2);

        ViewGroup.LayoutParams params = rlvBg.getLayoutParams();
        int screenWidth = Utils.getScreenWidth(this);
        //每个格的宽度  = （屏幕宽度 - 边缘宽度*2 - 中间空格宽度*2）/3;
        double unit = (screenWidth - Utils.dip2px(this, 16 * 2 + 18 * 2)) / 3;
        params.width = (int) (unit * 2 + Utils.dip2px(this, 18 + 8));
        rlvBg.setLayoutParams(params);

        vTitleDivider.setVisibility(View.GONE);
        flowLayoutEducation.setHorizontalSpace(18);
        flowLayoutEducation.setVerticalSpace(20);
        flowLayoutMarriage.setHorizontalSpace(18);
        flowLayoutMarriage.setVerticalSpace(20);
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_person_info;
    }


    @OnClick({R.id.iv_title_return, R.id.ll_city, R.id.ll_position, R.id.ll_income, R.id.ll_usefor, R.id.tv_next, R.id.ll_choose_phone1, R.id.ll_choose_phone2})
    public void onViewClicked(View view) {
        if(PreventClickUtils.canNotClick(view))return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.ll_position:
                pickPosition();
                break;
            case R.id.ll_income:
                pickIncome();
                break;
            case R.id.ll_usefor:
                pickUserFor();
                break;
            case R.id.tv_next:
                submitData();
                break;
            case R.id.ll_choose_phone1:
                requestPhone(REQUEST_PHONE_0);
                break;
            case R.id.ll_choose_phone2:
                requestPhone(REQUEST_PHONE_1);
                break;
            case R.id.ll_city:
                pickCity();
                break;
        }
    }

    /**
     * 选择城市
     */
    private void pickCity() {

        CityPickerDialog mCityPickerDialog = new CityPickerDialog(this, bean);
        mCityPickerDialog.setConfig(config);
        mCityPickerDialog.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(GlobalConfigBean.CityUnit province, GlobalConfigBean.CityUnit city, GlobalConfigBean.CityUnit district) {
                super.onSelected(province, city, district);
                StringBuilder builder = new StringBuilder();
                StringBuilder builder2 = new StringBuilder();
                builder.append(province.key);
                builder2.append(province.value);
                if (city != null) {
                    builder.append("-" + city.key);
                    if (!TextUtils.isEmpty(city.value)) {
                        builder2.append("  " + city.value);
                    }
                }
                if (district != null) {
                    builder.append("-" + district.key);
                    if (!TextUtils.isEmpty(district.value)) {
                        builder2.append("  " + district.value);
                    }
                }
                config.province = province;
                config.area = district;
                config.city = city;
                mPersonBean.cityCode = builder.toString();
                tvCity.setText(builder2.toString());

                checkData();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        });
        mCityPickerDialog.show();
    }

    /**
     * 请求联系人
     *
     * @param type
     */
    public void requestPhone(int type) {
        PackageManager pkgManager = getPackageManager();
        boolean contactPermission = pkgManager.checkPermission(Manifest.permission.READ_CONTACTS, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= 23 && !contactPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, type);
        } else if (REQUEST_PHONE_0 == type || REQUEST_PHONE_1 == type) {
            try {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MacUtils.getContacts(new DataChange<List<ContactInfo>>() {
                @Override
                public void onChange(List<ContactInfo> contactInfos) {
                    if (contactInfos != null && contactInfos.size() > 0) {
                        contactList = contactInfos;
                        person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_YES;
                    } else {
                        person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_NO;
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            showToast("获取联系人权限失败");
            return;
        }
        if (requestCode == REQUEST_PHONE_0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showToast("获取联系人权限失败");
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_PHONE_0);
            }
        }
        if (requestCode == REQUEST_PHONE_1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showToast("获取联系人权限失败");
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_PHONE_1);
            }
        }
        if (requestCode == REQUEST_CONTACTS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showToast("获取联系人权限失败");
            } else {
                MacUtils.getContacts(new DataChange<List<ContactInfo>>() {
                    @Override
                    public void onChange(List<ContactInfo> contactInfos) {
                        if (contactInfos != null && contactInfos.size() > 0) {
                            contactList = contactInfos;
                            person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_YES;
                        } else {
                            person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_NO;
                        }
                    }
                });
            }
        }
        //定位
        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showToast("定位权限获取失败");
            }
        }
    }

    /**
     * 检测数据完整性
     */
    private void checkData() {
        tvNext.setEnabled(mPersonBean.checkData());
    }

    /**
     * 提交数据
     */
    private void submitData() {
        if (TextUtils.isEmpty(mPersonBean.cityCode)) {
            showToast("请输入正确城市名称");
            return;
        }
        if (mPersonBean.profession < 0) {
            showToast("请选择职业类型");
            return;
        }
        if (!mPersonBean.hideCompany() && UCardUtil.isEmpty(mPersonBean.company)) {
            showToast("请输入正确公司名称");
            return;
        }
        if (mPersonBean.income < 0) {
            showToast("请选择月收入");
            return;
        }
        if (TextUtils.isEmpty(mPersonBean.useFor)) {
            showToast("请选择借款用途");
            return;
        }
        if (mPersonBean.qq < 0) {
            showToast("请输入QQ号");
            return;
        } else if (mPersonBean.qq < 10000) {
            showToast("请输入正确的QQ号");
            return;
        }
        if (TextUtils.isEmpty(mPersonBean.phone1)) {
            showToast("请选择父母或配偶手机号");
            return;
        } else {
            if (!Utils.isMobile(mPersonBean.phone1)) {
                showToast("手机号不符合要求，请重新选择");
                return;
            }
        }
        if (TextUtils.isEmpty(mPersonBean.phone2)) {
            showToast("请选择朋友手机号");
            return;
        } else {
            if (!Utils.isMobile(mPersonBean.phone2)) {
                showToast("手机号不符合要求，请重新选择");
                return;
            }
        }

        //名字截取 30个字符
        if (mPersonBean.name1.length() > 30) {
            mPersonBean.name1 = mPersonBean.name1.substring(0, 30);
        }
        if (mPersonBean.name2.length() > 30) {
            mPersonBean.name2 = mPersonBean.name2.substring(0, 30);
        }
        person_next_click.occupation = tvPosition.getText().toString();
        person_next_click.income = tvIncome.getText().toString();
        //定位权限阻断
        //请求最新
        location.requestLocation();
        if ((location.latitude == 0.0 && location.longitude == 0.0)) {
            if (location.isGPSable) {
                showToast("请开启定位权限");
            } else {
                showToast("请开启GPS定位");
            }
            person_next_click.gps = UCardConstants.UCARD_SDA_NO;
            GrowingIOUtils.track(UCardConstants.ANDR_PERSON_NEXTALL_CLICK);
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_PERSON_NEXT_CLICK, person_next_click);
            return;
        }

        GrowingIOUtils.track(UCardConstants.ANDR_PERSON_NEXT_CLICK);
        person_next_click.gps = UCardConstants.UCARD_SDA_YES;

        showLoadingDialog();

        //判断已经上传过 或者联系人列表不为空
        if (mPersonBean.getContactStatus() || !UCardUtil.isCollectionEmpty(contactList)) {
            submitLoanInfo();
        } else {
            MacUtils.getContacts(new DataChange<List<ContactInfo>>() {
                @Override
                public void onChange(List<ContactInfo> contactInfos) {
                    if (contactInfos != null && contactInfos.size() > 0) {
                        contactList = contactInfos;
                        person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_YES;
                    } else {
                        person_next_click.phonenumberlist = UCardConstants.UCARD_SDA_NO;
                    }
                    submitLoanInfo();
                }
            });
        }
    }

    /**
     * 提交借款信息
     */
    private void submitLoanInfo() {
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_PERSON_NEXT_CLICK, person_next_click);
        RequestMap map = new RequestMap(NetworkAddress.PERSON_INFO);
        map.put("cityCode", mPersonBean.cityCode);
        String company = mPersonBean.company;
        if (TextUtils.isEmpty(company)) {
            company = "无";
        }
        map.put("company", company);
        map.put("education", mPersonBean.education);
        map.put("income", mPersonBean.income);
        map.put("useFor", mPersonBean.useFor);
        map.put("latitude", location.latitude + "");
        map.put("longitude", location.longitude + "");
        map.put("marriage", mPersonBean.marriage);
        map.put("profession", mPersonBean.profession);
        map.put("qq", mPersonBean.qq);
        map.put("name1", mPersonBean.name1);
        map.put("name2", mPersonBean.name2);
        map.put("phone1", mPersonBean.phone1);
        map.put("phone2", mPersonBean.phone2);
        map.put("contactInfo", contactList);

        mNetworkAdapter.request(map, MVPUtils.Method.POST);
    }

    /**
     * 收入区间
     */
    private void pickIncome() {
        PicketDialog picketDialog = new PicketDialog(PersonInfoActivity.this, incomeList, "月收入");
        picketDialog.setDefault(ListUtils.findValue(incomes, mPersonBean.income + ""));
        picketDialog.setListener(new PicketDialog.OnClickListen() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm(String item) {
                tvIncome.setText(item);
                mPersonBean.income = Long.parseLong(ListUtils.findKey(incomes, item));
                checkData();
            }
        });
        picketDialog.show();
    }

    /**
     * 借款用途
     */
    private void pickUserFor() {
        PicketDialog picketDialog = new PicketDialog(PersonInfoActivity.this, useForList, "借款用途");
        picketDialog.setDefault(ListUtils.findValue(bean.useFor, mPersonBean.useFor + ""));
        picketDialog.setListener(new PicketDialog.OnClickListen() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm(String item) {
                tv_usefore.setText(item);
                mPersonBean.useFor = ListUtils.findKey(bean.useFor, item);
                checkData();
            }
        });
        picketDialog.show();
    }

    /**
     * 选择职业
     */
    private void pickPosition() {
        PicketDialog picketDialog = new PicketDialog(PersonInfoActivity.this, professionList, "职业类型");
        picketDialog.setDefault(ListUtils.findValue(professions, mPersonBean.profession + ""));
        picketDialog.setListener(new PicketDialog.OnClickListen() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm(String item) {
                tvPosition.setText(item);
                mPersonBean.profession = Long.parseLong(ListUtils.findKey(professions, item));
                setNeedCompany();
                checkData();
            }
        });
        picketDialog.show();
    }

    /**
     * 设置公司是否需要
     */
    private void setNeedCompany() {
        //默认不显示
        if (mPersonBean.hideCompany()) {
            llCompany.setVisibility(View.GONE);
            flCompanySegment.setVisibility(View.GONE);
        } else {
            llCompany.setVisibility(View.VISIBLE);
            flCompanySegment.setVisibility(View.VISIBLE);
        }
        etCompany.setText(mPersonBean.company);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从通讯录获取联系人
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation") Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
            if (cursor == null || cursor.moveToFirst() != true) {
                showToast("请开启联系人权限");
                return;
            }
            Cursor phone = null;
            try {
                int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String username = cursor.getString(columnIndex);
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone != null) {
                    while (phone.moveToNext()) {
                        String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                        if (requestCode == REQUEST_PHONE_0) {
                            String sPhone = filterPhone(usernumber);
                            if (!TextUtils.isEmpty(sPhone)) {
                                mPersonBean.phone1 = sPhone;
                                mPersonBean.name1 = username;
                                tvUser1.setText(username);
                            }
                        } else if (requestCode == REQUEST_PHONE_1) {
                            String sPhone2 = filterPhone(usernumber);
                            if (!TextUtils.isEmpty(sPhone2)) {
                                mPersonBean.phone2 = sPhone2;
                                mPersonBean.name2 = username;
                                tvUser2.setText(username);
                            }
                        }
                        checkData();
                    }
                } else {
                    showToast("联系人获取失败，请重试");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (phone != null) {
                    phone.close();
                }
            }
        }
    }

    private String filterPhone(String usernumber) {
        String reBack = usernumber;
        //替换开头86
        if (reBack.startsWith("+86")) {
            reBack = reBack.substring(3);
        } else if (reBack.startsWith("86")) {
            reBack = reBack.substring(2);
        }

        //提取数字
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(reBack);
        reBack = m.replaceAll("").trim();

        //验证是否为手机号码
        if (Utils.isMobile(reBack)) {
            return reBack;
        } else {
            showToast("手机号不符合要求，请重新选择");
            return "";
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PersonInfoActivity.class);
        UCardUtil.startActivity(context, intent);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkData();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 加载配置
     */
    public void loadConfig() {

        bean = GlobalConfigUtils.getGlobalConfig();
        educations = bean.education;
        marriages = bean.marriage;
        professions = bean.profession;
        incomes = bean.income;


        for (GlobalConfigBean.Unit unit : professions) {
            professionList.add(unit.value);
        }
        for (GlobalConfigBean.Unit unit : incomes) {
            incomeList.add(unit.value);
        }
        for (GlobalConfigBean.Unit unit : bean.useFor) {
            useForList.add(unit.value);
        }

        RadioButton radioButton;
        for (GlobalConfigBean.Unit unit : educations) {
            radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.view_radiobutton, null);
            radioButton.setId(UCardUtil.generateViewId());
            radioButton.setText(unit.value);
            radioButton.setTag(unit.key);
            flowLayoutEducation.addView(radioButton);
        }
        for (GlobalConfigBean.Unit unit : marriages) {
            radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.view_radiobutton, null);
            radioButton.setId(UCardUtil.generateViewId());
            radioButton.setText(unit.value);
            radioButton.setTag(unit.key);
            flowLayoutMarriage.addView(radioButton);
        }

        flowLayoutEducation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                mPersonBean.education = Long.parseLong(radioButton.getTag().toString());
                person_next_click.education_background = radioButton.getText().toString();
                checkData();

            }
        });
        flowLayoutMarriage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                mPersonBean.marriage = Long.parseLong(radioButton.getTag().toString());
                person_next_click.marrige_background = radioButton.getText().toString();
                checkData();
            }
        });
    }


    /**
     * 加载已经上传过得数据
     */
    private void loadOldInfo() {
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.QUERY_PERSON_INFO, MVPUtils.Method.GET, new SimpleViewImpl<PersonInfoBean>() {
            @Override
            public void onSuccess(PersonInfoBean bean) {
                dismissLoadingDialog();
                mPersonBean = bean;
                setOldData(mPersonBean);
                if (!mPersonBean.getContactStatus()) {
                    requestPhone(REQUEST_CONTACTS);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 填充上次认证数据
     *
     * @param bean 上次认证数据
     */
    private void setOldData(PersonInfoBean bean) {

        //城市
        if (!TextUtils.isEmpty(bean.cityCode)) {
            List<GlobalConfigBean.CityUnit> city = this.bean.city;
            String[] split = bean.cityCode.split("-");

            String newProvince = "";
            String newCity = "";
            String newArea = "";
            if (split != null && split.length == 3) {

                for (GlobalConfigBean.CityUnit u : city) {
                    if (!TextUtils.isEmpty(newProvince) && !TextUtils.isEmpty(newCity) && !TextUtils.isEmpty(newArea)) {
                        break;
                    }
                    if (u.key.equals(split[0])) {
                        newProvince = u.value;
                        config.province = u;
                    }
                    if (u.key.equals(split[1])) {
                        newCity = u.value;
                        config.city = u;
                    }
                    if (u.key.equals(split[2])) {
                        newArea = u.value;
                        config.area = u;
                    }
                }
                StringBuilder builder = new StringBuilder();
                builder.append(newProvince);
                if (!TextUtils.isEmpty(newCity)) {
                    builder.append("  " + newCity);
                }
                if (!TextUtils.isEmpty(newArea)) {
                    builder.append("  " + newArea);
                }
                tvCity.setText(builder.toString());

            }
        }

        //教育
        int childCount = flowLayoutEducation.getChildCount();
        RadioButton eduButton;
        for (int i = 0; i < childCount; i++) {
            eduButton = (RadioButton) flowLayoutEducation.getChildAt(i);
            if (eduButton.getTag().equals(bean.education + "")) {
                eduButton.setChecked(true);
                break;
            }
        }

        //婚姻
        childCount = flowLayoutMarriage.getChildCount();
        RadioButton marButton;
        for (int i = 0; i < childCount; i++) {
            marButton = (RadioButton) flowLayoutMarriage.getChildAt(i);
            if (marButton.getTag().equals(bean.marriage + "")) {
                marButton.setChecked(true);
                break;
            }
        }

        //职业
        tvPosition.setText(ListUtils.findValue(professions, bean.profession + ""));
//        //公司根据职业判断是否显示
        setNeedCompany();

        //收入
        tvIncome.setText(ListUtils.findValue(incomes, bean.income + ""));

        //用途
        if (!TextUtils.isEmpty(bean.useFor)) {
            tv_usefore.setText(ListUtils.findValue(this.bean.useFor, bean.useFor + ""));
        }

        //QQ
        etQq.setText(bean.qq + "");

        //亲人朋友手机号
        if (!TextUtils.isEmpty(mPersonBean.name1)) {
            tvUser1.setText(mPersonBean.name1);
        }
        if (!TextUtils.isEmpty(mPersonBean.name2)) {
            tvUser2.setText(mPersonBean.name2);
        }
        checkData();
    }

    @Override
    public void onSuccess(PersonInfoBean bean) {
        dismissLoadingDialog();
        UserStatusBean.getBean().setProfile();
        AuthorizeActivity.start(this);
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        switch (code) {
            case NetworkAddress.CODE_ERROR_MSG_FACR:
            case NetworkAddress.CODE_ERROR_MSG_ID:
                final SimpleDialog dialog = new SimpleDialog(getThis());
                dialog.setMessage(msg);
                dialog.setLeftButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setRightButton("返回首页", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.start(getThis());
                    }
                });
                dialog.show();
                break;
            default:
                showToast(msg);
                break;
        }
    }


    @Override
    public void initListener() {

        hslContent.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Utils.hideInputMethod(PersonInfoActivity.this);
            }
        });
        etCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (UCardUtil.isEmpty(s)) {
                    mPersonBean.company = "";
                } else {
                    mPersonBean.company = s.toString();
                }
                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etQq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mPersonBean.qq = -1;
                } else {
                    mPersonBean.qq = Long.parseLong(s.toString());
                }
                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (location != null) {
            location.removeListener();
        }
    }
}
