create table household
(
    id             bigint auto_increment comment '户ID'
        primary key,
    household_no   varchar(50)                        not null comment '户号',
    area_code      varchar(20)                        not null comment '所属区划',
    province       varchar(50)                        null comment '省',
    city           varchar(50)                        null comment '市',
    district       varchar(50)                        null comment '区',
    detail_address varchar(200)                       null comment '详细地址',
    head_person_id bigint                             null comment '户主ID',
    status         tinyint  default 1                 null comment '状态：1-正常，2-迁出',
    create_time    datetime default CURRENT_TIMESTAMP null,
    constraint household_no
        unique (household_no)
)
    comment '家庭户表';

create table person
(
    id                bigint auto_increment comment '人员ID'
        primary key,
    id_card           varchar(18)                        null comment '身份证号',
    name              varchar(50)                        not null comment '姓名',
    gender            tinyint                            null comment '性别：1-男，2-女',
    nation            varchar(20)                        null comment '民族',
    birthday          date                               null comment '出生日期',
    household_id      bigint                             null comment '所属户ID',
    relation          varchar(20)                        null comment '与户主关系',
    person_type       tinyint  default 1                 null comment '人口类型',
    register_province varchar(50)                        null comment '户籍省',
    register_city     varchar(50)                        null comment '户籍市',
    register_district varchar(50)                        null comment '户籍区',
    register_detail   varchar(200)                       null comment '户籍详细地址',
    current_province  varchar(50)                        null comment '现住省',
    current_city      varchar(50)                        null comment '现住市',
    current_district  varchar(50)                        null comment '现住区',
    current_detail    varchar(200)                       null comment '现住详细地址',
    phone             varchar(20)                        null comment '联系电话',
    person_status     tinyint  default 1                 null comment '人员状态',
    create_time       datetime default CURRENT_TIMESTAMP null,
    constraint id_card
        unique (id_card),
    constraint id_card_2
        unique (id_card),
    constraint person_ibfk_1
        foreign key (household_id) references household (id)
)
    comment '人员信息表';

create table certificate
(
    id          bigint auto_increment comment '证件ID'
        primary key,
    person_id   bigint                             not null comment '人员ID',
    type        tinyint                            not null comment '类型：1-身份证，2-居住证',
    number      varchar(50)                        not null comment '证件号码',
    issue_date  date                               null comment '签发日期',
    expire_date date                               null comment '有效期',
    status      tinyint  default 1                 null comment '状态：1-有效，2-过期，3-挂失',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint fk_certificate_person
        foreign key (person_id) references person (id)
            on update cascade on delete cascade
)
    comment '证件信息表';

create index household_id
    on person (household_id);

create table user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)                           not null comment '用户名',
    password    varchar(255)                          not null comment '密码',
    real_name   varchar(50)                           not null comment '真实姓名',
    role        varchar(20) default 'OFFICER'         null comment '角色：ADMIN, OFFICER',
    status      tinyint     default 1                 null comment '状态：1-正常，0-禁用',
    create_time datetime    default CURRENT_TIMESTAMP null,
    constraint username
        unique (username)
)
    comment '系统用户表';


