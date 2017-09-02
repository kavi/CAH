alter table game add column hand_size int not null default 10;
alter table game add column rounds_played int not null default 0;
alter table game add column number_of_rounds int not null default 25;
alter table game add column finished boolean not null default false;
