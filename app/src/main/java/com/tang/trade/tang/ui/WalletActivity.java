package com.tang.trade.tang.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tang.trade.tang.R;
import com.tang.trade.tang.adapter.WalletListAdapter;
import com.tang.trade.tang.net.TangApi;
import com.tang.trade.tang.net.model.BlockChainResponModel;
import com.tang.trade.tang.net.model.BlockResponModel;
import com.tang.trade.tang.ui.base.BaseActivity;
import com.tang.trade.tang.utils.Device;
import com.tang.trade.tang.utils.TLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dagou on 2017/9/22.
 */

public class WalletActivity extends BaseActivity {

    private TextView tvBlock;
    private TextView tvHeadBlock;
    private TextView tvActive;
    private TextView tvNexƒtMaintance;
    private TextView tvAtiveCommitee;
    private ListView listView;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            TangApi.getBlockChainInfo(new TangApi.BaseViewCallback<BlockChainResponModel>() {

                @Override
                public void setData(BlockChainResponModel blockChainResponModel) {
                    TLog.log("WalletActivity接受到了数据");
                    if (blockChainResponModel.getData() == null || blockChainResponModel.getData().size() == 0) {
                        return;
                    }

                    BlockChainResponModel.DataBean dataBean = blockChainResponModel.getData().get(0);

                    tvBlock.setText("#"+String.valueOf(dataBean.getHead_block_num()));
                    tvHeadBlock.setText(String.valueOf(dataBean.getHead_block_age()));
                    tvAtiveCommitee.setText(String.valueOf(dataBean.getActive_committee_members().size()));
                    tvActive.setText(String.valueOf(dataBean.getActive_witnesses().size()));
                    tvNexƒtMaintance.setText(dataBean.getNext_maintenance_time());
                    TangApi.getBlockInfo(String.valueOf(dataBean.getHead_block_num()), new TangApi.BaseViewCallback<BlockResponModel>() {

                        @Override
                        public void setData(BlockResponModel blockResponModel) {
                            listView.setAdapter(new WalletListAdapter(blockResponModel.getData()));
                        }
                    });

                }
            });


        }
    };
    private Timer timer;
    private ImageView ivBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backwallet:
                this.finish();
                break;
        }
    }

    @Override
    public void initView() {
        ivBack = (ImageView) findViewById(R.id.backwallet);
        tvBlock = (TextView) findViewById(R.id.currentblock);
        tvHeadBlock = (TextView) findViewById(R.id.headblockage);
        tvActive = (TextView) findViewById(R.id.activewitnesses);
        tvNexƒtMaintance = (TextView) findViewById(R.id.nextmaintance);
        tvAtiveCommitee = (TextView) findViewById(R.id.activecommitmember);
        listView = (ListView) findViewById(R.id.listview_wallet);

        ivBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        timer = new Timer();
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
