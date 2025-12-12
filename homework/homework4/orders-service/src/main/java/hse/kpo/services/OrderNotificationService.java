package hse.kpo.services;

import hse.kpo.dto.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOrderStatusChange(OrderResponse order) {
        messagingTemplate.convertAndSend(
                "/topic/orders/" + order.userId(),
                order
        );
    }
}
