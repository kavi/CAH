alter table white_card add column changed_by integer default null;
alter table white_card add constraint fk_white_card_changed_by FOREIGN KEY (changed_by) references user(id);

alter table black_card add column changed_by integer default null;
alter table black_card add constraint fk_black_card_changed_by FOREIGN KEY (changed_by) references user(id);

update white_card set changed_by = (select id from user where name = 'system');
update black_card set changed_by = (select id from user where name = 'system');

alter table white_card MODIFY changed_by int not null;
alter table black_card MODIFY changed_by int not null;
