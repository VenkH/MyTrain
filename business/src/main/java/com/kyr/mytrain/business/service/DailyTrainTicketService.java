package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.DailyTrainTicket;
import com.kyr.mytrain.business.domain.DailyTrainTicketExample;
import com.kyr.mytrain.business.domain.TrainStation;
import com.kyr.mytrain.business.enums.SeatTypeEnum;
import com.kyr.mytrain.business.enums.TrainTypeEnum;
import com.kyr.mytrain.business.mapper.DailyTrainTicketMapper;
import com.kyr.mytrain.business.req.DailyTrainTicketQueryReq;
import com.kyr.mytrain.business.req.DailyTrainTicketSaveReq;
import com.kyr.mytrain.business.resp.DailyTrainTicketQueryResp;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    private TrainStationService trainStationService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private TrainService trainService;

    public void save(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowIdLong());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }

    /**
     * 存在缓存击穿的问题：当热点的key过期时，大量的请求同时访问数据库
     * 1. 上分布式锁
     * 2. 使用定时任务定时更新缓存数据
     *
     * 同样也存在缓存雪崩：当大量热点的key同时过期，会有更多的请求去访问数据库
     * @param req
     * @return
     */
    @Cacheable(value = "queryList")
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.setOrderByClause("date desc, train_code asc");
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if (ObjectUtil.isNotEmpty(req.getStart())) {
            criteria.andStartEqualTo(req.getStart());
        }
        if (ObjectUtil.isNotEmpty(req.getEnd())) {
            criteria.andEndEqualTo(req.getEnd());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);

        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);

        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDailyTrainTicket(Date date, String trainCode) {
        LOG.info("开始生成日期为【{}】，编号为【{}】的火车余票数据", DateUtil.formatDate(date), trainCode);

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainStations)) {
            LOG.warn("车次编号为【{}】的火车不存在车站数据！结束生成火车余票数据！", trainCode);
            return;
        }

        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

        int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ);
        int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ);
        int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW);
        int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW);

        String trainType = trainService.selectTrainTypeByTrainCode(trainCode);
        BigDecimal priceRate = TrainTypeEnum.getPriceRateByTrainType(trainType);
        if (null == priceRate) {
            LOG.error("根据【{}】号列车的列车类型【{}】获取票价比例时错误！", trainCode, trainType);
            throw new NullPointerException();
        }

        for (int i = 0; i < trainStations.size(); i++) {
            // 始发站
            TrainStation trainStationStart = trainStations.get(i);

            BigDecimal sumKM = new BigDecimal(BigInteger.ZERO);
            for (int j = i+1; j < trainStations.size(); j++) {

                // 终点站
                TrainStation trainStationEnd = trainStations.get(j);

                sumKM = sumKM.add(trainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowIdLong());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);

                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());

                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());

                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);

                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);

                Date now = new Date();
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
                //LOG.info("生成【{}】【{}】号列车【{}】到【{}】的余票数据",DateUtil.formatDate(date), trainCode, trainStationStart.getName(), trainStationEnd.getName());
            }
        }

        LOG.info("生成日期为【{}】，编号为【{}】的火车余票数据结束", DateUtil.formatDate(date), trainCode);

    }

    public DailyTrainTicket selectByUnique(String trainCode, Date date, String start, String end) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        criteria.andDateEqualTo(date);
        criteria.andStartEqualTo(start);
        criteria.andEndEqualTo(end);
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isNotEmpty(dailyTrainTickets)) {
            return dailyTrainTickets.get(0);
        } else {
            return null;
        }
    }
}
