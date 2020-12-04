--
-- File generated with SQLiteStudio v3.2.1 on Fri Dec 4 15:50:48 2020
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: EmployeeData
CREATE TABLE EmployeeData (employee_id VARCHAR (4) PRIMARY KEY, employee_name VARCHAR, employee_pass VARCHAR, front_or_back VARCHAR (3));
INSERT INTO EmployeeData (employee_id, employee_name, employee_pass, front_or_back) VALUES ('0001', 'John Smith', 'plain&simple', 'FOH');
INSERT INTO EmployeeData (employee_id, employee_name, employee_pass, front_or_back) VALUES ('0002', 'Bruce Green', 'skab4raggae', 'BOH');
INSERT INTO EmployeeData (employee_id, employee_name, employee_pass, front_or_back) VALUES ('0003', 'Michael Reeves', 'codeGoblin', 'FOH');

-- Table: Inventory
CREATE TABLE Inventory (item_id VARCHAR (3) PRIMARY KEY, use_id VARCHAR, item_name VARCHAR, quantity VARCHAR (3), expiration_date DATE);
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('124', 'preparation', 'small pot', '15', NULL);
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('125', 'uniform', 'chef jacket', '4', NULL);
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('126', 'sanitation', 'disinfectant wipes', '200', NULL);
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('127', 'uniform', 'parking safety vest', '15', NULL);
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('300', 'dessert', 'jello mix', '40', '2038-12-02');
INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES ('301', 'dinner', 'beef bullion', '200', '2022-10-19');

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
