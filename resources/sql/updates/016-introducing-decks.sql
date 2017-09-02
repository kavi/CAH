create table language (
  id int not null primary key auto_increment,
  language varchar(64) not null unique
) ENGINE = INNODB;

create table deck (
  id int not null primary key auto_increment,
  owner_id int,
  language_id int not null,
  name  varchar(64) not null,
  public_read boolean not null default true,
  public_write boolean not null default true,
  constraint `fk_deck_language` foreign key (language_id) references language(id),
  constraint `fk_deck_owner` foreign key (owner_id) references user(id)
) ENGINE = INNODB;


ALTER TABLE `deck` ADD UNIQUE `unique_deck_name`(`language_id`, `name`);

create table deck_editor (
  user_id int,
  deck_id int,
  constraint `fk_deck_editor_user` foreign key (user_id) references user(id),
  constraint `fk_deck_editor_deck` foreign key (deck_id) references deck(id)
) ENGINE = INNODB;

ALTER TABLE `deck_editor` ADD UNIQUE `unique_deck_editor`(`user_id`, `deck_id`);

insert into language (id, language) values (1, 'Danish');
insert into language (id, language) values (2, 'English');

insert into deck (id, language_id, name) values (1, 1, 'Grundspil');
insert into deck (id, language_id, name) values (2, 2, 'Base');

update deck set owner_id = (select id from user where name = 'Admin') where id in (1,2); 
alter table deck MODIFY owner_id int not null;

alter table white_card add column deck_id int;
update white_card set deck_id = 1;
alter table white_card add constraint fk_white_card_deck_id FOREIGN KEY (deck_id) references deck(id);
alter table white_card MODIFY deck_id int not null;

alter table black_card add column deck_id int;
update black_card set deck_id = 1;
alter table black_card add constraint fk_black_card_deck_id FOREIGN KEY (deck_id) references deck(id);
alter table black_card MODIFY deck_id int not null;

create table game_decks (
  game_id int,
  deck_id int,
  constraint `fk_game_decks_game` foreign key (game_id) references game(id),
  constraint `fk_game_decks_deck` foreign key (deck_id) references deck(id)
) ENGINE = INNODB;