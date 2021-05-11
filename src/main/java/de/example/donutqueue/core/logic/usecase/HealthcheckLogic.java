package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.db.DaoFactory;

/**
 * Checks if database is accessible.
 */
public class HealthcheckLogic extends UseCase {

    private final DaoFactory daoFactory;

    public HealthcheckLogic(DaoFactory daoFactory) {
        super();
        this.daoFactory = daoFactory;
    }

    @Override
    public void handleExecution() {
        daoFactory.getOrderDao().getByClientId(1);
    }

    @Override
    public String dumpInput() {
        return null;
    }
}