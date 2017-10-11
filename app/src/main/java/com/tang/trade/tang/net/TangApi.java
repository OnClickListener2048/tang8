package com.tang.trade.tang.net;

import android.Manifest;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.callback.JsonCallBack;
import com.tang.trade.tang.net.model.AssetResponseModel;
import com.tang.trade.tang.net.model.BlockChainResponModel;
import com.tang.trade.tang.net.model.BlockResponModel;
import com.tang.trade.tang.net.model.HistoryResponseModel;
import com.tang.trade.tang.net.model.ServiceChargeResponseModel;
import com.tang.trade.tang.net.model.UpdateResponseModel;
import com.tang.trade.tang.utils.Device;
import com.tang.trade.tang.utils.TLog;

import java.io.File;


import static com.tang.trade.tang.net.TangConstant.URL;
import static com.tang.trade.tang.net.TangConstant.URL_UPDATE;

/**
 * Created by dagou on 2017/9/21.
 */

public abstract class TangApi {




    public static void login(String username, String password, Callback callback) {
        OkGo
                .<String>post(URL)
                .upJson(JsonCreator.loginJsonCreator(username, password))
                .execute(callback);
    }

    public static void register(String username, String password, Callback callback) {
        OkGo.<String>post(URL)
                .upJson(JsonCreator.RegisterJsonCreator(username, password))
                .execute(callback);
    }

    public static HistoryResponseModel getHistory(String name, final BaseViewCallback baseView) {
        OkGo.<HistoryResponseModel>post(URL)
                .upJson(JsonCreator.HistoryJsonCreator(name))
                .execute(new JsonCallBack<HistoryResponseModel>(HistoryResponseModel.class) {
                    @Override
                    public void onSuccess(Response<HistoryResponseModel> response) {
                        HistoryResponseModel historyResponseModel = response.body();
                        TLog.log(historyResponseModel.getStatus() + "=historyResponseModel.getStatus()");
                        if (TextUtils.equals(historyResponseModel.getStatus(), "success")) {
                            TLog.log("interface");
                            baseView.setData(historyResponseModel);
                        }
                    }
                });


        return null;

    }

    public static void getBalance(String name, final BaseViewCallback baseViewCallback) {
        OkGo.<AssetResponseModel>post(URL)
                .upJson(JsonCreator.BalanceJsonCreator(name))
                .execute(new JsonCallBack<AssetResponseModel>(AssetResponseModel.class) {
                    @Override
                    public void onSuccess(Response<AssetResponseModel> response) {
                        if (TextUtils.equals(response.body().getStatus(), "success") && response.body().getData() != null) {
                            baseViewCallback.setData(response.body());
                        }
                    }

                    @Override
                    public void onError(Response<AssetResponseModel> response) {
                        super.onError(response);
                        TLog.log(response.message());
                    }
                });
    }

    public static void receptionRecord(String name, Callback callback) {
        OkGo.<String>post(URL)
                .upJson(JsonCreator.receptionRecordJsonCreator(name))
                .execute(callback);
    }

    /**
     * @param from      发送人账号
     * @param to        接收人账号
     * @param amount    发送额度
     * @param symbol    币种（BTS）
     * @param memo      备注（Endorsement of transfer）
     * @param broadcast 是否广播（true）     token算法：Base64（SHA256（from+to+amount+symbol+memo+broadcast））
     * @param token     token：令牌；
     * @param callback
     */
    public static void sendOut(String from, String to, String amount, String symbol, String memo, String broadcast, String token, Callback callback) {
        OkGo.<String>post(URL)
                .upJson(JsonCreator.sendOutJsonCreator(from, to, amount, symbol, memo, broadcast, token))
                .execute(callback);
    }

    public static void accountYuE(String username, Callback callback) {
        OkGo.<String>post(URL)
                .upJson(JsonCreator.accountYuEJsonCreator(username))
                .execute(callback);
    }

    public static void getBlockChainInfo(final BaseViewCallback baseViewCallback) {
        OkGo.<BlockChainResponModel>post(URL)
                .upJson(JsonCreator.blockChainInfoCreator())
                .execute(new JsonCallBack<BlockChainResponModel>(BlockChainResponModel.class) {
                    @Override
                    public void onSuccess(Response<BlockChainResponModel> response) {
                        BlockChainResponModel blockChainResponModel = response.body();


                        if (blockChainResponModel != null) {
                            TLog.log("blockChainResponModel回掉了");
                            baseViewCallback.setData(blockChainResponModel);
                        }
                    }
                });
    }

    public static void getBlockInfo(String page, final BaseViewCallback baseViewCallback) {
        OkGo.<BlockResponModel>post(URL)
                .upJson(JsonCreator.blockInfoCreator(page))
                .execute(new JsonCallBack<BlockResponModel>(BlockResponModel.class) {
                    @Override
                    public void onSuccess(Response<BlockResponModel> response) {
                        BlockResponModel blockResponModel = response.body();
                        if (TextUtils.equals(blockResponModel.getStatus(), "success")) {
                            baseViewCallback.setData(blockResponModel);
                        }
                    }
                });
    }

    public static void getServiceCharge(String id, final BaseViewCallback baseViewCallback) {
        OkGo.<ServiceChargeResponseModel>post(URL)
                .upJson(JsonCreator.ServiceChargeJsonCreator(id))
                .execute(new JsonCallBack<ServiceChargeResponseModel>(ServiceChargeResponseModel.class) {
                    @Override
                    public void onSuccess(Response<ServiceChargeResponseModel> response) {
                        ServiceChargeResponseModel serviceChargeResponseModel = response.body();
                        if (serviceChargeResponseModel.getStatus().equals("success")) {
                            baseViewCallback.setData(serviceChargeResponseModel);
                        }
                    }
                });
    }

    public static void getUpdateInfo(final BaseViewCallback baseViewCallback) {
        OkGo.<UpdateResponseModel>get(URL_UPDATE)
                .execute(new JsonCallBack<UpdateResponseModel>(UpdateResponseModel.class) {

                    @Override
                    public void onSuccess(Response<UpdateResponseModel> response) {
                        if (response.body()!= null) {
                            baseViewCallback.setData(response.body());
                        }
                    }
                });
    }
    public static void downloadApk(String url ,final BaseViewCallBackWithProgress baseViewCallback) {
        if (Device.sdcardExit()) {
            OkGo.<File>get(url).execute(new FileCallback(Device.DEFAULT_SAVE_FILE_PATH ,"Borderless.apk"){
                @Override
                public void onSuccess(Response<File> response) {
                    if (response.body() != null) {
                        baseViewCallback.setData(response.body());
                    }
                }

                @Override
                public void downloadProgress(Progress progress) {
                    super.downloadProgress(progress);
                    baseViewCallback.setProgress(progress);
                }

                @Override
                public void onStart(Request<File, ? extends Request> request) {
                    super.onStart(request);
                    baseViewCallback.onStart(request);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    baseViewCallback.onFinish();
                }
            });
        }

    }


    public interface BaseViewCallback<T> {
        void setData(T t);
    }

    public interface BaseViewCallBackWithProgress<T>extends BaseViewCallback<T>{
        void setProgress(Progress progress);

        void onStart(Request<File, ? extends Request> request);

        void onFinish();

    }

}
