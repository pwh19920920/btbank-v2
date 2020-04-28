package com.spark.bitrade.repository.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;

import java.util.List;

/**
 * 换汇银行卡(ForeignMemberBankinfo)表服务接口
 *
 * @author qiuyuanjie
 * @since 2020-02-04 11:27:09
 */
public interface ForeignMemberBankinfoService extends IService<ForeignMemberBankinfo> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ForeignMemberBankinfo queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ForeignMemberBankinfo> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 实例对象
     */
    ForeignMemberBankinfo insert(ForeignMemberBankinfo foreignMemberBankinfo);

    /**
     * 修改数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 实例对象
     */
    ForeignMemberBankinfo update(ForeignMemberBankinfo foreignMemberBankinfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 对象列表
     */
    IPage<ForeignMemberBankinfo> queryAll(ForeignMemberBankinfo foreignMemberBankinfo, QueryVo queryVo);

}