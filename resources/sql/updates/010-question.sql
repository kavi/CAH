alter table question add column black_card_id int not null;

alter table question add constraint fk_question_black_card_id FOREIGN KEY (black_card_id) references black_card(id);
