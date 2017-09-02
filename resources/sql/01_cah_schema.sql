create table user (
  id int not null primary key auto_increment,
  password varchar(128) not null,
  name varchar(128) not null unique,
  email varchar(512) not null
) ENGINE = INNODB;

create table game_phase (
  id int not null primary key auto_increment,
  name varchar(32) not null unique
) ENGINE = INNODB;

create table game (
  id int not null primary key auto_increment,
  name varchar(60) not null unique,
  active boolean not null default true,
  phase_id int default 1,
  foreign key (phase_id) references game_phase(id)
) ENGINE = INNODB;

create table player (
  id int not null primary key auto_increment,
  user_id int not null,
  game_id int not null,
  points int not null default 0,
  foreign key (game_id) references game(id),
  foreign key (user_id) references user(id)
) ENGINE = INNODB;

create table white_card (
  id int not null primary key auto_increment,
  text varchar(1024) not null
) ENGINE = INNODB;

create table player_card (
  id int not null primary key auto_increment,
  player_id int not null,
  white_card_id int not null,
  foreign key (white_card_id) references white_card(id),
  foreign key (player_id) references player(id)  
) ENGINE = INNODB;

create table white_discard (
  id int not null primary key auto_increment,
  game_id int not null,
  foreign key (game_id) references game(id)
) ENGINE = INNODB;

create table white_discard_cards (
  id int not null primary key auto_increment,
  white_card_id int not null,
  white_discard_id int not null,
  foreign key (white_card_id) references white_card(id),
  foreign key (white_discard_id) references white_discard(id)
) ENGINE = INNODB;

create table white_deck (
  id int not null primary key auto_increment,
  game_id int not null,
  foreign key (game_id) references game(id)
) ENGINE = INNODB;

create table white_deck_cards (
  id int not null primary key auto_increment,
  white_card_id int not null,
  white_deck_id int not null,
  foreign key (white_card_id) references white_card(id),
  foreign key (white_deck_id) references white_deck(id)
) ENGINE = INNODB;


create table black_card (
  id int not null primary key auto_increment,
  text varchar(1024) not null,
  cards_to_pick int not null default 1,
  cards_to_draw int not null default 0
) ENGINE = INNODB;

create table black_discard (
  id int not null primary key auto_increment,
  game_id int not null,
  foreign key (game_id) references game(id)
) ENGINE = INNODB;

create table black_discard_cards (
  id int not null primary key auto_increment,
  black_card_id int not null,
  black_discard_id int not null,
  foreign key (black_card_id) references black_card(id),
  foreign key (black_discard_id) references black_discard(id)
) ENGINE = INNODB;

create table black_deck (
  id int not null primary key auto_increment,
  game_id int not null,
  foreign key (game_id) references game(id)
) ENGINE = INNODB;

create table black_deck_cards (
  id int not null primary key auto_increment,
  black_card_id int not null,
  black_deck_id int not null,
  foreign key (black_card_id) references black_card(id),
  foreign key (black_deck_id) references black_deck(id)
) ENGINE = INNODB;


create table question (
  id int not null primary key auto_increment,
  active boolean not null default false,
  game_id int not null,
  foreign key (game_id) references game(id)  
) ENGINE = INNODB;

create table played_card (
  id int not null primary key auto_increment,
  white_card_id int not null,
  question_id int not null,
  player_id int not null,
  foreign key (white_card_id) references white_card(id),  
  foreign key (question_id) references question(id),
  foreign key (player_id) references player(id)  
) ENGINE = INNODB;

create table vote (
  id int not null primary key auto_increment,
  score int not null default 0,
  played_card_id int not null,
  player_id int not null,
  foreign key (played_card_id) references played_card(id), 
  foreign key (player_id) references player(id)  
) ENGINE = INNODB;