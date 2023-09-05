package com.kyr.mytrain.member.dto;

import com.kyr.mytrain.common.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerQueryDto extends PageDto {

    private Long memberId;
}