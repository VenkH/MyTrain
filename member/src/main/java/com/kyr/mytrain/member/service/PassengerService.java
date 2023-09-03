package com.kyr.mytrain.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.kyr.mytrain.common.util.SnowUtil;
import com.kyr.mytrain.member.domain.Passenger;
import com.kyr.mytrain.member.dto.PassengerDto;
import com.kyr.mytrain.member.mapper.PassengerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    public void savePassenger(PassengerDto passengerDto) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(passengerDto, Passenger.class);
        passenger.setId(SnowUtil.getSnowIdLong());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }
}
