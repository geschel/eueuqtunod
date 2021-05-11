package de.example.donutqueue.db.dao;

import de.example.donutqueue.db.pojo.OrderRow;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;
import java.util.List;


/**
 * Dao for table 'orders'.
 */
@RegisterBeanMapper(OrderRow.class)
public interface OrderDao {

    /*
     * Create
     */
    @SqlUpdate("create table if not exists orders\n" +
            "(\n" +
            "    client_id int primary key,\n" +
            "    create_time datetime not null,\n" +
            "    quantity int not null,\n" +
            "    premium_customer bool not null\n" +
            ")")
    void createTable();

    @SqlUpdate("INSERT INTO " + OrderRow.TABLE_NAME + " (client_id, create_time, quantity, premium_customer) VALUES (:b.client_id, :b.create_time, :b.quantity, :b.premium_customer)")
    void insert(@BindBean("b") OrderRow order);

    /*
     * Read
     */
    @SqlQuery("SELECT * FROM " + OrderRow.TABLE_NAME + " WHERE client_id = :client_id")
    OrderRow getByClientId(@Bind("client_id") Integer clientId);

    @SqlQuery("SELECT * FROM " + OrderRow.TABLE_NAME + " ORDER BY premium_customer desc, create_time asc LIMIT :limit OFFSET :offset")
    List<OrderRow> get(@Bind("limit") Long limit, @Bind("offset") Long offset);

    @SqlQuery("SELECT COUNT(*) FROM " + OrderRow.TABLE_NAME + " WHERE premium_customer = true OR (premium_customer = false AND create_time < :create_time )")
    int countPremiumsAndOlderNonPremiums(@Bind("create_time") Timestamp create_time);

    @SqlQuery("SELECT COUNT(*) FROM " + OrderRow.TABLE_NAME + " WHERE premium_customer = true AND create_time < :create_time")
    int countOlderPremiums(@Bind("create_time") Timestamp createTime);

    /*
     * Delete
     */
    @SqlUpdate("DELETE FROM " + OrderRow.TABLE_NAME + " WHERE client_id = :client_id")
    void delete(@Bind("client_id") Integer clientId);

    @SqlUpdate("DELETE FROM " + OrderRow.TABLE_NAME + " WHERE client_id IN (<client_ids>)")
    void deleteBulk(@BindList("client_ids") List<Integer> clientIdsToDelete);
}