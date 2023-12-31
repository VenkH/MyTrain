package com.kyr.mytrain.common.context;

import com.kyr.mytrain.common.resp.MemberLoginResp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginContext {

    private static ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    public static MemberLoginResp getMember(){
        return member.get();
    }

    public static void setMember(MemberLoginResp member) {
        LoginContext.member.set(member);
    }

    public static Long getId() {
        return member.get().getId();
    }
}
