package com.hfax.ucard.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hfax.app.BaseFragment;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.bean.CanChangeDebitBean;
import com.hfax.ucard.modules.user.AddBankCardActivity;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 银行卡切换对话框
 *
 * @author SongGuangyao
 * @date 2018/5/4
 */

public class CardChangeDialog extends Dialog {
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.title)
    TextView tvTitle;
    private String content, title;

    private OnClickListen listener;

    public CardChangeDialog(@NonNull Context context, String content) {
        super(context, R.style.BaseCustomAlertDialog);
        this.content = content;
    }

    public CardChangeDialog(@NonNull Context context, String title, String content) {
        super(context, R.style.BaseCustomAlertDialog);
        this.content = content;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_card_change);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    @OnClick(R.id.tv_next)
    public void onViewClicked() {
        if (listener != null) {
            listener.onClick();
        }
    }


    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    public void setListener(OnClickListen listener) {
        this.listener = listener;
    }

    public interface OnClickListen {
        /**
         * 点击
         */
        void onClick();
    }

    public static void checkBank(final BaseFragment fragment, final BorrowDetails mBorrowDetails, final ActivityCallbackUtils.Callback... callbacks) {
        fragment.showLoadingDialog();
        RequestMap requestMap = new RequestMap(NetworkAddress.CAN_CHANGE_DEBIT);
        final String bankCardType;
        switch (mBorrowDetails.orderType) {
            default:
            case 1:
                bankCardType = "DEBIT";
                break;
            case 2:
                bankCardType = "PLATFORM";
                break;
        }
        requestMap.put("bankCardType", bankCardType);
        requestMap.put("applyNo", mBorrowDetails.applyNo);
        new SimplePresent().request(requestMap, fragment, MVPUtils.Method.GET, new SimpleViewImpl<CanChangeDebitBean>() {
            @Override
            public void onSuccess(CanChangeDebitBean canChangeDebitBean) {
                fragment.dismissLoadingDialog();
                if (canChangeDebitBean.canChange) {
                    final CardChangeDialog dialog = new CardChangeDialog(fragment.getActivity(), mBorrowDetails.orderType == 2 ? fragment.getString(R.string.change_card) : null);
                    dialog.show();
                    dialog.setListener(new OnClickListen() {
                        @Override
                        public void onClick() {
                            switch (mBorrowDetails.orderType) {
                                default:
                                case 1:
                                    AddBankCardActivity.start(fragment.getActivity(), BankCardBean.TYPE_CHANGE_DEBIT,callbacks);
                                    break;
                                case 2:
                                    AddBankCardActivity.start(fragment.getActivity(), BankCardBean.TYPE_CHANGE_PLATFORM, mBorrowDetails.applyNo,callbacks);
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    UCardUtil.showToast(fragment.getActivity(),canChangeDebitBean.reason);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                fragment.dismissLoadingDialog();
                UCardUtil.showToast(fragment.getActivity(),msg);
            }
        });
    }
}
