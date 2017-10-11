package com.tang.trade.tang.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.lzy.okgo.model.Response;
import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.adapter.ReceptionAdapter;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.callback.JsonCallBack;
import com.tang.trade.tang.net.model.ReceptionRecordResponseModel;
import com.tang.trade.tang.net.model.RegisterResponseModel;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/22.
 */

public class ReceptionActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.lv_reception_record)
    ListView lvReceptionRecord;

    private List<ReceptionRecordResponseModel.NewObject> data = new ArrayList<ReceptionRecordResponseModel.NewObject>();
    private ReceptionAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reception;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void initView() {
        ivBack.setOnClickListener(this);
        adapter = new ReceptionAdapter(this, data);
        lvReceptionRecord.setAdapter(adapter);
    }

    @Override
    public void initData() {
        String username = MyApp.get(BuildConfig.USERNAME, "no_username");
        if (username != "no_username") {
            TangApi.receptionRecord(username, new JsonCallBack<ReceptionRecordResponseModel>(ReceptionRecordResponseModel.class) {

                @Override
                public void onSuccess(Response<ReceptionRecordResponseModel> response) {
                    ReceptionRecordResponseModel receptionRecordResponseModel = response.body();
                    if (receptionRecordResponseModel.getStatus().equals("success")) {
                        if (receptionRecordResponseModel.getData() != null) {
                            adapter.setData(receptionRecordResponseModel.getData());
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        MyApp.showToast(receptionRecordResponseModel.getMsg());
                    }
                }
            });
        }


    }
}
