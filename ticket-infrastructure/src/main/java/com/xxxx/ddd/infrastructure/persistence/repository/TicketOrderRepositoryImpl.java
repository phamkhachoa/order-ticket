package com.xxxx.ddd.infrastructure.persistence.repository;

import com.xxxx.ddd.domain.respository.TicketOrderRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.TicketOrderJPAMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketOrderRepositoryImpl implements TicketOrderRepository {

    private final TicketOrderJPAMapper ticketOrderJPAMapper;

    @Override
    public boolean decreaseStockLevel1(Long ticketId, int quantity) {
        log.info("Run test: decreaseStockLevel1 with: | {}, {}", ticketId, quantity);
        return ticketOrderJPAMapper.decreaseStockLevel1(ticketId, quantity) > 0;
    }

    @Override
    public boolean decreaseStockLevel2(Long ticketId, int quantity) {
        return false;
    }

    @Override
    public boolean decreaseStockLevel3CAS(Long ticketId, int oldStockAvailable, int quantity) {
        log.info("Run test: decreaseStockLevel3CAS with: | {}, {}", ticketId, quantity);
        return ticketOrderJPAMapper.decreaseStockLevel3CAS(ticketId, oldStockAvailable, quantity) > 0;
    }

    @Override
    public int getStockAvailable(Long ticketId) {
        return ticketOrderJPAMapper.getStockAvailable(ticketId);
    }
}
