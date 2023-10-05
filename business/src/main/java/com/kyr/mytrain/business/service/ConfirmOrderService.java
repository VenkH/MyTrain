package com.kyr.mytrain.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.business.domain.*;
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
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

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

        String trainCode = req.getTrainCode();
        Date date = req.getDate();
        String start = req.getStart();
        String end = req.getEnd();

        // 查出真实的余票记录
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(trainCode, date, start, end);
        LOG.info("【余票记录-扣减前】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());

        // 扣减余票，确认余票是否足够
        reduceTicket(req, dailyTrainTicket);
        LOG.info("【余票记录-扣减后】一等座：{}，二等座：{}，软卧：{}，硬卧：{}", dailyTrainTicket.getYdz(), dailyTrainTicket.getEdz(), dailyTrainTicket.getRw(), dailyTrainTicket.getYw());

        // 计算偏移量
        ConfirmOrderTicketReq ticket0 = req.getTickets().get(0);
        if (StrUtil.isBlank(ticket0.getSeat())) {

            LOG.info("本次购票不选座");
            // 选座
            for (ConfirmOrderTicketReq ticket : req.getTickets()) {
                getSeatWithoutSelections(
                        date,
                        trainCode,
                        ticket.getSeatTypeCode(),
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );
            }

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
            // 选座
            getSeatWithSelections(
                    date,
                    trainCode,
                    ticket0.getSeatTypeCode(),
                    offsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex(),
                    ticket0.getSeat()
            );
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

    /**
     * 选择座位，有选座，一次性选定所有座位
     * @param date
     * @param trainCode
     * @param seatType
     * @param offsetList
     * @param startIndex
     * @param endIndex
     * @param firstSelectSeat
     */
    private void getSeatWithSelections(Date date, String trainCode, String seatType, ArrayList<Integer> offsetList, Integer startIndex, Integer endIndex, String firstSelectSeat) {
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("列车{}符合条件(日期：{}，座位类型：{})的车厢有：{}个", trainCode, DateUtil.formatDate(date), SeatTypeEnum.getEnumByCode(seatType).getDesc(),carriageList.size());

        for (DailyTrainCarriage carriage : carriageList) {
            // 一个车厢一个车厢获取数据
            List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatService.selectByCarriage(date, trainCode, carriage.getIndex());
            LOG.info("在列车{}的第{}号车厢中选座", trainCode, carriage.getIndex());

            for (int seatIndex = 0; seatIndex < dailyTrainSeats.size(); seatIndex++) {

                DailyTrainSeat seat = dailyTrainSeats.get(seatIndex);
                // 列号不对，从下一个座位开始重新选
                if (!firstSelectSeat.contains(seat.getCol())) {
                    continue;
                }

                boolean selectFlag = canSell(seat, startIndex, endIndex);
                // 该座位不可选，继续选座
                if (!selectFlag ) {
                    continue;
                }

                boolean isSelectAll = true;
                // 还有座位需要选座
                if (offsetList.size() > 1) {
                    for (int j = 1; j < offsetList.size(); j++) {
                        // 偏移后的索引超过了车厢的座位数，选座失败
                        if (dailyTrainSeats.size() <= seatIndex + offsetList.get(j)) {
                            isSelectAll = false;
                            break;
                        }
                        int nextSeatIndex = seatIndex + offsetList.get(j);
                        DailyTrainSeat nextSeat = dailyTrainSeats.get(nextSeatIndex);
                        isSelectAll = canSell(nextSeat, startIndex, endIndex);
                        // 该座位不可选，继续选座
                        if (!isSelectAll) {
                            break;
                        }
                    }
                }

                if (!isSelectAll) {
                    // 没有把全部座位选中，从下一个座位开始重新选
                    LOG.info("【选座失败】无法选中所有座位，重新开始");
                    continue;
                } else {
                    // 选好了座位
                    LOG.info("【选座成功】");
                    return;
                }
            }
        }

    }

    /**
     * 选择座位，没有选座
     * 因为不选座可以选择不同类型的座位，所以不选座时应一个座位一个座位选
     * @param date
     * @param trainCode
     * @param seatType
     */
    private void getSeatWithoutSelections(Date date, String trainCode, String seatType,  Integer startIndex, Integer endIndex) {
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);

        LOG.info("列车{}符合条件(日期：{}，座位类型：{})的车厢有：{}个", trainCode, DateUtil.formatDate(date), SeatTypeEnum.getEnumByCode(seatType).getDesc(),carriageList.size());

        for (DailyTrainCarriage carriage : carriageList) {
            // 一个车厢一个车厢获取数据
            List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatService.selectByCarriage(date, trainCode, carriage.getIndex());
            LOG.info("在列车{}的第{}号车厢中选座", trainCode, carriage.getIndex());

            for (int i = 0; i < dailyTrainSeats.size(); i++) {
                boolean sellFlag = canSell(dailyTrainSeats.get(i), startIndex, endIndex);
                if (sellFlag) {
                    return;
                }
            }

        }
    }

    /**
     * 判断座位是否可选，选中后修改售卖情况
     * @param seat
     * @param startIndex
     * @param endIndex
     * @return
     */
    private boolean canSell(DailyTrainSeat seat, Integer startIndex, Integer endIndex) {
        String sell = seat.getSell();
        String sellPart = sell.substring(startIndex - 1, endIndex - 1);
        if (Integer.parseInt(sellPart) > 0) {
            // 已卖出
            return false;
        } else {
            // 未卖出，修改sell，以便存入数据库
            sellPart = sellPart.replace('0', '1');
            sellPart = StrUtil.fillBefore(sellPart, '0', endIndex - 1);
            sellPart = StrUtil.fillAfter(sellPart, '0', sell.length());

            // 与原先的售卖情况作或运算，获得最新的售卖情况
            int newSellInt = NumberUtil.binaryToInt(sellPart) | NumberUtil.binaryToInt(sell);
            // 将十进制再转换成二进制，并且在前面补0，以免从十进制转换成二进制时0被省略
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());

            seat.setSell(newSell);
            LOG.info("选中{}号车厢的第{}行第{}列座位，该座位的售卖情况为：{}，修改为：{}", seat.getCarriageIndex(), seat.getRow(), seat.getCol(), sell, newSell);
            return true;
        }
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
