package de.example.donutqueue.db.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * A row in table 'orders'.<p>
 * Member names do not follow java naming conventions in order to make database mapping automatic.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRow {

    public static final String TABLE_NAME = "orders";

    //Member names do not follow java naming conventions in order to make database mapping easier
    private Integer client_id; //primary key, between 1 and 20000
    private Timestamp create_time; //longer in queue -> higher priority
    private Integer quantity; //quantity of ordered donuts
    private Boolean premium_customer; //premium orders get priority treatment
}