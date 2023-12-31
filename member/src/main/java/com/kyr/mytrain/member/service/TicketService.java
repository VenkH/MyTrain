package com.kyr.mytrain.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kyr.mytrain.common.req.MemberTicketSaveReq;
import com.kyr.mytrain.common.resp.PageResp;
import com.kyr.mytrain.common.util.SnowUtil;
import com.kyr.mytrain.member.domain.Ticket;
import com.kyr.mytrain.member.domain.TicketExample;
import com.kyr.mytrain.member.mapper.TicketMapper;
import com.kyr.mytrain.member.req.TicketQueryReq;
import com.kyr.mytrain.member.req.remote.ConfirmOrderDoReq;
import com.kyr.mytrain.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    private TicketMapper ticketMapper;

    public void save(MemberTicketSaveReq req) throws Exception {
//        LOG.info("seata全局事务id:{}", RootContext.getXID());
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setId(SnowUtil.getSnowIdLong());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
//        if (1==1) {
//            throw new Exception("模拟异常");
//        }
    }

    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("id desc");
        TicketExample.Criteria criteria = ticketExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);

        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }

    public List<Ticket> queryBoughtTicket(ConfirmOrderDoReq req) {
        TicketExample ticketExample = new TicketExample();
        TicketExample.Criteria criteria = ticketExample.createCriteria();
        criteria.andTrainDateEqualTo(req.getDate())
                .andTrainCodeEqualTo(req.getTrainCode());
        ArrayList<Long> passengerIdList = new ArrayList<>();
        req.getTickets().forEach((ticket)-> passengerIdList.add(ticket.getPassengerId()));
        criteria.andPassengerIdIn(passengerIdList);

        return ticketMapper.selectByExample(ticketExample);
    }
}
