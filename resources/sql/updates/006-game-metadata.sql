alter table game add column owner_id int;
alter table game add column started boolean not null default false;
alter table game add column password varchar(24);

alter table game add constraint fk_game_owner_id FOREIGN KEY (owner_id) references user(id);

