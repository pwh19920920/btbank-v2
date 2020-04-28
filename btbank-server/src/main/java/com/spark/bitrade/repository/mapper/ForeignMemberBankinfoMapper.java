package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 换汇银行卡(ForeignMemberBankinfo)表数据库访问层
 *
 * @author qiuyuanjie
 * @since 2020-02-04 11:30:48
 */
@Mapper
public interface ForeignMemberBankinfoMapper extends BaseMapper<ForeignMemberBankinfo> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ForeignMemberBankinfo queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ForeignMemberBankinfo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 对象列表
     */
    List<ForeignMemberBankinfo> queryAll(ForeignMemberBankinfo foreignMemberBankinfo);

    /**
     * 新增数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 影响行数
     */
    int insert(ForeignMemberBankinfo foreignMemberBankinfo);

    /**
     * 修改数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 影响行数
     */
    int update(ForeignMemberBankinfo foreignMemberBankinfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}