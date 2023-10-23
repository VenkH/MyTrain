package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.SkToken;
import com.kyr.mytrain.business.domain.SkTokenExample;
import com.kyr.mytrain.business.mapper.SkTokenMapper;
import com.kyr.mytrain.business.req.SkTokenQueryReq;
import com.kyr.mytrain.business.req.SkTokenSaveReq;
import com.kyr.mytrain.business.resp.SkTokenQueryResp;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowIdLong());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    public void genDailySkToken(Date date, String trainCode) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowIdLong());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        //skToken.setCount();
        Date now = new Date();
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int countSeat = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("编号为：{}的列车有座位{}个", trainCode, countSeat);

        int countStation = dailyTrainStationService.countStation(date, trainCode);
        LOG.info("编号为：{}的列车有车站{}个", trainCode, countStation);

        int countToken = (int) ((int) countSeat * countStation * 0.75);
        LOG.info(" 编号为:{}的列车有令牌：{}个", trainCode, countToken);
        skToken.setCount(countToken);

        skTokenMapper.insert(skToken);
    }

}
