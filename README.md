# Invest-Portfolio
Invest Portfolio pet project

This app helps you to control your investing portfolio on Moscow Stock Exchange. 
App gets share prices directly from Moscow Stock Exchange via its API, and provides you actual financial result of your portfolio.

User creates a profile, and then he can create portfolios for stocks. 
Stocks added to portfolio by creating deals which include security name, price, quantity, commissions etc.
Portfolio creates position for each stock which accumulates deals for this stock.
App provides you information for profit or loss for each position and for every portfolio.

To run this app locally you need: 
1. Postgres (14.4 was used for dev): 
 - database name: investportfolio_dev;
 - user: postgres;
 - password: postgres;
 - port: 5432.
2. Run app with command - mvn spring-boot:run -D"spring-boot.run.profiles=dev". 
Or run manually with IDE, set "Active profiles" to "dev".

You can also make a docker image, docker file is in the root folder. Container will run with spring profile "dev".
You'll need to configure datasource properties, to let your container find the database.

Liquibase is used to create all DDL in database.
Also, a test account will be created with username 'admin@admin.com' and password 'admin'.
Fell free to use it for test-drive or create a new one.