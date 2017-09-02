alter table white_card drop column locked;
alter table white_card add column parent_id integer default null;
alter table white_card add constraint fk_white_card_parent_id FOREIGN KEY (parent_id) references white_card(id);

alter table black_card drop column locked;
alter table black_card add column parent_id integer default null;
alter table black_card add constraint fk_black_card_parent_id FOREIGN KEY (parent_id) references black_card(id);