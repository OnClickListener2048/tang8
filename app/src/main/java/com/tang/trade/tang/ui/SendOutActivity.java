package com.tang.trade.tang.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.callback.JsonCallBack;
import com.tang.trade.tang.net.model.AccountYuEResponseModel;
import com.tang.trade.tang.net.model.LoginResponseModel;
import com.tang.trade.tang.net.model.ServiceChargeResponseModel;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.TLog;
import com.tang.trade.tang.zxing.android.CaptureActivity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/22.
 */

public class SendOutActivity extends BaseActivity {

    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final int REQURE_CODE_SCAN = 100;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_balance1)
    TextView etBalance1;
    @BindView(R.id.et_balance2)
    EditText etBalance2;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.tv_send_out)
    TextView tvSendOut;
    @BindView(R.id.servicecharge)
    TextView tvServiceCharge;
    @BindView(R.id.qr)
    ImageView qr;
    private String username;
    private PopupWindow window;
    private View view;
    int i =0;
    private ServiceChargeResponseModel serviceChargeResponseModel1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sendout;
    }

    @Override
    public void initView() {
        setPricePoint(etAmount);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)  //打开相机权限
                    != PackageManager.PERMISSION_GRANTED ) {
                //申请WRITE_EXTERNAL_STORAGE权限

//                try{
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
//
//          ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},2);
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
//                }catch(Exception e){
////                    Toast.makeText(this, "异常"+e.toString(),Toast.LENGTH_LONG).show();
//                }

                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        2);

            }
        }

    }

    @Override
    public void initData() {
        qr.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvSendOut.setOnClickListener(this);
        username = MyApp.get(BuildConfig.USERNAME, "no_username");
        TangApi.getServiceCharge("0", new TangApi.BaseViewCallback<ServiceChargeResponseModel>() {

            @Override
            public void setData(ServiceChargeResponseModel serviceChargeResponseModel) {
                serviceChargeResponseModel1 = serviceChargeResponseModel;
                if (serviceChargeResponseModel.getData() != null && serviceChargeResponseModel.getData().size() != 0) {
                    tvServiceCharge.setText(getString(R.string.change) + serviceChargeResponseModel.getData().get(0).getFee() + " BDS");
                }
            }
        });

        TangApi.accountYuE(username, new JsonCallBack<AccountYuEResponseModel>(AccountYuEResponseModel.class) {

            @Override
            public void onSuccess(Response<AccountYuEResponseModel> response) {
                AccountYuEResponseModel accountYuEResponseModel = response.body();
                if (accountYuEResponseModel.getStatus().equals("success")) {
                    if (null != accountYuEResponseModel.getData() || accountYuEResponseModel.getData().size() != 0) {
                        if (accountYuEResponseModel.getData() == null || accountYuEResponseModel.getData().size() == 0) {
                            TLog.log("accountYuEResponseModel.getData()" + accountYuEResponseModel.getData());
                            TLog.log("accountYuEResponseModel.getData()" + accountYuEResponseModel.getData().size());
                            etBalance1.setText("0.00000 BDS");
                            return;
                        }
                        double amount = Double.parseDouble(accountYuEResponseModel.getData().get(0).getAmount());
                        BigDecimal big1 = new BigDecimal(amount);
                        BigDecimal big2 = new BigDecimal(100000);
                        BigDecimal shang = big1.divide(big2, 5, BigDecimal.ROUND_HALF_UP);//保留5位小数，返回bigDecimal
                        etBalance1.setText(shang + " BDS");
                    } else {
                        etBalance1.setText("0.000000 BDS");
                    }
                }

            }
        });

        Log.e("sha_256", shaEncrypt("wuwenkai") + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_send_out:
                if (TextUtils.isEmpty(etBalance2.getText().toString().trim())) {
                    MyApp.showToast(R.string.warmUser);
                    return;
                }
                if (TextUtils.isEmpty(etAmount.getText().toString().trim())||Double.valueOf(etAmount.getText().toString().trim())==0) {
                    MyApp.showToast(R.string.warmAmount);
                    return;
                }

                showDialog();

                break;
            case R.id.qr:

                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQURE_CODE_SCAN);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // CameraManager必须在这里初始化，而不是在onCreate()中。
                    // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
                    // 当扫描框的尺寸不正确时会出现bug

                } else {
                    if (i==0) {
                        MyApp.showToast(R.string.permissionsError);
                        i++;
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQURE_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            TLog.log("qr success");
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                etBalance2.setText(content);
            }
        }
    }

    /**
     * SHA加密
     *
     * @param strSrc 明文
     * @return 加密之后的密文
     */
    public static String shaEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * byte数组转换为16进制字符串
     *
     * @param bts 数据源
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    // 加密
    public static String getBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 5) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 6);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showDialog() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder
  */
        String str =getString(R.string.isconfirm) + etBalance2.getText().toString().trim() + "\n" + getString(R.string.Amount) + etAmount.getText().toString().trim();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.attention1);
        builder.setMessage(str);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BigDecimal big1 = new BigDecimal(etAmount.getText().toString().trim());
                TLog.log(big1.toString());
                BigDecimal big2 = new BigDecimal(etBalance1.getText().toString().substring(0,etBalance1.getText().toString().length()-4));
                TLog.log(big2.toString());
                if (big2.subtract(big1.add(new BigDecimal(serviceChargeResponseModel1.getData().get(0).getFee()))).doubleValue()>=0){
                    if (!username.equals(etBalance2.getText().toString().trim())){
                        sendout();
                    }else{
                        MyApp.showToast(R.string.myself);
                    }

                }else{
                    MyApp.showToast(R.string.priceInfo);
                }

            }
        });
        builder.show();
    }

    public void sendout() {
        /**
         * @param from   发送人账号
         * @param to         接收人账号
         * @param amount    发送额度
         * @param symbol         币种（BTS）
         * @param memo       备注（Endorsement of transfer）
         * @param broadcast    是否广播（true）     token算法：Base64（SHA256（from+to+amount+symbol+memo+broadcast））
         * @param token     token：令牌；
         * @param callback
         */
        TangApi.sendOut(username
                , etBalance2.getText().toString().trim()
                , etAmount.getText().toString().trim()
                , "BTS"
                , ""
                , "True"
                , getBase64(shaEncrypt(username + etBalance2.getText().toString().trim() + etAmount.getText().toString().trim() + "BTS" + "" + "True"))
                , new JsonCallBack<LoginResponseModel>(LoginResponseModel.class) {

                    @Override
                    public void onSuccess(Response<LoginResponseModel> response) {
                        LoginResponseModel model = response.body();
                        if (model.getStatus().equals("success")) {
                            MyApp.showToast(getString(R.string.outsuccess));
                            finish();
                        } else {
                            MyApp.showToast(getString(R.string.nousername));
                        }
                    }
                });
    }

    public void show() {

        Button btnCencel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnCommit = (Button) view.findViewById(R.id.btn_commit);

        btnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                window.dismiss();

            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                window.dismiss();
            }
        });

        if (!window.isShowing()) {
            // 设置背景颜色变暗
            WindowManager.LayoutParams lp5 = getWindow().getAttributes();
            lp5.alpha = 0.5f;
            getWindow().setAttributes(lp5);
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp3 = getWindow().getAttributes();
                    lp3.alpha = 1f;
                    getWindow().setAttributes(lp3);
                }
            });

            window.setOutsideTouchable(true);

            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0x00ffffff);
            window.setBackgroundDrawable(dw);

            // 在底部显示
            window.showAtLocation(ivBack,
                    Gravity.BOTTOM, 0, 0);
        }
    }

}



