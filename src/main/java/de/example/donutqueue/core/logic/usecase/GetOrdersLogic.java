package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.core.logic.exception.NotFoundException;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.pojo.OrderRow;
import lombok.Getter;

import java.util.List;

/**
 * Business logic responsible for retrieving the order queue.
 */
public class GetOrdersLogic extends UseCase {

    private final DaoFactory daoFactory;

    /**
     * All queued orders in the right sort order.
     */
    @Getter
    private List<OrderRow> resultOrders;

    /**
     * The queue position of the first order in the result set.
     */
    @Getter
    private int resultOffsetFirstOrder;

    public GetOrdersLogic(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * No params
     */
    @Override
    public String dumpInput() {
        return "";
    }

    @Override
    public void handleExecution() throws NotFoundException {
        //we retrieve the user's order
        this.resultOrders = daoFactory.getOrderDao().get(20000L, 0L);
        this.resultOffsetFirstOrder = 1;
    }

}