package com.kyr.mytrain.business.mapper;

import com.kyr.mytrain.business.domain.Station;
import com.kyr.mytrain.business.domain.StationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StationMapper {
    long countByExample(StationExample example);

    int deleteByExample(StationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Station record);

    int insertSelective(Station record);

    List<Station> selectByExample(StationExample example);

    Station selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Station record, @Param("example") StationExample example);

    int updateByExample(@Param("record") Station record, @Param("example") StationExample example);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);
}