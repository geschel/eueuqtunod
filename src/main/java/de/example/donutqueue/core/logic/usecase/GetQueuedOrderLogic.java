package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.core.logic.exception.NotFoundException;
import de.example.donutqueue.core.logic.exception.SyntaxException;
import de.example.donutqueue.core.util.JsonUtil;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.dao.OrderDao;
import de.example.donutqueue.db.pojo.OrderRow;
import lombok.Getter;

/**
 * Business logic responsible for retrieving a specific order.
 */
public class GetQueuedOrderLogic extends UseCase {

    private final String clientIdParam; //from path param
    private final DaoFactory daoFactory;

    @Getter
    private OrderRow resultOrder;

    @Getter
    private int resultPositionInQueue;

    public GetQueuedOrderLogic(String clientIdParam, DaoFactory daoFactory) {
        this.clientIdParam = clientIdParam;
        this.daoFactory = daoFactory;
    }

    /**
     * Requirement: Client id is an integer between 1 and 20000.
     *
     * @throws SyntaxException if requirements are not met
     */
    @Override
    public void checkSyntax() throws SyntaxException {
        int parsedNumber;
        try {
            parsedNumber = Integer.parseInt(clientIdParam);
        } catch (Exception e) {
            throw new SyntaxException("The client id is not a valid number.", null);
        }
        if(parsedNumber < 0 || parsedNumber > 20000) {
            throw new SyntaxException("The client id is not a valid number between 1 and 20000.", null);
        }
    }

    /**
     * Requirement: The order exists.
     *
     * @throws NotFoundException - if order does not exist
     */
    @Override
    public void checkRequirements() throws NotFoundException {
        OrderRow orderFromDatabase = daoFactory.getOrderDao().getByClientId(Integer.parseInt(this.clientIdParam));
        if (null == orderFromDatabase) {
            throw new NotFoundException("Order not found.", null);
        }
    }

    @Override
    public String dumpInput() {
        return JsonUtil.dump(this.clientIdParam);
    }

    @Override
    public void handleExecution() throws NotFoundException {
        //we retrieve the user's order
        OrderDao orderDao = daoFactory.getOrderDao();
        this.resultOrder = orderDao.getByClientId(Integer.parseInt(this.clientIdParam));
        if(null == this.resultOrder) {
            throw new NotFoundException("Order not found.", "Your order has been processed recently.");
        }
        //we calculate the order position by adding all orders that are longer in the queue than current order
        if (this.resultOrder.getPremium_customer()) {
            this.resultPositionInQueue = 1 + orderDao.countOlderPremiums(this.resultOrder.getCreate_time());
        } else {
            this.resultPositionInQueue = 1 + orderDao.countPremiumsAndOlderNonPremiums(this.resultOrder.getCreate_time());
        }
    }

}
