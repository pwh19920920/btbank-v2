package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;

/**
 * 企业矿工申请表(EnterpriseMinerApplication)表服务接口
 *
 * @author biu
 * @since 2019-12-23 17:14:51
 */
public interface EnterpriseMinerApplicationService extends IService<EnterpriseMinerApplication> {

    boolean hasApplication(Long memberId);

    EnterpriseMinerApplication latestJoinApplication(Long memberId);

    EnterpriseMinerApplication latestApplication(Long memberId);
}