alter table white_deck_cards drop foreign key white_deck_cards_ibfk_2;
alter table white_discard_cards drop foreign key white_discard_cards_ibfk_2;
alter table black_deck_cards drop foreign key black_deck_cards_ibfk_2;
alter table black_discard_cards drop foreign key black_discard_cards_ibfk_2;

alter table white_deck_cards drop column white_deck_id;
alter table white_discard_cards drop column white_discard_id;
alter table black_deck_cards drop column black_deck_id;
alter table black_discard_cards drop column black_discard_id;

drop table white_deck;
drop table white_discard;
drop table black_deck;
drop table black_discard;

alter table white_deck_cards add column game_id int not null;
alter table white_discard_cards add column game_id int not null;
alter table black_deck_cards add column game_id int not null;
alter table black_discard_cards add column game_id int not null;

alter table white_deck_cards add constraint fk_white_deck_game FOREIGN KEY (game_id) references game(id);
alter table white_discard_cards add constraint fk_white_discard_game FOREIGN KEY (game_id) references game(id);
alter table black_deck_cards add constraint fk_black_deck_game FOREIGN KEY (game_id) references game(id);
alter table black_discard_cards add constraint fk_black_discard_game FOREIGN KEY (game_id) references game(id);
