package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.vo.ProfitVo;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * (MemberTransaction)控制层
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
@RestController
@RequestMapping("api/v2/memberTransaction")
@Api(description = "控制层")
public class MemberTransactionController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private IMemberTransactionService memberTransactionService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param memberTransaction 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "memberTransaction", dataTypeClass =MemberTransaction.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<MemberTransaction>> list(Integer size, Integer current, MemberTransaction memberTransaction) {
        return success(this.memberTransactionService.page(new Page<>(current, size), new QueryWrapper<>(memberTransaction)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParam(value = "主键", name = "id", dataTypeClass = Serializable.class, required = true)
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<MemberTransaction> get(@RequestParam("id") Serializable id) {
        return success(this.memberTransactionService.getById(id));
    }

    /**
     * 昨日收益与累计收益
     *
     * @param memberId 会员id
     * @return 单条数据
     */
    @ApiOperation(value = "昨日收益与累计收益", notes = "昨日收益与累计收益接口")
    @RequestMapping(value = "/ProfitCount", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ProfitVo> ProfitCount(@RequestParam("memberId") Long memberId) {
        return success(this.memberTransactionService.profitCount(memberId));
    }

    @ApiOperation(value = "累计收益详情列表", notes = "累计收益详情列表接口")
    @RequestMapping(value = "/ProfitList", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<MemberTransaction>> ProfitList(@RequestParam("memberId") Long memberId,
                                                            @RequestParam("page") int page,
                                                           @RequestParam("size") int size) {
        return success(this.memberTransactionService.profitList(memberId,page,size));
    }
//
//    /**
//     * 新增数据
//     *
//     * @param memberTransaction 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "memberTransaction", dataTypeClass =MemberTransaction.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(MemberTransaction memberTransaction) {
//        return success(this.memberTransactionService.save(memberTransaction));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param memberTransaction 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "memberTransaction", dataTypeClass =MemberTransaction.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(MemberTransaction memberTransaction) {
//        return success(this.memberTransactionService.updateById(memberTransaction));
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param idList 主键集合
//     * @return 删除结果
//     */
//    @DeleteMapping
//    @ApiOperation(value = "删除数据接口", notes = "删除数据接口")
//    @ApiImplicitParam(value = "主键集合", name = "idList", dataTypeClass = List.class, required = true)
//    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult delete(@RequestParam("idList") List<Serializable> idList) {
//        return success(this.memberTransactionService.removeByIds(idList));
//    }
}