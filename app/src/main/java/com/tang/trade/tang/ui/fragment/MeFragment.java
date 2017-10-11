package com.tang.trade.tang.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.adapter.HomeListAdapter;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.model.HistoryResponseModel;
import com.tang.trade.tang.ui.LoginActivity;
import com.tang.trade.tang.ui.NewReceptionActivity;
import com.tang.trade.tang.ui.SendOutActivity;
import com.tang.trade.tang.ui.SettingActivity;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.TLog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements TangApi.BaseViewCallback<HistoryResponseModel>, View.OnClickListener {


    private Button btn_reception;
    private Button btn_sendout;
    private ListView listView;
    private String username;
    private TextView tvUsername;
    private ImageView ivExit;
    private ImageView ivRefresh;
    private View inflate;
//    private MeFragment meFragment;

    public MeFragment() {
        // Required empty public constructor
    }

//    public MeFragment newInstance() {
//        if (meFragment == null){
//            meFragment = new MeFragment();
//        }
//        return meFragment;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        username = MyApp.get(BuildConfig.USERNAME, "no_username");
        return inflater.inflate(R.layout.fragment_me, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();


    }

    private void initData() {

        if (username != null) {
            TLog.log("我拿到了username" + username);
            TangApi.getHistory(username, this);
        }

    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.listview);
        btn_reception = (Button) view.findViewById(R.id.btn_reception);
        btn_sendout = (Button) view.findViewById(R.id.btn_sendout);
        ivExit = (ImageView) view.findViewById(R.id.exit);
        tvUsername = (TextView) view.findViewById(R.id.username);
        tvUsername.setText(username);
        ivExit.setOnClickListener(this);
        btn_reception.setOnClickListener(this);
        btn_sendout.setOnClickListener(this);
        inflate = View.inflate(getActivity(), R.layout.view_home_recycler_header, null);
        listView.addHeaderView(inflate);

    }


    @Override
    public void setData(HistoryResponseModel historyResponseModel) {
        TLog.log("回掉了");
        listView.setAdapter(new HomeListAdapter(historyResponseModel.getData()));

        ivRefresh = (ImageView) inflate.findViewById(R.id.refresh);
        ivRefresh.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reception:
                startActivity(new Intent(getContext(), NewReceptionActivity.class));
                break;
            case R.id.btn_sendout:
                startActivity(new Intent(getContext(), SendOutActivity.class));
                break;
            //设置
            case R.id.exit:
//                showDialog();
                startActivity(new Intent(getActivity(), SettingActivity.class));

                break;
            case R.id.refresh:
                refresh();
                break;
        }
    }

    private void showDialog() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder
  */
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Attention);
        builder.setMessage(R.string.exit);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApp.set(BuildConfig.USERNAME, "no_username");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (username != null) {
            TLog.log("我拿到了username" + username);
            TangApi.getHistory(username, this);
        }
    }
}
