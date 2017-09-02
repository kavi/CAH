alter table user add column family_filter boolean not null default true;
alter table game add column family_filter boolean not null default true;

alter table white_card add column safe_for_work boolean not null default false;
alter table black_card add column safe_for_work boolean not null default false;
