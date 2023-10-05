package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.DailyTrainCarriage;
import com.kyr.mytrain.business.domain.DailyTrainCarriageExample;
import com.kyr.mytrain.business.domain.TrainCarriage;
import com.kyr.mytrain.business.enums.BusinessExceptionEnum;
import com.kyr.mytrain.business.enums.SeatColEnum;
import com.kyr.mytrain.business.mapper.DailyTrainCarriageMapper;
import com.kyr.mytrain.business.req.DailyTrainCarriageQueryReq;
import com.kyr.mytrain.business.req.DailyTrainCarriageSaveReq;
import com.kyr.mytrain.business.resp.DailyTrainCarriageQueryResp;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Resource
    private TrainCarriageService trainCarriageService;

    public void save(DailyTrainCarriageSaveReq req) {
        // 验证列数和总座位数是否正确
        List<SeatColEnum> colsByType = SeatColEnum.getColsByType(req.getSeatType());
        int seatCount = colsByType.size() * req.getRowCount();
        int colNum = colsByType.size();

        if (req.getSeatCount() != seatCount || req.getColCount() != colNum) {
            throw new BusinessException(BusinessExceptionEnum.SEAT_COUNT_OR_COL_ERROR);
        }

        DateTime now = DateTime.now();
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            // 校验车次-车厢号唯一键是否已存在
            DailyTrainCarriageService dailyTrainCarriageService = new DailyTrainCarriageService();
            DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
            DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
            criteria.andTrainCodeEqualTo(req.getTrainCode());
            criteria.andIndexEqualTo(req.getIndex());
            List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
            if (dailyTrainCarriages.size() > 0) {
                throw new BusinessException(BusinessExceptionEnum.CARRIAGE_INDEX_EXIST);
            }


            dailyTrainCarriage.setId(SnowUtil.getSnowIdLong());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
        }
    }

    /**
     * 查找某天某趟车某个座位类型的所有车厢
     * @param date
     * @param trainCode
     * @param seatType
     * @return
     */
    public List<DailyTrainCarriage> selectBySeatType(Date date, String trainCode, String seatType) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("`index` asc");
        dailyTrainCarriageExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        return dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc");
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);

        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);

        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }

    public void genDailyTrainCarriage(Date date, String trainCode) {
        LOG.info("开始生成日期为【{}】，编号为【{}】的火车车厢数据", DateUtil.formatDate(date), trainCode);
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainCarriages)) {
            LOG.warn("车次编号为【】的列车不存在车厢数据");
            return;
        }

        for (TrainCarriage trainCarriage :
                trainCarriages) {
            genDailyTrainCarriage(date, trainCarriage);
        }

        LOG.info("生成日期为【{}】，编号为【{}】的火车车厢数据结束", DateUtil.formatDate(date), trainCode);

    }

    public void genDailyTrainCarriage(Date date, TrainCarriage trainCarriage) {
        LOG.info("生成日期为【{}】，编号为【{}】的火车,第【{}】个车厢数据", DateUtil.formatDate(date), trainCarriage.getTrainCode(), trainCarriage.getIndex());
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.createCriteria()
                .andTrainCodeEqualTo(trainCarriage.getTrainCode())
                .andIndexEqualTo(trainCarriage.getIndex())
                .andDateEqualTo(date);
        dailyTrainCarriageMapper.deleteByExample(dailyTrainCarriageExample);

        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
        dailyTrainCarriage.setId(SnowUtil.getSnowIdLong());
        dailyTrainCarriage.setDate(date);
        Date now = new Date();
        dailyTrainCarriage.setCreateTime(now);
        dailyTrainCarriage.setUpdateTime(now);
        dailyTrainCarriageMapper.insert(dailyTrainCarriage);
    }
}
