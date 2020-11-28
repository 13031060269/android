package com.hfax.ucard.utils.Constants;

import java.io.Serializable;

/**
 * Created by liuweiping on 2018/3/22.
 */

public interface UCardConstants {
    /**
     * common
     */
    int COMMON_RESULT_CODE = -10608; //所有startActivityForResult时，新的activity返回时的resultCode
    int COMMON_REQUEST_CODE = -10609; //所有startActivityForResult时，新的activity返回时的resultCode
    String COMMON_RESULT_DATA = "result_data"; //startActivityForResult时，新的activity返回的数据key，统一返回json
    String ISCLOSEPAGE = "isClosePage"; //h5打开本地页面时传进来的参数名，等到从三级页面返回时这个二级页面要不要关闭掉,如果value是close则关闭
    String CLOSEPAGE = "close"; //ISCLOSEPAGE的关闭常量close
    String FLAG = "flag";//存管H5结果页面返回结果（"flag": "0"(成功)，"1"(失败)，"2"(处理中)，"3"(待处理)）
    String MSG = "msg";//存管H5结果页面返回提示信息

    /**
     * 个人征信
     */
    //京东
    String AUTHORIZE_JD = "AUTHORIZE_JD";
    //淘宝
    String AUTHORIZE_TAOBAO = "AUTHORIZE_TAOBAO";
    //支付宝
    String AUTHORIZE_ALIPAY = "AUTHORIZE_ALIPAY";

    /**
     * face++
     */
    int FACE_ID_REQUEST = 1000;
    int FACE_LIVE_REQUEST = 1001;


    /**
     * 身份证扫描来源
     */
    //个人中心实名认证
    String IDCARD_PERSON_CENTEL = "IDCARD_PERSON_CENTEL";
    //借款认证
    String IDCARD_LOAN = "IDCARD_LOAN";


    /**
     * Status
     */
    //借款
    String STATUS_LOAN = "STATUS_LOAN";
    //还款
    String STATUS_BACK = "STATUS_BACK";

    /**
     * app版本
     */
    String KEY_VERSION = "key_version";


    //授权状态
    String NOTAUTH = "notauth";
    String CREATETOKEN = "createtoken";
    String SUSPENDED = "suspended";
    String PROCESSING = "processing";
    String DONE = "done";
    String EXPIRED = "expired";
    String FAILD = "faild";


