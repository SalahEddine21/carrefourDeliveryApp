DROP TABLE IF EXISTS delivery_products;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS delivery;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS delivery_slot;

create table customer
(
    id         bigint auto_increment
        primary key,
    email      varchar(255) null,
    first_name varchar(255) null,
    last_name  varchar(255) null,
    password   varchar(255) null,
    phone      varchar(255) null
);

create table delivery_slot (
   id bigint auto_increment primary key,
   weekday int null,
   end_time time null,
   max_reservations int null,
   mode varchar(255) not null,
   start_time time null
);

create table delivery
(
    id               bigint auto_increment
        primary key,
    created_at       datetime(6)                                                      null,
    delivery_address varchar(255)                                                     null,
    status           varchar(255) null,
    customer_id      bigint                                                           not null,
    delivery_slot_id bigint                                                           not null,
    constraint FK7pha7cqoupw87x6nyfau16jni
        foreign key (delivery_slot_id) references delivery_slot (id),
    constraint FKr0mg2e4p18frsju6qut84g8fs
        foreign key (customer_id) references customer (id)
);

create table product
(
    id             bigint auto_increment
        primary key,
    code           varchar(255) null,
    name           varchar(255) null,
    price          double       null,
    stock_quantity int          null
);

create table delivery_products
(
    quantity    int    not null,
    delivery_id bigint not null,
    product_id  bigint not null,
    primary key (delivery_id, product_id),
    constraint FKlvcc4slmi656qfe6tj3elw6hy
        foreign key (delivery_id) references delivery (id),
    constraint FKn99v2vv9atosc1wns4lax9i8u
        foreign key (product_id) references product (id)
);