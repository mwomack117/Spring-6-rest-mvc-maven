
    drop table if exists beer;

    drop table if exists customer;

    create table beer (
        beer_style tinyint not null check (beer_style between 0 and 8),
        price decimal(38,2) not null,
        quantity_on_hand integer,
        version integer,
        created_date datetime(6),
        update_date datetime(6),
        beer_name varchar(30) not null,
        id varchar(36) not null,
        upc varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer (
        version integer,
        create_date datetime(6),
        update_date datetime(6),
        id varchar(36) not null,
        first_name varchar(255),
        last_name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    drop table if exists beer;

    drop table if exists customer;

    create table beer (
        beer_style tinyint not null check (beer_style between 0 and 8),
        price decimal(38,2) not null,
        quantity_on_hand integer,
        version integer,
        created_date datetime(6),
        update_date datetime(6),
        beer_name varchar(30) not null,
        id varchar(36) not null,
        upc varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer (
        version integer,
        create_date datetime(6),
        update_date datetime(6),
        id varchar(36) not null,
        first_name varchar(255),
        last_name varchar(255),
        primary key (id)
    ) engine=InnoDB;
