package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.PictureAccessory;
import com.spark.bitrade.repository.mapper.PictureAccessoryMapper;
import com.spark.bitrade.repository.service.PictureAccessoryService;
import org.springframework.stereotype.Service;

/**
 * 图片附件表(PictureAccessory)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-01 15:36:38
 */

@Service("pictureAccessoryService")
public class PictureAccessoryServiceImpl extends ServiceImpl<PictureAccessoryMapper, PictureAccessory> implements PictureAccessoryService {

}