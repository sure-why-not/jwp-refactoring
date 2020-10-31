package kitchenpos.ui;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.application.OrderService;
import kitchenpos.dao.MenuGroupRepository;
import kitchenpos.dao.MenuRepository;
import kitchenpos.dao.OrderTableRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.OrderChangeStatusRequest;
import kitchenpos.dto.OrderCreateRequest;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderResponse;

class OrderRestControllerTest extends ControllerTest {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderService orderService;

    @DisplayName("create: 주문 등록 테스트")
    @Test
    void createTest() throws Exception {
        final OrderTable orderTable = orderTableRepository.save(new OrderTable(5, false));
        final MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup("세트 메뉴"));
        final Menu menu = menuRepository.save(
                new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), menuGroup, new ArrayList<>()));
        final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 2);
        final OrderCreateRequest order = new OrderCreateRequest(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));

        create("/api/orders", order)
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.orderTableId").isNotEmpty());
    }

    @DisplayName("전체 주문을 확인하는 테스트")
    @Test
    void listTest() throws Exception {
        final OrderTable orderTable = orderTableRepository.save(new OrderTable(0, false));
        final MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup("세트 메뉴"));
        final Menu menu = menuRepository.save(
                new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), menuGroup, new ArrayList<>()));
        final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 2);
        final OrderCreateRequest order = new OrderCreateRequest(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));

        orderService.create(order);

        findList("/api/orders")
                .andExpect(jsonPath("$.[0].id").exists());
    }

    @DisplayName("changeOrderStatus: 주문 상태를 변경하는 테스트")
    @Test
    void changeOrderStatusTest() throws Exception {
        final OrderTable orderTable = orderTableRepository.save(new OrderTable(0, false));
        final MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup("세트 메뉴"));
        final Menu menu = menuRepository.save(
                new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), menuGroup, new ArrayList<>()));
        final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 2);
        final OrderCreateRequest order = new OrderCreateRequest(orderTable.getId(),
                Collections.singletonList(orderLineItemRequest));
        final OrderResponse saved = orderService.create(order);
        final OrderChangeStatusRequest orderChangeStatusRequest = new OrderChangeStatusRequest("MEAL");

        update("/api/orders/" + saved.getId() + "/order-status", orderChangeStatusRequest)
                .andExpect(jsonPath("$.orderStatus").value("MEAL"));
    }
}