package com.spark.bitrade.biz;

import com.spark.bitrade.entity.Member;

import java.util.Date;

public interface MemberExperienceBizService {

    void lockExperienceAmount(Member member, Date limitTime);

    void lockExperience();

    void oldMemberRelease();
}
