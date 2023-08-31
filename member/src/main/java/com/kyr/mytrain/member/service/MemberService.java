package com.kyr.mytrain.member.service;

import cn.hutool.core.collection.CollUtil;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.member.domain.Member;
import com.kyr.mytrain.member.domain.MemberExample;
import com.kyr.mytrain.member.dto.MemberRegisterDto;
import com.kyr.mytrain.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    /**
     * 使用手机号码注册用户
     * @param memberRegisterDto
     * @return
     */
    public CommonResp<Long> register(MemberRegisterDto memberRegisterDto) {
        String mobile = memberRegisterDto.getMobile();
        // 校验手机号是否已注册
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(members)) {
            throw new RuntimeException("手机号已被注册！");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return new CommonResp<>(member.getId());
    }


}
