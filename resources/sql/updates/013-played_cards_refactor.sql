alter table played_card drop foreign key played_card_ibfk_1;
alter table played_card drop column white_card_id;

alter table played_card rename to answer;

create table answer_cards (
  white_card_id int not null,
  answer_id int not null,
  position int not null default 1,
  constraint `fk_answer_cards_white_card` foreign key (white_card_id) references white_card(id),
  constraint `fk_answer_cards_answer` foreign key (answer_id) references answer(id)
) ENGINE = INNODB;
