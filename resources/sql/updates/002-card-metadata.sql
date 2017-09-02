alter table white_card add column disabled boolean not null default false;
alter table white_card add column locked boolean not null default false;

alter table black_card add column disabled boolean not null default false;
alter table black_card add column locked boolean not null default false;
