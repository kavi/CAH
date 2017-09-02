create table version_history (
  id int not null primary key auto_increment,
  version int not null,
  filename varchar(512) not null
) ENGINE = INNODB;
