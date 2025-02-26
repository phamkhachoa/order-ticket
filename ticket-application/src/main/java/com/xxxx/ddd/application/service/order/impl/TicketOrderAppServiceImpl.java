package com.xxxx.ddd.application.service.order.impl;

import com.xxxx.ddd.application.service.order.TicketOrderAppService;
import com.xxxx.ddd.domain.service.TicketOrderDomainService;
import jakarta.annotation.Resource;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PessimisticLockException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketOrderAppServiceImpl implements TicketOrderAppService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final TicketOrderDomainService ticketOrderDomainService;

    private static final String CHECK_AND_DECREASE_STOCK_LUA = """
        local stockKey = KEYS[1]
        local stock = redis.call('get', stockKey)
        if not stock or tonumber(stock) <= 0 then
            return 0
        end
        redis.call('decr', stockKey)
        return tonumber(stock)
        """;

    @Override
    public boolean decreaseStockLevel1(Long ticketId, int quantity) {
        try {
            int stockAvailable = ticketOrderDomainService.getStockAvailable(ticketId);
            if (stockAvailable < quantity) {
                log.info("Case stockAvailable < quantity | {}, {}", stockAvailable, quantity);
                return false;
            }

            return ticketOrderDomainService.decreaseStockLevel1(ticketId, quantity);
        } catch (PessimisticLockException e) {
            log.warn("Error");
            return false;
        } catch (LockTimeoutException e) {
            log.warn("Error");
            return false;
        } catch (Exception e) {
            log.warn("Error");
            return false;
        }
    }

    @Override
    public boolean decreaseStockLevel1WithCache(Long ticketId, int quantity) {
        try {
            String stockKey = "flash:stock:" + ticketId;

            // Execute Lua script for atomic inventory check and decrease
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(CHECK_AND_DECREASE_STOCK_LUA);
            redisScript.setResultType(Long.class);

            Long result = redisTemplate.execute(redisScript, Arrays.asList(stockKey));

            if (result != null && result == 1) {
                // Add to order queue for async processing
                log.info("Add order to queue ");
                return ticketOrderDomainService.decreaseStockLevel2(ticketId, quantity);
            }

            log.info("Out of stock");
            return false;
        } catch (PessimisticLockException e) {
            log.warn("Error");
            return false;
        } catch (LockTimeoutException e) {
            log.warn("Error");
            return false;
        } catch (Exception e) {
            log.warn("Error");
            return false;
        }
    }

    @Override
    public boolean decreaseStockLevel2(Long ticketId, int quantity) {
        return false;
    }

    @Override
    public boolean decreaseStockLevel3CAS(Long ticketId, int quantity) {
        try {
            String stockKey = "flash:stock:" + ticketId;

            // Execute Lua script for atomic inventory check and decrease
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(CHECK_AND_DECREASE_STOCK_LUA);
            redisScript.setResultType(Long.class);

            Long result = redisTemplate.execute(redisScript, Arrays.asList(stockKey));

            if (result != null && result >= 0) {
                // Add to order queue for async processing
                log.info("Add order to queue ");
                return ticketOrderDomainService.decreaseStockLevel3CAS(ticketId, result.intValue(), quantity);
            }

            log.info("Out of stock");
            return false;
        } catch (PessimisticLockException e) {
            log.warn("Error");
            return false;
        } catch (LockTimeoutException e) {
            log.warn("Error");
            return false;
        } catch (Exception e) {
            log.warn("Error");
            return false;
        }
    }

    @Override
    public boolean decreaseStockLevel3CASWithCache(Long ticketId, int quantity) {
        return false;
    }

    @Override
    public int getStockAvailable(Long ticketId) {
        return 0;
    }
}
