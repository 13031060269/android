package com.hfax.ucard.utils.mvp;

/**
 * 存放请求的网络地址
 */
public class NetworkAddress {
    public static String BASE_URL = "https://m.beneucard.com/";

    //神策地址
    private static String URL_SDA_IP = "https://iolog.hfax.com/sa?project=";
    public static String URL_SDA_DEBUG = URL_SDA_IP+"default";
    public static String URL_SDA = URL_SDA_IP+"production";

    /******
     * 账户
     ******/
    public static final String EXIST_MOBILE = "hfas/sapi/account/exist-mobile";//查询手机号是否为平台注册用户
    public static final String LOGIN = "/hfas/sapi/account/signin";        //登录
    public static final String REGISTER = "hfas/sapi/account/signup";   //注册
    public static final String RESET_PASSWORD = "hfas/sapi/account/reset-password";   //忘记密码
    public static final String IMGCODE_URL = "/hfas/sapi/captcha/gen-img";  //获取图片验证码
    public static final String SMS_CODE = "/hfas/sapi/sms/gen-sms"; //短信验证码
    public static final String GET_RESETPWD_CODE = "/hfas/sapi/account/get-resetpwd-code"; //获取重置密码短信验证码
    public static final String SMS_BANK_CODE = "/hfas/sapi/sms/gen-bank-sms"; //银行相关短信验证码
    public static final String FACE_IMG = "/hfas/sapi/pre-loan/identity/upload-face-image"; //上传Face照片
    public static final String ID_IMG = "/hfas/sapi/pre-loan/identity/upload-idcard"; //上传身份证照片
    public static final String ID_INFO = "/hfas/sapi/pre-loan/identity/submit-idcard-info"; //上传身份证信息
    public static final String PERSON_INFO = "/hfas/sapi/pre-loan/identity/submit-profile"; //上传个人信息
    public static final String LAST_CODE = "/hfas/sapi/sms/resend-count-down-time"; //计算重新发送短信验证码剩余时间(单位-秒)
    public static final String QUERY_PERSON_INFO = "/hfas/sapi/pre-loan/identity/query-profile"; //查询个人信息
    public static final String QUERY_AUTH_STATUS = "/hfas/sapi/v2/credit-auth/query-auth-status"; //查询授权状态
    public static final String QUERY_AUTH_URL = "/hfas/sapi/v2/credit-auth/query-url"; //查询授权Url
    public static final String LOAN_APPLY_INFO = "/hfas/sapi/pre-loan/query-loan-apply-info"; //借款申请信息+信用卡列表查询
    public static final String SUBMIT_LOAN_APPLY = "/hfas/sapi/v2/pre-loan/apply-loan"; //提交借款
    public static final String LOGIN_OUT = "/hfas/sapi/account/signout"; //登出
    public static final String UPDATE_LOGIN_PWD = "/hfas/sapi/account/modify-password";  //修改登录密码
    public static final String IDENTITY_TYPE = "/hfas/sapi/pre-loan/identity/identity-type";  //查询OCR和活体渠道

    /**
     * "我的"模块接口调整
     */
    public static final String CHECK_COMPLETE_IDCARD = "/hfas/sapi/pre-loan/identity/check-complete-idcard";//查询是否完成实名认证
    public static final String QUERY_BANK_LIST = "/hfas/sapi/bank-card/query-bank-list";//银行卡列表查询
    public static final String ADD_CREDIT_CARD = "/hfas/sapi/bank-card/add-credit-card";//添加信用卡
    public static final String ADD_DEBIT_CARD = "/hfas/sapi/bank-card/add-debit-card";//存管开户绑卡
    public static final String CAN_CHANGE_DEBIT = "/hfas/sapi/bank-card/can-change-card";//当前还款卡是否可更换
    public static final String CHANGE_DEBIT_CARD = "/hfas/sapi/bank-card/change-debit-card";//换还款卡
    public static final String ADD_OR_CHANGE_PLATFORM_CARD = "/hfas/sapi/bank-card/add-or-change-platform-card";//添加或更换平台卡
    public static final String QUERY_COUPONQUERY_COUPON_LIST_LIST = "/hfas/sapi/loan/queryCouponList";//查询优惠券列表接口
    /**
     * 我的借款
     */

    public static final String QUERY_LOAN_RECORD = "/hfas/sapi/v2/loan/queryLoanRecord";//查询借款记录
    public static final String QUERY_LOAN_DETAIL = "/hfas/sapi/v2/loan/queryLoanDetail";//查询借款详情
    public static final String WITHDRAW = "/hfas/sapi/fund/withdraw";//提现
    public static final String CONFIRM_LOAN = "/hfas/sapi/v2/loan/confirmLoan";//确认要款
    public static final String REPAY = "/hfas/sapi/v2/loan/repay";//还款
    public static final String RECORD_CLICK_FAIL_BAR = "/hfas/sapi/loan/recordClickFailBar";//还款详情中一个放款失败条点击提交

