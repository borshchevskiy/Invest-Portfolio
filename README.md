# Invest-Portfolio
Invest Portfolio pet project

This app helps you to control your investing portfolio on Moscow Stock Exchange. 
App gets share prices directly from Moscow Stock Exchange via its API, and provides you actual financial result of your portfolio.

User creates a profile, and then he can create portfolios for stocks. 
Stocks added to portfolio by creating deals which include security name, price, quantity, commissions etc.
Portfolio creates position for each stock which accumulates deals for this stock.
App provides you information for profit or loss for each position and for црщду portfolio.

To run this app locally you need: 
1. Postgres (14.4 was used for dev): 
 - database name: investportfolio_dev;
 - user: postgres;
 - password: postgres;
 - port: 5432.
2. Set Spring Boot active profile to 'dev'.

Liquibase is used here, it will create all DDL in database.
Also, a test account will be created with username 'admin@admin.com' and password 'admin'.
Fell free to use it for test-drive or create a new one.