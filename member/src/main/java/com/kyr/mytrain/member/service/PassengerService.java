package com.kyr.mytrain.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.kyr.mytrain.common.context.MemberContext;
import com.kyr.mytrain.common.util.SnowUtil;
import com.kyr.mytrain.member.domain.Passenger;
import com.kyr.mytrain.member.domain.PassengerExample;
import com.kyr.mytrain.member.dto.PassengerQueryDto;
import com.kyr.mytrain.member.dto.PassengerSaveDto;
import com.kyr.mytrain.member.mapper.PassengerMapper;
import com.kyr.mytrain.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    public void savePassenger(PassengerSaveDto passengerDto) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(passengerDto, Passenger.class);
        passenger.setId(SnowUtil.getSnowIdLong());
        passenger.setMemberId(MemberContext.getId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }

    public List<PassengerQueryResp> queryPassenger(PassengerQueryDto passengerQueryDto) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();

        if (ObjectUtil.isNotNull(passengerQueryDto.getMemberId())) {
            criteria.andMemberIdEqualTo(passengerQueryDto.getMemberId());
        }
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(passengers, PassengerQueryResp.class);
    }
}
