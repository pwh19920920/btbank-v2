package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.Advertise;
import com.spark.bitrade.repository.mapper.AdvertiseMapper;
import com.spark.bitrade.repository.service.AdvertisesService;
import org.springframework.stereotype.Service;

@Service
public class AdvertisesServiceImpl extends ServiceImpl<AdvertiseMapper,Advertise> implements AdvertisesService {
}
