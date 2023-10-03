package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.ConfirmOrder;
import com.kyr.mytrain.business.domain.ConfirmOrderExample;
import com.kyr.mytrain.business.domain.DailyTrainTicket;
import com.kyr.mytrain.business.enums.BusinessExceptionEnum;
import com.kyr.mytrain.business.enums.ConfirmOrderStatusEnum;
import com.kyr.mytrain.business.enums.SeatColEnum;
import com.kyr.mytrain.business.enums.SeatTypeEnum;
import com.kyr.mytrain.business.mapper.ConfirmOrderMapper;
import com.kyr.mytrain.business.req.ConfirmOrderDoReq;
import com.kyr.mytrain.business.req.ConfirmOrderQueryReq;
import com.kyr.mytrain.business.req.ConfirmOrderTicketReq;
import com.kyr.mytrain.business.resp.ConfirmOrderQueryResp;
import com.kyr.mytrain.common.context.LoginContext;
import com.kyr.mytrain.common.exception.BusinessException;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    public void save(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowIdLong());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setMemberId(LoginContext.getId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setTickets(JSON.toJSONString(req.getTickets()));
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }


    public void doConfirm(ConfirmOrderDoReq req) {
        // 参数校验

        // 保存确认订单表，状态初始
        save(req);

        // 查出真实的余票记录
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(req.getTrainCode(), req.getDate(), req.getStart(), req.getEnd());
        LOG.info("【余票记录-扣减前】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());

        // 扣减余票，确认余票是否足够
        reduceTicket(req, dailyTrainTicket);
        LOG.info("【余票记录-扣减后】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());

        // 计算偏移量
        ConfirmOrderTicketReq ticket0 = req.getTickets().get(0);
        if (StrUtil.isBlank(ticket0.getSeat())) {
            LOG.info("本次购票不选座");
        } else {
            LOG.info("本次购票需要选座");
            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(ticket0.getSeatTypeCode());
            LOG.info("座位类型：{}，可选择的座位：{}", SeatTypeEnum.getEnumByCode(ticket0.getSeatTypeCode()).getDesc(), colsByType);

            ArrayList<Integer> absoluteOffsetList = new ArrayList<>(5);
            for (ConfirmOrderTicketReq ticket : req.getTickets()) {
                for (int i = 0; i < colsByType.size(); i++) {
                    if (ticket.getSeat().contains(colsByType.get(i).getCode())) {
                        if (ticket.getSeat().contains("1")) {
                            absoluteOffsetList.add(i);
                        } else {
                            absoluteOffsetList.add(i + colsByType.size());
                        }
                        break;
                    }
                }
            }
            LOG.info("绝对偏移量：{}", absoluteOffsetList);

            ArrayList<Integer> offsetList = new ArrayList<>(absoluteOffsetList.size());
            for (Integer absoluteOffset : absoluteOffsetList) {
                int offset = absoluteOffset - absoluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("相对偏移量：{}", offsetList);
        }

        // 选座
            // 一个车厢一个车厢获取数据
            // 挑选符合条件的座位

        // 选座后的事务处理
            // 选座表修改售卖情况Sell
            // 余票详情表修改余票
            // 为会员增加购票记录
            // 更新订单状态为成功

    }

    private void reduceTicket(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            SeatTypeEnum enumByCode = SeatTypeEnum.getEnumByCode(ticketReq.getSeatTypeCode());
            switch (enumByCode) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
                default -> throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SEAT_TYPE_ERROR);

            }
        }
    }
}
