package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;
import com.spark.bitrade.repository.mapper.ForeignCashLocationMapper;
import com.spark.bitrade.repository.mapper.ForeignMemberBankinfoMapper;
import com.spark.bitrade.repository.service.ForeignMemberBankinfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 换汇银行卡(ForeignMemberBankinfo)表服务实现类
 *
 * @author qiuyuanjie
 * @since 2020-02-04 11:28:10
 */
@Service("foreignMemberBankinfoService")
public class ForeignMemberBankinfoServiceImpl extends ServiceImpl<ForeignMemberBankinfoMapper,ForeignMemberBankinfo> implements ForeignMemberBankinfoService {


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ForeignMemberBankinfo queryById(Long id) {
        return getBaseMapper().queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ForeignMemberBankinfo> queryAllByLimit(int offset, int limit) {
        return getBaseMapper().queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 实例对象
     */
    @Override
    public ForeignMemberBankinfo insert(ForeignMemberBankinfo foreignMemberBankinfo) {
        int insert = getBaseMapper().insert(foreignMemberBankinfo);

        return foreignMemberBankinfo;
    }

    /**
     * 修改数据
     *
     * @param foreignMemberBankinfo 实例对象
     * @return 实例对象
     */
    @Override
    public ForeignMemberBankinfo update(ForeignMemberBankinfo foreignMemberBankinfo) {
        getBaseMapper().update(foreignMemberBankinfo);
        return this.queryById(foreignMemberBankinfo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return getBaseMapper().deleteById(id) > 0;
    }

    @Override
    public IPage<ForeignMemberBankinfo> queryAll(ForeignMemberBankinfo foreignMemberBankinfo, QueryVo queryVo) {
        QueryWrapper<ForeignMemberBankinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",foreignMemberBankinfo.getMemberId());
        IPage<ForeignMemberBankinfo> page = queryVo.toPage();
        IPage<ForeignMemberBankinfo> result = this.page(page,wrapper);
        return result;
    }
}