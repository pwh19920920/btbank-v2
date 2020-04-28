package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ImGroup;
import com.spark.bitrade.repository.entity.ImMember;

import java.util.List;

/**
 * (ImGroup)表服务接口
 *
 * @author yangch
 * @since 2020-01-20 10:58:22
 */
public interface ImGroupService extends IService<ImGroup> {

    public List<ImGroup> getAvailableGroup();

}