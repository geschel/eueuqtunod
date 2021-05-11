package de.example.donutqueue.db;

import de.example.donutqueue.db.dao.OrderDao;

/**
 * Instantiates DAOs for specific database tables. Can easily be mocked for unit tests.
 */
public interface DaoFactory {
    OrderDao getOrderDao();
}