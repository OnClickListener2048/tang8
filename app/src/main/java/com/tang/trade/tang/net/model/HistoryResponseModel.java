package com.tang.trade.tang.net.model;

import java.util.List;

/**
 * Created by dagou on 2017/9/22.
 */

public class HistoryResponseModel {

    /**
     * status : success
     * msg :
     * data : [{"amount":"10","symbol":"BTS","from":"lee","to":"init0"},{"amount":"10","symbol":"BTS","from":"lee","to":"init0"},{"amount":"500","symbol":"BTS","from":"init0","to":"lee"}]
     *
     * {"status":"success","msg":"","data":[{"amount":"0.56000","symbol":"BTS","from":"lee","to":"init0"},{"amount":"0.00010","symbol":"BTS","from":"lee","to":"init0"},{"amount":"10","symbol":"BTS","from":"lee","to":"init0"},{"amount":"10","symbol":"BTS","from":"lee","to":"init0"},{"amount":"500","symbol":"BTS","from":"init0","to":"lee"}]}
     */

    private String status;
    private String msg;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * amount : 10
         * symbol : BTS
         * from : lee
         * to : init0
         */

        private String amount;
        private String symbol;
        private String from;
        private String to;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