    /**
     * Growing IO 事件名称
     */
    //注册
    String ANDR_BUTTON_SMS_CLICK = "andr_button_SMS_click";//注册页面获取验证码点击量
    //我要借款
    String ANDR_FACE_PAGE = "andr_face_page";//身份认证-face页面浏览量
    String ANDR_FACE_FINISH_PAGE = "andr_face_finish_page";//身份认证-face(完成)页面浏览量
    String ANDR_FACE_VIDEO_CLICK = "andr_face_video_click";//身份认证-face页面录制视频按钮点击量
    String ANDR_FACE_VIDEO_FINISH_CLICK = "andr_face_video_finish_click";//身份认证-face页面录制视频按钮点击量
    String ANDR_OCR_PAGE = "andr_ocr_page";//身份认证-ocr页面浏览量
    String ANDR_OCR_FRONT_BUTTON_CLICK = "andr_ocr_front_button_click";//身份认证-ocr页面身份证正面按钮点击量
    String ANDR_OCR_CON_BUTTON_CLICK = "andr_ocr_con_button_click";//身份认证-ocr页面身份证反面按钮点击量
    String ANDR_OCR_NAME_CHANGE = "andr_ocr_name_change";//身份认证-ocr页面姓名文本框修改量
    String ANDR_OCR_NEXT_BUTTON_CLICK = "andr_ocr_next_button_click";//身份认证-ocr页面下一步按钮点击量
    String ANDR_INSUREID_PAGE = "andr_insureid_page";//身份信息确认弹窗-浏览量
    String ANDR_INSUREID_Y_CLICK = "andr_insureid_Y_click";//身份信息确认弹窗-确认提交按钮点击量
    String ANDR_INSUREID_N_CLICK = "andr_insureid_N_click";//身份信息确认弹窗-返回修改按钮点击量
    String ANDR_PERSON_PAGE = "andr_person_page";//个人信息页面浏览量
    String ANDR_PERSON_NEXT_CLICK = "andr_person_next_click";//个人信息页面下一步按钮点击量
    String ANDR_PERSON_NEXTALL_CLICK = "andr_person_nextall_click";//个人信息页面下一步按钮点击量(无定位)
    String ANDR_AUZ_PAGE = "andr_AUZ_page";//授权页面浏览量
    String ANDR_AUZ_PHONE_CLICK = "andr_AUZ_phone_click";//授权页面运营商授权按钮点击量
    String ANDR_AUZ_NEXT_CLICK = "andr_AUZ_next_click";//授权页面下一步按钮点击量
    String ANDR_INSURELOAN_PAGE = "andr_insureloan_page";//借款确认页面浏览量
    String ANDR_INSURELOAN_ADDCREDIT_CLICK = "andr_insureloan_addcredit_click";//借款确认页面添加信用卡按钮点击量
    String ANDR_ADDCREDIT_FINISH_CLICK = "andr_addcredit_finish_click";//添加信用卡页面完成按钮点击量
    String ANDR_INSURELOAN_SUBMIT_CLICK = "andr_insureloan_submit_click";//借款确认页面确认提交按钮点击量
    //还款
    String ANDR_PAYMENT_PAGE = "andr_payment_page";//还款详情页
    String ANDR_PAYMENT_C_CLICK = "andr_payment_C_click";//还款详情页_确认还款点击量
    String ANDR_PAYMENT_CARD_CLICK = "andr_payment_card_click";//还款详情页_变更银行卡点击量
    //2018.10.25新加
    String ANDR_TEL_NEXT_CHANGE = "andr_tel_next_change";//输入手机号页面下一步按钮点击量
    String ANDR_LOGIN_BOTTON_CLICK = "andr_login_botton_click";//登录页面登录按钮点击量
    String ANDR_REGISTER_PAGE = "andr_register_page";//注册页面浏览量
    String ANDR_BUTTON_SURE_CLICK = "andr_button_sure_click";//注册页面完成注册按钮点击量
    String ANDR_HOME_PAGE = "andr_home_page";//首页页面浏览量
    String ANDR_LOAN_BOTTON_CLICK = "andr_loan_botton_click";//首页去借款按钮点击量
    String ANDR_ONLINE_CUSTSERVICE_CLICK = "andr_online_custservice_click";//在线客服点击量


    //神策打点

    String UCARD_SDA_SUCCEED = "成功";//成功
    String UCARD_SDA_FAILED = "失败";//成功
    String UCARD_SDA_CREDIT = "添加信用卡";//信用卡
    String UCARD_SDA_PLATFORM = "添加平台借记卡";//平台借记卡
    String UCARD_SDA_PLATFORM_CHANGE = "变更平台借记卡";//变更平台借记卡
    String UCARD_SDA_BANK = "添加厦行借记卡";//厦行借记卡
    String UCARD_SDA_BANK_CHANGE = "变更厦行借记卡";//变更厦行借记卡
    String UCARD_SDA_YES = "是";//是
    String UCARD_SDA_NO = "否";//否

    String UCARD_REGISTER = "ucard_register";//注册

    class UCARD_REGISTER implements Serializable {
        public String signup_result;//注册结果   成功，失败
        public String error_type;//错误原因
    }

    String UCARD_LOAN_BOTTON_CLICK = "ucard_loan_botton_click";//点击借款

    class UCARD_LOAN_BOTTON_CLICK implements Serializable {
        public long loan_Amount;//借款金额
        public long loan_deadline;//借款期限
    }

    String UCARD_OCR_PAGE = "ucard_ocr_page";//OCR页面浏览
    String UCARD_OCR_NEXT_BUTTON_CLICK = "ucard_ocr_next_button_click";//OCR按钮点击
    String UCARD_INSUREID_PAGE = "ucard_insureid_page";//个人信息核对
    String UCARD_FACE_PAGE = "ucard_face_page";//Face页面浏览
    String UCARD_FACE_VIDEO_FINISH = "ucard_face_video_finish";//Face按钮点击

    class UCARD_FACE_VIDEO_FINISH implements Serializable {
        public String face_result;//Face结果
        public String error_type;//error_type
    }

    String UCARD_PERSON_PAGE = "ucard_person_page";//个人信息页面浏览
    String UCARD_PERSON_NEXT_CLICK = "ucard_person_next_click";//个人信息下一步点击

    class UCARD_PERSON_NEXT_CLICK implements Serializable {
        public String education_background;//学历
        public String marrige_background;//婚姻情况
        public String occupation;//职业
        public String income;//收入
        public String phonenumberlist = "空";//是否获取到通讯录权限
        public String gps;//是否获取地理位置
    }

