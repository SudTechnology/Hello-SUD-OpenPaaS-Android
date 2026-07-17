package global.sud.gi.hello.game.pay;

import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONObject;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.SUDGIWrapper.custom.base.utils.IntentUtils;
import global.sud.gi.hello.app.common.http.param.BaseResponse;
import global.sud.gi.hello.app.common.http.rx.RxCallback;
import global.sud.gi.hello.app.service.MainRepository;
import global.sud.gi.hello.app.service.resp.CreateOrderResp;
import global.sud.gi.hello.app.service.resp.PaymentResp;
import global.sud.gi.hello.app.service.resp.QueryOrderResp;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.wrapped.SUDOPRequestPaymentParams;

public class PayManager {

    private FragmentActivity activity;
    private BaseGameViewModel gameViewModel;

    public PayManager(FragmentActivity activity, BaseGameViewModel gameViewModel) {
        this.activity = activity;
        this.gameViewModel = gameViewModel;
    }

    public void requestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        MainRepository.createOrder(activity, gameViewModel.getAppId(), gameViewModel.getUserId(), params.signData, params.signature, new RxCallback<CreateOrderResp>() {
            @Override
            public void onNext(BaseResponse<CreateOrderResp> t) {
                super.onNext(t);
                if (t.getRet_code() == 0) {
                    if (t.getData() != null && t.getData().is_valid) {
                        showPaymentDialog(handle, params);
                    } else {
                        handle.failure(-1, "validate payment fail");
                    }
                } else {
                    handle.failure(t.getRet_code(), t.getRet_msg());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                handle.failure(-1, "validate payment net fail:" + e);
            }
        });
    }

    private void showPaymentDialog(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        PaymentDialog dialog = new PaymentDialog(params);
        dialog.setPaymentListener(new PaymentDialog.PaymentListener() {
            @Override
            public void paymentOperate(boolean isPayment) {
                if (isPayment) {
                    MainRepository.payment(activity, params.sudTradeNo, new RxCallback<PaymentResp>() {
                        @Override
                        public void onNext(BaseResponse<PaymentResp> t) {
                            super.onNext(t);
                            String payUrl = t.getData() == null ? null : t.getData().pay_url;
                            if (t.getRet_code() == 0 && !TextUtils.isEmpty(payUrl)) {
                                IntentUtils.openUrl(activity, payUrl);
                            } else {
                                handle.failure(-1, "app server fail,code:" + t.getRet_code() + " pay_url:" + payUrl);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            handle.failure(-1, "payment net fail:" + e);
                        }
                    });
                } else {
                    handle.failure(-1, "user cancel");
                    dialog.dismiss();
                }
            }

            @Override
            public void payemntCompleted() {
                MainRepository.queryOrder(activity, params.sudTradeNo, new RxCallback<QueryOrderResp>() {
                    @Override
                    public void onNext(BaseResponse<QueryOrderResp> t) {
                        super.onNext(t);
                        int orderStatus = t.getData() == null ? 0 : t.getData().order_status;
                        if (t.getRet_code() == 0) {
                            if (orderStatus == 3) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("sudTradeNo", params.sudTradeNo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                handle.success(jsonObject.toString());
                            } else {
                                handle.failure(-1, "payment fail,orderStatus:" + orderStatus);
                            }
                        } else {
                            handle.failure(-1, "queryOrder app server fail,code:" + t.getRet_code());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        handle.failure(-1, "user cancel");
                    }
                });
            }
        });
        dialog.show(activity.getSupportFragmentManager(), null);
    }

}
