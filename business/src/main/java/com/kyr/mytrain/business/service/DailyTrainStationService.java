package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.DailyTrainStation;
import com.kyr.mytrain.business.domain.DailyTrainStationExample;
import com.kyr.mytrain.business.domain.TrainStation;
import com.kyr.mytrain.business.mapper.DailyTrainStationMapper;
import com.kyr.mytrain.business.req.DailyTrainStationQueryReq;
import com.kyr.mytrain.business.req.DailyTrainStationSaveReq;
import com.kyr.mytrain.business.resp.DailyTrainStationQueryResp;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Resource
    private DailyTrainStationMapper dailyTrainStationMapper;

    @Resource
    private TrainStationService trainStationService;

    public void save(DailyTrainStationSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowIdLong());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("date desc, train_code asc");
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);

        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainStationQueryResp> list = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryResp.class);

        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }

    /**
     * 生成某日的火车车站数据
     * @param date
     */
    public void genDailyTrainStation(Date date, String trainCode) {
        LOG.info("开始生成日期为【{}】，编号为【{}】的火车车站数据", DateUtil.formatDate(date), trainCode);
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainStations)) {
            LOG.warn("车次编号为【{}】的火车不存在车站数据！", trainCode);
        }

        for (TrainStation trainStation :
                trainStations) {
            genDailyTrainStation(date, trainStation);
        }
        LOG.info("生成日期为【{}】，编号为【{}】的火车车站数据结束", DateUtil.formatDate(date), trainCode);
    }

    public void genDailyTrainStation(Date date, TrainStation trainStation) {
        LOG.info("生成日期为【{}】，列车编号为【{}】的【{}】车站数据", DateUtil.formatDate(date), trainStation.getTrainCode(), trainStation.getName());
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainStation.getTrainCode())
                .andNameEqualTo(trainStation.getName());
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);

        Date now = new Date();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
        dailyTrainStation.setId(SnowUtil.getSnowIdLong());
        dailyTrainStation.setDate(date);
        dailyTrainStation.setCreateTime(now);
        dailyTrainStation.setUpdateTime(now);
        dailyTrainStationMapper.insert(dailyTrainStation);
    }
}
