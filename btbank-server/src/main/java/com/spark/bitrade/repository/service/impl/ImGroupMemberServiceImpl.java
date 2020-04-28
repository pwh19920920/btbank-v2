package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ImGroupMemberMapper;
import com.spark.bitrade.repository.entity.ImGroupMember;
import com.spark.bitrade.repository.service.ImGroupMemberService;
import org.springframework.stereotype.Service;

/**
 * (ImGroupMember)表服务实现类
 *
 * @author yangch
 * @since 2020-01-20 10:59:34
 */
@Service("imGroupMemberService")
public class ImGroupMemberServiceImpl extends ServiceImpl<ImGroupMemberMapper, ImGroupMember> implements ImGroupMemberService {

}