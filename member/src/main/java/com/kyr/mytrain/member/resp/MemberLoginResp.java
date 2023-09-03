package com.kyr.mytrain.member.resp;

import com.kyr.mytrain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginResp {

    Long id;

    String mobile;

    String token;

    public MemberLoginResp(Member member, String token) {
        this.mobile = member.getMobile();
        this.id = member.getId();
        this.token = token;
    }
}
