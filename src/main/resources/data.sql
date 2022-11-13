insert into category(category_name) values('Fashion');
insert into category(category_name) values('Electronics');
insert into category(category_name) values('Books');
insert into category(category_name) values('Groceries');
insert into category(category_name) values('Medicines');



insert into Users(username,password) values('jack','pass_word');
insert into Users(username,password) values('bob','pass_word');
insert into Users(username,password) values('apple','pass_word');
insert into Users(username,password) values('glaxo','pass_word');

insert into Cart(total_amount,user_user_id) values(20,1);
insert into Cart(total_amount,user_user_id) values(0,2);

insert into Product(price,product_name,category_id,seller_id) values(29190,'Apple iPad 10.2 8th Gen WiFi iOS Tablet',2,3);
insert into Product(price,product_name,category_id,seller_id) values(10,'Crocin pain relief tablet',5,4);

insert into cart_product(cart_id,product_id,quantity) values(1,2,2);

insert into user_role(user_id,roles) values(1,'CONSUMER');
insert into user_role(user_id,roles) values(2,'CONSUMER');
insert into user_role(user_id,roles) values(3,'SELLER');
insert into user_role(user_id,roles) values(4,'SELLER');