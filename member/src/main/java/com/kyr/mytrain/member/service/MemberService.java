package com.kyr.mytrain.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kyr.mytrain.common.constant.BusinessExceptionEnum;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.CommonResp;
import com.kyr.mytrain.common.util.JwtUtil;
import com.kyr.mytrain.common.util.SnowUtil;
import com.kyr.mytrain.member.domain.Member;
import com.kyr.mytrain.member.domain.MemberExample;
import com.kyr.mytrain.member.dto.MemberLoginDto;
import com.kyr.mytrain.member.dto.SendCodeDto;
import com.kyr.mytrain.member.mapper.MemberMapper;
import com.kyr.mytrain.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    /**
     * 发送验证码
     * @param sendCodeDto
     * @return
     */
    public CommonResp<Object> sendCode(SendCodeDto sendCodeDto) {
        String mobile = sendCodeDto.getMobile();
        // 校验手机号是否已注册
        Member memberDB = selectMemberByMobile(mobile);

        // 如果手机号码不存在则插入一条记录
        if (ObjectUtil.isEmpty(memberDB)) {
            Member member = new Member();
            member.setId(SnowUtil.getSnowIdLong());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }

        // 保存到短信记录表，包括：手机号，短信验证码、有效期、是否已使用、业务类型、发送时间、使用时间等

        //对接短信通道，发短信

        return new CommonResp();
    }

    /**
     * 使用短信验证码登录
     * @param memberLoginDto
     * @return
     */
    public CommonResp<MemberLoginResp> login(MemberLoginDto memberLoginDto) {
        String mobile = memberLoginDto.getMobile();
        // 校验手机号是否已注册
        Member member = selectMemberByMobile(mobile);

        // 如果手机号码不存在则直接返回
        if (ObjectUtil.isEmpty(member)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        // 验证码写死为8888
        if ( !"8888".equals(memberLoginDto.getCode())) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_CODE_ERROR);
        }

        String token = JwtUtil.createToken(member);
        MemberLoginResp memberLoginResp = new MemberLoginResp(member, token);

        return new CommonResp<>(memberLoginResp);
    }

    private Member selectMemberByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);

        if (CollUtil.isEmpty(members)) {
            return null;
        } else {
            return members.get(0);
        }
    }


}
