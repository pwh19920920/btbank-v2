package com.spark.bitrade.constant;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeignConst {
    public static final String FOREIGNKEY = "lyEsYNoWOyS7trKNnhoXoTOiR8X1gZOB";
    public static final String FOREIGNEXCHANGE="https://api.1forge.com/convert?from=CNH&to=";
    public static final String FOREIGNEXCHANGEPAIRS="https://api.1forge.com/quotes?pairs=";
    public static final String OTHERFOREIGNTRANSFER="https://ali-waihui.showapi.com/waihui-transform?fromCode=CNY&money=";
    public static final String OTHERFOREIGLIST = "https://ali-waihui.showapi.com/waihui-list";
    public static final String APPCODE = "6c313e656c024cc78403c58f7d9ed9d3";
    public static final Map<String,String>  otherCurrency = new HashMap();
    static {
        otherCurrency.put("IDR","IDR");
        otherCurrency.put("THB","THB");
        otherCurrency.put("PHP","PHP");
        otherCurrency.put("MYR","MYR");
        otherCurrency.put("INR","INR");
        otherCurrency.put("AED","AED");
        otherCurrency.put("SAR","SAR");
        otherCurrency.put("BRL","BRL");
        otherCurrency.put("MOP","MOP");
        otherCurrency.put("KRW","KRW");
        otherCurrency.put("TWD","TWD");
    }
    public static String getForeignexchange(String unit, BigDecimal amount){
        StringBuilder sb = new StringBuilder(FOREIGNEXCHANGE);
        sb.append(unit).append("&quantity=").append(amount.toString()).append("&api_key=").append(FOREIGNKEY);
        return sb.toString();
    }
    public static String getForeignexchangePer(String unit){
        StringBuilder sb = new StringBuilder(FOREIGNEXCHANGE);
        sb.append(unit).append("&quantity=1").append("&api_key=").append(FOREIGNKEY);
        return sb.toString();
    }
    public static String getForeignexchange(List<String > listpairs){
        StringBuilder sb = new StringBuilder(FOREIGNEXCHANGEPAIRS);
        sb.append(StringUtils.join(listpairs ,",")).append("&api_key=").append(FOREIGNKEY);
        return sb.toString();
    }

    public static String getOtherForeignexchange(String unit, BigDecimal amount){
        StringBuilder sb = new StringBuilder(OTHERFOREIGNTRANSFER);
        sb.append(amount).append("&toCode=").append(unit);
        return sb.toString();
    }
}
