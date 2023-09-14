package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.TrainStation;
import com.kyr.mytrain.business.domain.TrainStationExample;
import com.kyr.mytrain.business.enums.BusinessExceptionEnum;
import com.kyr.mytrain.business.mapper.TrainStationMapper;
import com.kyr.mytrain.business.req.TrainStationQueryReq;
import com.kyr.mytrain.business.req.TrainStationSaveReq;
import com.kyr.mytrain.business.resp.TrainStationQueryResp;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Resource
    private TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            // 新增火车车站时校验车次-站序唯一键是否已存在
            TrainStation trainStation1 = selectByUnique(req.getTrainCode(), req.getIndex());
            if (ObjectUtil.isNotNull(trainStation1)) {
                throw new BusinessException(BusinessExceptionEnum.TRAIN_STATION_TRAINCODE_INDEX_UNIQUE);
            }

            // 新增火车车站时校验车次-站名序唯一键是否已存在
            TrainStation trainStation2 = selectByUnique(req.getTrainCode(), req.getName());
            if (ObjectUtil.isNotNull(trainStation2)) {
                throw new BusinessException(BusinessExceptionEnum.TRAIN_STATION_TRAINCODE_NAME_UNIQUE);
            }

            trainStation.setId(SnowUtil.getSnowIdLong());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("train_code asc, `index` asc");
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);

        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);

        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }

    public TrainStation selectByUnique(String trainCode, Integer index) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        criteria.andIndexEqualTo(index);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(trainStations)) {
            return trainStations.get(0);
        } else {
            return null;
        }
    }

    public TrainStation selectByUnique(String trainCode, String name) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        criteria.andNameEqualTo(name);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(trainStations)) {
            return trainStations.get(0);
        } else {
            return null;
        }
    }
}