    /**
     * 首页
     */
    public static final String CONFIG_DICT = "hfas/sapi/config/dict";//配置查询字典信息
    public static final String QUERY_TRIAL_REPAYMENT = "hfas/sapi/v2/fund/query-trial-repayment";//提交还款试算档位信息
    public static final String QUERY_USER_STATUS = "hfas/sapi/pre-loan/query-user-status";//查询用户状态
    public static final String SUBMIT_REPAY_INFO = "hfas/sapi/pre-loan/submit-repay-info";//试算月还款列表,成功会回调
    public static final String QUERY_SUMMARY = "hfas/sapi/v2/index/my/query-summary";//查询还款汇总信息和状态提示
    public static final String QUERY_LOAN_STATUS = "hfas/sapi/v2/index/query-loan-status";//借款申请进度查询(首页订单状态提示条)
    public static final String RECORD_BAR_CLICK = "hfas/sapi/index/record-bar-click";//记录状态栏点击记录
    public static final String GET_BANNER = "/hfas/sapi/cms/get-banner";//获取banner
    public static final String QUERY_WINDOW_STATUS = "/hfas/sapi/contract/query-window-status";//获取确认授权弹框
    public static final String SIGN_CONTRACT_CONFIRM = "/hfas/sapi/contract/sign-contract-confirm";//同意协议并确认授权提交接口
    public static final String QUERY_CONTRACT_CONFIRM = "/hfas/sapi/contract/query-contract-content";//获取合同内容详情
    public static final String COUPON_CLICK = "/hfas/sapi/index/coupon-click";//首页优惠券弹窗点击通知


    /**
     * error code
     */
    public static final int CODE_ERROR_PIC = 802001;//图片验证码不正确
    public static final int CODE_ERROR_PIC_OVER = 802002;//图片验证码已过期
    public static final int CODE_ERROR_MSG_SEND = 802003;//短信验证码已发送，请稍后再试
    public static final int CODE_ERROR_MSG_OVER = 802004;//短信验证码已过期
    public static final int CODE_ERROR_MSG = 802005;//短信验证码不正确
    public static final int CODE_ERROR_CHECK = 802006;//SmsCheck注解需要制定短信业务类型
    public static final int CODE_ERROR_MSG_INPUT = 802007;//请输入图片验证码
    public static final int CODE_ERROR_YUN_PIAN = 802013;//滑块验证码
    public static final int CODE_ERROR_MSG_FACR = 803001;//请FACE++失效
    public static final int CODE_ERROR_MSG_ID = 803002;//请完善身份证信息
    public static final int CODE_ERROR_MSG_AUTH = 803003;//请完善信用授权信息
    public static final int CODE_ERROR_MSG_ING = 803005;//当前有一笔借款正在进行
    public static final int CODE_ERROR_MSG_PERSON = 803006;//请完善个人信息
    public static final int CODE_ERROR_MSG_FAILE = 896999;//错误信息取决于资管系统
    public static final int SUBMIT_TOKEN_ERROR = 800005;//请求处理中，请勿重复提交！
    public static final int UNIDENTIFIED_ERROR = 800001;//访客太多,请稍后重试！
    public static final int DEFAULT2 = 896998;//提交失败，请稍后重试
    public static final int REPAY_STATUS_ERROR = 800007;//提交失败，请稍后重试
    public static final int AFFIRM_BANKCARD_DELETE = 804019;//该开户行卡暂不支持，请更换其他银行卡

    /**
     * h5
     */
    public static final String H5_MORE_BORROW_MONEY_WAYS = "/gateway.html#/apply-failed";//更多借款方式
    public static final String H5_FAQ = "/faq.html#/";//常见问题
    public static final String H5_ACTIVITY = "/activity.html#/center";//活动小组
    public static final String H5_STRATEGY = "/activity.html#/borrow";//借款攻略
    public static final String H5_DISCOVER = "/activity.html#/find";//发现
    public static final String H5_CONTRACT = "/doc.html?applyNo=%s#/doclist/%s";//查看合同列表，第一个参数为标的no，第二一个为请求的类型，regist:注册; application:进件; confirm:确认要款
    public static final String H5_BANNER = "https://p.beneucard.com/#/hyuk";//banner页面
    public static final String ABOUT_US = "/doc.html#/aboutUs";//关于我们
    public static final String H5_CONTRACT_SERVICE = "/doc.html?applyNo=%22%22#/contract/HYYKHJSFWXY/true";//服务协议
    public static final String H5_CONTRACT_PRIVACY = "/doc.html?applyNo=%22%22#/contract/HYYKHJSYSGZ/true";//隐私协议

    /**
     * 公共
     */
    public static final String GEN_SUBMIT_TOKEN = "/hfas/sapi/common/gen-submit-token";//获取防重复提交令牌

    /**
     * 短信验证码登录
     */
    public static final String GET_LOGIN_CODE = "/hfas/sapi/account/get-login-code"; //获取验证码
    public static final String LOGIN_WITH_CODE = "/hfas/sapi/account/login-with-code"; //登录

    /**
     * 销户
     */
    public static final String CANCELLATION = "/hfas/sapi/account/cancellation"; //销户
    public static final String COULD_CANCEL = "/hfas/sapi/account/could-cancel"; //用户是否满足销户条件
    public static final String GEN_CANCEL_SMS = "/hfas/sapi/sms/gen-cancel-sms"; //发送销户短信验证码
}
