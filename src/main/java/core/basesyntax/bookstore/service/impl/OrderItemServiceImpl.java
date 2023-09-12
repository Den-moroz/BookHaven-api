package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import core.basesyntax.bookstore.mapper.OrderItemMapper;
import core.basesyntax.bookstore.repository.orderitem.OrderItemRepository;
import core.basesyntax.bookstore.service.OrderItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public List<OrderItemDto> getAllByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId)
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderItemDto getByItemIdAndOrderId(Long itemId, Long orderId) {
        return orderItemMapper.toDto(orderItemRepository.findAllByIdAndOrderId(itemId, orderId));
    }
}
