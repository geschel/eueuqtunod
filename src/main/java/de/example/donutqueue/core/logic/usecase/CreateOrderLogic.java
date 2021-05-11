package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.api.request.CreateOrderRequest;
import de.example.donutqueue.core.logic.exception.ForbiddenException;
import de.example.donutqueue.core.logic.exception.SyntaxException;
import de.example.donutqueue.core.util.JsonUtil;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.dao.OrderDao;
import de.example.donutqueue.db.pojo.OrderRow;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Business logic responsible for creating new queued orders.
 */
public class CreateOrderLogic extends UseCase {

    private final CreateOrderRequest creationRequest;
    private final DaoFactory daoFactory;

    @Getter
    private OrderRow resultOrder;

    @Getter
    private int resultPositionInQueue;

    public CreateOrderLogic(CreateOrderRequest createRequest, DaoFactory daoFactory) {
        this.creationRequest = createRequest;
        this.daoFactory = daoFactory;
    }

    /**
     * Attributes client_id and quantity must be present.
     * client id is between 1 and 20000 and quantity between 1 and 50
     *
     * @throws SyntaxException if requirements are not met
     */
    @Override
    public void checkSyntax() throws SyntaxException {
        if (null == creationRequest) {
            throw new SyntaxException("Empty request.", "Please provide client_id and quantity.");
        }
        if (null == creationRequest.getClientId()) {
            throw new SyntaxException("Missing param.", "Please provide client_id.");
        }
        if (creationRequest.getClientId() < 1 || creationRequest.getClientId() > 20000) {
            throw new SyntaxException("Bad param.", "The client_id should be between 1 and 20000.");
        }
        if (null == creationRequest.getQuantity()) {
            throw new SyntaxException("Missing param.", "Please provide quantity of donuts.");
        }
        if (creationRequest.getQuantity() < 1 || creationRequest.getQuantity() > 50) {
            throw new SyntaxException("Bad param.", "You may only order between 1 and 50 donuts.");
        }
    }

    /**
     * Each client can only have one order active. Throw a {@link ForbiddenException} if client already has an open order
     *
     * @throws ForbiddenException - reason why execution is forbidden
     */
    @Override
    public void checkRequirements() throws ForbiddenException {
        OrderRow fromDatabase = daoFactory.getOrderDao().getByClientId(creationRequest.getClientId());
        if (null != fromDatabase) {
            throw new ForbiddenException("Client already has a queued order.", "Each client id can only have one active order. You can either wait until it is processed or you can delete your current order.");
        }
    }

    @Override
    public String dumpInput() {
        return JsonUtil.dump(this.creationRequest);
    }

    @Override
    public void handleExecution() {
        //we create a new order
        OrderDao orderDao = daoFactory.getOrderDao();
        orderDao.insert(buildNewOrderInstance());
        this.resultOrder = orderDao.getByClientId(creationRequest.getClientId());

        //we calculate the order position by adding all orders that are longer in the queue than current order
        if (this.resultOrder.getPremium_customer()) {
            this.resultPositionInQueue = 1 + orderDao.countOlderPremiums(this.resultOrder.getCreate_time());
        } else {
            this.resultPositionInQueue = 1 + orderDao.countPremiumsAndOlderNonPremiums(this.resultOrder.getCreate_time());
        }
    }

    private OrderRow buildNewOrderInstance() {
        final boolean premium = creationRequest.getClientId() < 1000;
        return new OrderRow(
                creationRequest.getClientId(),
                Timestamp.from(Instant.now()),
                creationRequest.getQuantity(),
                premium);
    }
}