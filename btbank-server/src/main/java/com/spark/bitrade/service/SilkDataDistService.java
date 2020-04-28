package com.spark.bitrade.service;

import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class SilkDataDistService {
    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;
    public SilkDataDist findOne(String id,String key){
        MessageRespResult<SilkDataDist> result =  silkDataDistApiService.findOne(id,key);
        if(result.isSuccess()){
            return result.getData();
        }
        return null;
    }
}
