package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class TableServiceTest extends ApplicationTest {

    @Autowired
    private TableService tableService;

    @DisplayName("테이블을 조회한다.")
    @Test
    void list() {
        주문테이블_생성(new OrderTable(null, 0, true));
        주문테이블_생성(new OrderTable(null, 0, true));

        List<OrderTable> tables = tableService.list();

        assertThat(tables).hasSize(2);
    }

    @DisplayName("테이블의 empty를 수정한다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean empty) {
        OrderTable table = 주문테이블_생성(new OrderTable(null, 5, true));
        OrderTable updateTable = new OrderTable(table.getId(), table.getNumberOfGuests(), empty);

        OrderTable updatedTable = tableService.changeEmpty(table.getId(), updateTable);

        List<OrderTable> tables = tableService.list();
        OrderTable foundTable = tables.stream()
                .filter(t -> updatedTable.getId().equals(t.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(foundTable).extracting("empty").isEqualTo(empty);
    }
}
