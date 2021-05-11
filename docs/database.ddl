create table if not exists orders
(
    client_id        int primary key,
    create_time      datetime not null,
    quantity         int      not null,
    premium_customer bool     not null
);