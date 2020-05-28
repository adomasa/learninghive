-- insert default restrictions
--todo: change default quarter day limit to 3
insert into restrictions (id, restriction_type, days_limit, date) values(1, 3, 30, CURRENT_TIMESTAMP);