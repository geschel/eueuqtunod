package de.example.donutqueue.db;

import de.example.donutqueue.db.dao.OrderDao;
import org.jdbi.v3.core.Jdbi;

/**
 * Instantiates data access objects on demand.
 */
public class DaoFactoryImpl implements DaoFactory {

    private static DaoFactoryImpl instance = null;

    private final Jdbi jdbi;

    public DaoFactoryImpl(Jdbi jdbi) {
        super();
        this.jdbi = jdbi;
    }

    public static DaoFactoryImpl getInstance() {
        return instance;
    }

    public static void setInstance(DaoFactoryImpl inst) {
        instance = inst;
    }

    @Override
    public OrderDao getOrderDao() {
        return jdbi.onDemand(OrderDao.class);
    }

}