package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.api.request.FillDeliveryCartRequest;
import de.example.donutqueue.core.util.JsonUtil;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.pojo.OrderRow;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic responsible for filling and returning delivery cart.<p>
 * All orders in cart are removed from queue.
 */
public class FillDeliveryCartLogic extends UseCase {

    private static final int CART_MAX_CAPACITY = 50;

    private final DaoFactory daoFactory;
    private final FillDeliveryCartRequest fillCartRequest;

    @Getter
    private List<OrderRow> filledDeliveryCart;

    @Getter
    private int resultPositionInQueue;

    public FillDeliveryCartLogic(FillDeliveryCartRequest fillCartRequest, DaoFactory daoFactory) {
        this.fillCartRequest = fillCartRequest;
        this.daoFactory = daoFactory;
    }

    @Override
    public String dumpInput() {
        return JsonUtil.dump(this.fillCartRequest);
    }

    /**
     * We delete orders from the queue and add them to the delivery cart until at least one statement is true<p>
     * <ul>
     *     <li>the cart reached it's capacity (50 donuts)</li>
     *     <li>the queue is empty</li>
     * </ul>
     */
    @Override
    public void handleExecution() {
        //retrieve current queue
        List<OrderRow> queue = daoFactory.getOrderDao().get(20000L, 0L);

        //add to delivery cart as long as the cart capacity allows it
        this.filledDeliveryCart = new ArrayList<>();
        List<Integer> clientIdsToDelete = new ArrayList<>();
        int currentCartCapacity = CART_MAX_CAPACITY;

        for (OrderRow orderRow : queue) {
            if (orderRow.getQuantity() <= currentCartCapacity) {
                this.filledDeliveryCart.add(orderRow);
                currentCartCapacity -= orderRow.getQuantity();
                clientIdsToDelete.add(orderRow.getClient_id());
            } else {
                break; //stop adding to cart because we must not skip any order
            }
        }

        //bulk delete of all delivery cart orders from queue
        if(!clientIdsToDelete.isEmpty()) {
            daoFactory.getOrderDao().deleteBulk(clientIdsToDelete);
        }

        //queue positions start with 1
        resultPositionInQueue = 1;
    }

}