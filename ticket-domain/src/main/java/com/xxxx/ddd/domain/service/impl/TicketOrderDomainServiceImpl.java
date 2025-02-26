package com.xxxx.ddd.domain.service.impl;

import com.xxxx.ddd.domain.respository.TicketOrderRepository;
import com.xxxx.ddd.domain.service.TicketOrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketOrderDomainServiceImpl implements TicketOrderDomainService {
    private final TicketOrderRepository ticketOrderRepository;

    @Override
    public boolean decreaseStockLevel1(Long ticketId, int quantity) {
        return ticketOrderRepository.decreaseStockLevel1(ticketId, quantity);
    }

    @Override
    public boolean decreaseStockLevel2(Long ticketId, int quantity) {
        return ticketOrderRepository.decreaseStockLevel2(ticketId, quantity);
    }

    @Override
    public boolean decreaseStockLevel3CAS(Long ticketId, int oldStockAvailable, int quantity) {
        return ticketOrderRepository.decreaseStockLevel3CAS(ticketId, oldStockAvailable, quantity);
    }

    @Override
    public int getStockAvailable(Long ticketId) {
        return ticketOrderRepository.getStockAvailable(ticketId);
    }
}
