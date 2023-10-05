package com.kyr.mytrain.member.req;

import com.kyr.mytrain.common.dto.PageDto;

public class TicketQueryReq extends PageDto {

    private Long memberId;

    @Override
    public String toString() {
        return "TicketQueryReq{" +
                "memberId='" + memberId + '\'' +
                '}';
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
