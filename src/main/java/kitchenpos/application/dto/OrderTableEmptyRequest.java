package kitchenpos.application.dto;

public class OrderTableEmptyRequest {

    private boolean empty;

    public OrderTableEmptyRequest() {
    }

    public OrderTableEmptyRequest(boolean empty) {
        this.empty = empty;
    }

    public boolean getEmpty() {
        return empty;
    }
}