    String UCARD_AUZ_PAGE = "ucard_AUZ_page";//授权页面浏览
    String UCARD_AUZ_NEXT_CLICK = "ucard_AUZ_next_click";//授权页面下一步点击
    String UCARD_INSURELOAN_PAGE = "ucard_insureloan_page";//确认借款页面浏览
    String UCARD_INSURELOAN_SUBMIT_CLICK = "ucard_insureloan_submit_click";//确认借款按钮点击

    class UCARD_INSURELOAN_SUBMIT_CLICK implements Serializable {

        public int loan_Amount;//借款金额
        public int loan_deadline;//借款期限
        public String submit_loan_result;//申请结果
        public String error_type;//错误原因
        public String applist;//是否获取到APPlist权限
    }

    String UCARD_TEL_NEXT_CLICK = "ucard_tel_next_click";//输入手机号页面下一步按钮点击量

    String UCARD_REGISTER_PAGE = "ucard_register_page";//注册页面浏览量
    String UCARD_REGIST_AMS_CLICK = "ucard_regist_ams_click";//注册页面获取验证码点击量
    String UCARD_REGIST_SURE_CLICK = "ucard_regist_sure_click";//注册页面完成注册按钮点击量
    String UCARD_HOME_PAGE = "ucard_home_page";//首页页面浏览量
    String UCARD_OCR_FRONT_BUTTON_CLICK = "ucard_ocr_front_button_click";//OCR身份证正面按钮点击量
    String UCARD_OCR_CON_BUTTON_CLICK = "ucard_ocr_con_button_click";//OCR身份证反面按钮点击量
    String UCARD_FACE_FINISH_PAGE = "ucard_face_finish_page";//face页面已完成浏览量
    String UCARD_FACE_VIDEO_FINISH_CLICK = "ucard_face_video_finish_click";//face页面已完成页面下一步按钮点击量
    class UCARD_FACE_VIDEO_FINISH_CLICK implements Serializable{
        public String error_typel;
    }
    String UCARD_H5V103_PERSON_NEXTALL_CLICK = "ucard_H5V103_person_nextall_click";//个人信息页面下一步按钮点击量（全部）
    String UCARD_INSUREID_Y_CLICK = "ucard_insureid_Y_click";//身份信息确认弹窗-确认提交按钮点击量

    class UCARD_INSUREID_Y_CLICK implements Serializable {
        public String error_type;
    }

    String UCARD_INSUREID_N_CLICK = "ucard_insureid_N_click";//身份信息确认弹窗-返回修改按钮点击量
    String UCARD_PHONENUMBER_CLICK = "ucard_phonenumber_click";//授权页面运营商授权按钮点击量
    String UCARD_INSURELOAN_ADDCREDIT_CLICK = "ucard_insureloan_addcredit_click";//借款确认页面添加信用卡按钮点击量
    String UCARD_ADD_CARD_PAGE = "ucard_add_card_page";//添加银行卡页面浏览量
    String UCARD_ADD_CARD_BUTTON_SMS_CLICK = "ucard_add_card_button_SMS_click";//添加银行卡页面获取验证码点击量
    String UCARD_ADD_CARD_FINISH_CLICK = "ucard_add_card_finish_click";//添加银行卡页面完成按钮点击量

    class UCARD_ADD_CARD implements Serializable {
        public String type;//信用卡，平台借记卡，厦行借记卡
        public String result= UCARD_SDA_SUCCEED;
        public String error_type;
    }

    String UCARD_OCR_UPLOAD = "ucard_ocr_upload";//ucard_ocr_upload

    class UCARD_OCR_UPLOAD implements Serializable {
        public String result = UCARD_SDA_SUCCEED;
        public String error_type = "";
    }
    class RESULT implements Serializable {
        public String result = UCARD_SDA_SUCCEED;
        public String error_type = "";
    }

    //短信登录
    String SLIDE_VERFI="slide_verfi";//滑块操作
    String PASSWORD_LOGIN_PAGE="password_login_page";//密码登录页
    String SYS_LOGIN_PAGE="sys_login_page";//短信登录页


    String PASSWORD_LOGIN_CLICK="password_login_click";//密码登录结果
    String SYS_LOGIN_CLICK="sys_login_click";//短信登录结果

    class LOGIN_RESULT implements Serializable {

        public String login_result = UCARD_SDA_SUCCEED;
        public String error_type = "";
    }



}
