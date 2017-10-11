package com.tang.trade.tang.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tang.trade.tang.MyApp;
import com.tang.trade.tang.R;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.model.AssetResponseModel;
import com.tang.trade.tang.ui.WalletActivity;
import com.tang.trade.tang.utils.BuildConfig;
import com.tang.trade.tang.utils.TLog;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements TangApi.BaseViewCallback<AssetResponseModel>, View.OnClickListener {



    private TextView tvBDS;
    private TextView tvBTC;
    private TextView tvETH;
    private TextView tvREP;
    private TextView tvAsset;
    private ImageView ivWallet;
    private String username;
    private TextView tvUsername;
    private TextView tvCNY;
    private TextView tvUSD;
//    private  HomeFragment homeFragment;

    public HomeFragment() {
        // Required empty public constructor
    }

//    public  HomeFragment newInstance() {
//        if (homeFragment == null){
//            new HomeFragment();
//        }
//        return homeFragment;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        username = MyApp.get(BuildConfig.USERNAME, "no_username");
        return inflater.inflate(R.layout.fragment_home, null);
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
            TangApi.getBalance(username, this);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (username != null) {
            TLog.log("我拿到了username" + username);
            TangApi.getBalance(username, this);
        }
    }

    private void initView(View v) {
        tvAsset = (TextView) v.findViewById(R.id.assets);
        tvCNY = (TextView) v.findViewById(R.id.cny);
        tvUSD = (TextView) v.findViewById(R.id.usd);
        tvBDS = (TextView) v.findViewById(R.id.bds);
        tvBTC = (TextView) v.findViewById(R.id.btc);
        tvETH = (TextView) v.findViewById(R.id.eth);
        ivWallet = (ImageView) v.findViewById(R.id.wallet);
        tvUsername = (TextView) v.findViewById(R.id.username);
        ivWallet.setOnClickListener(this);

    }

    @Override
    public void setData(AssetResponseModel assetResponseModel) {
        if (assetResponseModel != null) {
            if (assetResponseModel.getData() == null || assetResponseModel.getData().size() == 0) {
                tvAsset.setText("0.00000");
                tvBDS.setText("0.00000");
                return;
            }
            long amount = assetResponseModel.getData().get(0).getAmount();
            DecimalFormat decimalFormat = new DecimalFormat("#0.00000");
            tvAsset.setText(decimalFormat.format((double) amount / 100000));
            tvBDS.setText(decimalFormat.format((double) amount / 100000));
        }else{
            tvAsset.setText("0.00000");
            tvBDS.setText("0.00000");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wallet:
                startActivity(new Intent(getActivity(), WalletActivity.class));
                break;
        }
    }
}
