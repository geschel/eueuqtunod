package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.core.logic.exception.NotFoundException;
import de.example.donutqueue.core.logic.exception.SyntaxException;
import de.example.donutqueue.core.util.JsonUtil;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.pojo.OrderRow;

/**
 * Business logic responsible for deleting a specific order.
 */
public class DeleteOrderLogic extends UseCase {

    private final String clientIdParam; //from path param
    private final DaoFactory daoFactory;

    public DeleteOrderLogic(String clientIdParam, DaoFactory daoFactory) {
        this.clientIdParam = clientIdParam;
        this.daoFactory = daoFactory;
    }

    /**
     *
     * Requirement: Client id is an integer.
     *
     * @throws SyntaxException if requirements are not met
     *
     */
    @Override
    public void checkSyntax() throws SyntaxException {
        try {
            Integer.parseInt(clientIdParam);
        } catch (Exception e) {
            throw new SyntaxException("The client id is not a valid number.", null);
        }
    }

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
    public void handleExecution() {
        daoFactory.getOrderDao().delete(Integer.parseInt(this.clientIdParam));
    }

}
