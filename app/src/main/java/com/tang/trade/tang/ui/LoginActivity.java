package com.tang.trade.tang.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.callback.JsonCallBack;
import com.tang.trade.tang.net.model.LoginResponseModel;
import com.tang.trade.tang.net.model.RegisterResponseModel;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.NumberUtils;
import com.tang.trade.tang.utils.TLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener {


    EditText etUsername;
    EditText etPassword;
    TextView tvNewAccount;
    ImageView login;
    ImageView back;
    EditText registrationUsername;
    EditText registrationPassword;
    EditText registrationConfirm;
    ImageView register;
    private ViewStub viewStub_login;
    private View view_login;
    private ViewStub viewStub_register;
    private boolean isVisible = false;
    private View view_register;
    private ImageView iv_eye_login;
    private ImageView iv_eye_register_password;
    private ImageView iv_eye_register_confirm_password;
    private ViewStub viewStub_welcome;
    private View view_welcome;
    private TextView tvPwd;
    private ImageView ivEnter;
    private TextView tv_password_cue;
    private TextView tv_confrim_cue;

    String regEx2 = "^[a-z][a-z0-9]{0,20}$";
    Pattern pattern2 = Pattern.compile(regEx2);


    @Override
    protected int getLayoutId() {
        return R.layout.activity_registration;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void initView() {
        setTheme(R.style.FullscreenTheme);

        viewStub_login = (ViewStub) findViewById(R.id.stub_login);
        viewStub_login.setLayoutResource(R.layout.view_login);
        view_login = viewStub_login.inflate();
        viewStub_register = (ViewStub) findViewById(R.id.stub_register);
        viewStub_register.setLayoutResource(R.layout.view_registration);
        view_register = viewStub_register.inflate();
        view_register.setVisibility(View.INVISIBLE);

        viewStub_welcome = (ViewStub) findViewById(R.id.stub_welcome);
        viewStub_welcome.setLayoutResource(R.layout.view_welcome);
        view_welcome = viewStub_welcome.inflate();
        view_welcome.setVisibility(View.INVISIBLE);
        tvPwd = (TextView) view_welcome.findViewById(R.id.tv_pwd);
        ivEnter = (ImageView) view_welcome.findViewById(R.id.iv_enter);

        etUsername = (EditText) view_login.findViewById(R.id.et_username);
        etPassword = (EditText) view_login.findViewById(R.id.et_password);
        tvNewAccount = (TextView) view_login.findViewById(R.id.tv_newAccount);
        login = (ImageView) view_login.findViewById(R.id.login);
        back = (ImageView) view_register.findViewById(R.id.back);
        registrationUsername = (EditText) view_register.findViewById(R.id.registration_username);
        registrationPassword = (EditText) view_register.findViewById(R.id.registration_password);
        registrationConfirm = (EditText) view_register.findViewById(R.id.registration_confirm);
        register = (ImageView) view_register.findViewById(R.id.register);
        tv_password_cue = (TextView) view_register.findViewById(R.id.password_cue);
        tv_password_cue.setVisibility(View.GONE);

        tv_confrim_cue = (TextView) view_register.findViewById(R.id.confirm_cue);
        tv_confrim_cue.setVisibility(View.GONE);
        iv_eye_login = (ImageView) view_login.findViewById(R.id.eye_login);
        iv_eye_register_password = (ImageView) findViewById(R.id.eye_register_password);
        iv_eye_register_confirm_password = (ImageView) findViewById(R.id.eye_register_confirm_password);
        login.setEnabled(false);
        register.setEnabled(false);
        iv_eye_register_password.setOnClickListener(this);
        iv_eye_register_confirm_password.setOnClickListener(this);
        iv_eye_login.setOnClickListener(this);
        tvNewAccount.setOnClickListener(this);
        back.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        ivEnter.setOnClickListener(this);
        etUsername.setOnEditorActionListener(this);
        registrationPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(registrationPassword.getText().toString())) {
                        tv_password_cue.setVisibility(View.VISIBLE);
                    }

                    if (NumberUtils.isLetterDigit(registrationPassword.getText().toString())) {
                        tv_password_cue.setVisibility(View.GONE);
                    } else {
                        tv_password_cue.setVisibility(View.VISIBLE);
                    }

                    if (TextUtils.isEmpty(registrationConfirm.getText().toString())) {
                        tv_confrim_cue.setVisibility(View.GONE);
                    } else {
                        if (isTheSamePassword()) {
                            tv_confrim_cue.setVisibility(View.GONE);
                        } else {
                            tv_confrim_cue.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        registrationConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(registrationConfirm.getText().toString())) {
                        tv_password_cue.setVisibility(View.GONE);
                    }else{
                        if (isTheSamePassword()) {
                            tv_confrim_cue.setVisibility(View.GONE);
                        } else {
                            tv_confrim_cue.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                }


            }
        });

        registrationConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.equals(registrationConfirm.getText().toString(), registrationPassword.getText().toString())) {
                    tv_confrim_cue.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(registrationConfirm.getText().toString())) {
                        tv_confrim_cue.setVisibility(View.VISIBLE);
                    } else {
                        tv_confrim_cue.setVisibility(View.GONE);
                    }
                } else {
                    tv_confrim_cue.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registrationPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (NumberUtils.isLetterDigit(s.toString())) {
                    tv_password_cue.setVisibility(View.GONE);
                } else {
                    tv_password_cue.setVisibility(View.VISIBLE);
                }

                if (isTheSamePassword()) {
                    tv_confrim_cue.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(registrationConfirm.getText().toString())) {
                        tv_confrim_cue.setVisibility(View.VISIBLE);
                    } else {
                        tv_confrim_cue.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TLog.log("onTextChangedCharSequence" + s);
                TLog.log("onTextChangedstart" + start);
                TLog.log("onTextChangedbefore" + before);
                TLog.log("onTextChangedcount" + count);
                TLog.log("s.length" + s.length());
                if (String.valueOf(s).length() >= 6) {
                    login.setEnabled(true);
                } else {
                    login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registrationPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TLog.log("onTextChangedCharSequence" + s);
                TLog.log("onTextChangedstart" + start);
                TLog.log("onTextChangedbefore" + before);
                TLog.log("onTextChangedcount" + count);
                TLog.log("s.length" + s.length());
                if (s.length() >= 8) {
                    register.setEnabled(true);
                } else {
                    register.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public boolean isValideRegisterPassword() {
        String registerConfirm = registrationConfirm.getText().toString();
        String registerPassword = registrationPassword.getText().toString();
        Matcher matcher1 = pattern2.matcher(registerConfirm);
        Matcher matcher2 = pattern2.matcher(registerPassword);
        return matcher1.matches() && matcher2.matches();
    }

    public boolean isTheSamePassword() {
        String registerConfirm = registrationConfirm.getText().toString();
        String registerPassword = registrationPassword.getText().toString();
        return TextUtils.equals(registerConfirm, registerPassword);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_username:
                break;
            case R.id.et_password:
                break;
            case R.id.tv_newAccount:
                log("tv_newAccount");
                view_login.setVisibility(View.INVISIBLE);
                view_register.setVisibility(View.VISIBLE);
                tv_password_cue.setVisibility(View.GONE);

                break;
            case R.id.login:
                if (!checkLoginIsNull()) {
                    login();
                }
                break;
            case R.id.back:

                view_login.setVisibility(View.VISIBLE);
                view_register.setVisibility(View.INVISIBLE);
                tv_password_cue.setVisibility(View.GONE);
                tv_confrim_cue.setVisibility(View.GONE);
                clearRegister();
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                break;
            case R.id.registration_username:
                break;
            case R.id.registration_password:
                break;
            case R.id.registration_confirm:
                break;
            case R.id.register:
                if (!checkRegisIsNull() && checkConfirmPassword()) {
                    register();
                }
                break;
            case R.id.eye_login:
                if (iv_eye_login.isSelected()) {
                    iv_eye_login.setSelected(false);
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    iv_eye_login.setSelected(true);
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.eye_register_password:
                if (iv_eye_register_password.isSelected()) {
                    iv_eye_register_password.setSelected(false);
                    registrationPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    iv_eye_register_password.setSelected(true);
                    registrationPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.eye_register_confirm_password:
                if (iv_eye_register_confirm_password.isSelected()) {
                    iv_eye_register_confirm_password.setSelected(false);
                    registrationConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    iv_eye_register_confirm_password.setSelected(true);
                    registrationConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.iv_enter:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                MyApp.set(BuildConfig.USERNAME, registrationUsername.getText().toString());
                finish();
                break;
        }
    }


    private boolean checkConfirmPassword() {
        boolean equals = TextUtils.equals(registrationPassword.getText().toString(), registrationConfirm.getText().toString());
        if (!equals) {
            MyApp.showToast(getString(R.string.shouldBeSame));
        }
        return equals;
    }

    private void register() {


        Matcher matcher = pattern2.matcher(registrationUsername.getText().toString());
        boolean b = matcher.matches();
        if (b == true) {
            if (NumberUtils.isLetterDigit(registrationConfirm.getText().toString())) {
                TangApi.register(registrationUsername.getText().toString()
                        , registrationConfirm.getText().toString()
                        , new JsonCallBack<RegisterResponseModel>(RegisterResponseModel.class) {

                            @Override
                            public void onSuccess(Response<RegisterResponseModel> response) {
                                RegisterResponseModel registerResponseModel = response.body();
                                if (registerResponseModel.getStatus().equals("success")) {
//                            MyApp.showToast(getString(R.string.registersuccess));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            view_login.setVisibility(View.GONE);
                                            view_register.setVisibility(View.GONE);
                                            view_welcome.setVisibility(View.VISIBLE);
                                            tvPwd.setText(registrationConfirm.getText().toString());
                                        }
                                    });

//                            clearRegister();
                                } else if (registerResponseModel.getStatus().equals("failure")) {
                                    if (registerResponseModel.getMsg().equals("exists")) {
                                        MyApp.showToast(getString(R.string.exists));
                                    }
                                }
                            }
                        });
            } else {
                MyApp.showToast(getString(R.string.PwdError));
            }


        } else {
            MyApp.showToast(getString(R.string.userNameError));
        }

    }

    private void login() {
        TangApi.login(etUsername.getText().toString(), etPassword.getText().toString(), new JsonCallBack<LoginResponseModel>(LoginResponseModel.class) {

            @Override
            public void onSuccess(Response<LoginResponseModel> response) {
                LoginResponseModel loginResponseModel = response.body();
                if (loginResponseModel.getStatus().equals("success")) {
                    MyApp.set(BuildConfig.USERNAME, etUsername.getText().toString());
//                    MyApp.showToast(getString(R.string.success));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else if (loginResponseModel.getStatus().equals("failure")) {
                    MyApp.showToast(getString(R.string.failure));
                }

            }

            @Override
            public void onError(Response<LoginResponseModel> response) {
                super.onError(response);
                MyApp.showToast(getString(R.string.failure));
            }
        });
    }

    private boolean checkLoginIsNull() {
        if (TextUtils.isEmpty(etUsername.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())) {
            MyApp.showToast(getString(R.string.cannotbenull));
            return true;
        }
        return false;
    }

    private boolean checkRegisIsNull() {
        if (TextUtils.isEmpty(registrationUsername.getText().toString()) || TextUtils.isEmpty(registrationConfirm.getText().toString()) || TextUtils.isEmpty(registrationPassword.getText().toString())) {
            MyApp.showToast(getString(R.string.cannotbenull));
            return true;
        }
        return false;
    }


//    private void startFlipAnim() {
//        ViewPropertyAnimator viewPropertyAnimator = view.animate();
//        viewPropertyAnimator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float f = (float) valueAnimator.getAnimatedValue();
//                if (NumberUtils.compareFloatAndInt(f, 90)) {
//                    viewStub.setLayoutResource(R.layout.activity_registration);
//                    viewStub.inflate();
//                }
//            }
//        });
//        viewPropertyAnimator.rotationYBy(180).setDuration(1000).start();
//        log("startFlipAnim");
//    }


    public void log(String log) {
        TLog.log(log);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }

    private void clearRegister() {
        registrationConfirm.setText("");
        registrationUsername.setText("");
        registrationPassword.setText("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)   //可读
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  //可写
                            != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限

//                try{
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
//                    ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},2);
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
//                }catch(Exception e){
////                    Toast.makeText(this, "异常"+e.toString(),Toast.LENGTH_LONG).show();
//                }

                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

            }
        }

    }
}
