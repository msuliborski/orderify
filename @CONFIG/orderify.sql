########################################################################
######################## Create database 'Orderify'
CREATE DATABASE IF NOT EXISTS `Orderify`;
USE `Orderify`;

########################################################################
######################## Table 'dishesCategories'
DROP TABLE IF EXISTS `dishesCategories`;
CREATE TABLE `dishesCategories` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `name` VARCHAR(255) NOT NULL, 
  `imgPath` VARCHAR(255), 
  PRIMARY KEY (`ID`)
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'dishes'
DROP TABLE IF EXISTS `dishes`;
CREATE TABLE `dishes` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `name` VARCHAR(255) NOT NULL, 
  `price` INTEGER NOT NULL, 
  `descS` VARCHAR(255), 
  `descL` LONGTEXT, 
  `categoryID` INTEGER NOT NULL DEFAULT 0, 
  INDEX (`categoryID`), 
  PRIMARY KEY (`ID`),
  FOREIGN KEY (categoryID) REFERENCES dishesCategories(ID) 
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'addons'
DROP TABLE IF EXISTS `addons`;
CREATE TABLE `addons` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `name` VARCHAR(255) NOT NULL, 
  `price` INTEGER NOT NULL, 
  PRIMARY KEY (`ID`)
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'addonsCategories'
DROP TABLE IF EXISTS `addonsCategories`;
CREATE TABLE `addonsCategories` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `name` VARCHAR(255) NOT NULL, 
  PRIMARY KEY (`ID`)
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'addonsCategoriesToAddons'
DROP TABLE IF EXISTS `addonsCategoriesToAddons`;
CREATE TABLE `addonsCategoriesToAddons` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `addonCategoryID` INTEGER NOT NULL DEFAULT 0, 
  `addonID` INTEGER NOT NULL DEFAULT 0, 
  PRIMARY KEY (`ID`),
  FOREIGN KEY (addonCategoryID) REFERENCES addonsCategories(ID),
  FOREIGN KEY (addonID) REFERENCES addons(ID) 
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'dishesToAddonsCategoris'
DROP TABLE IF EXISTS `dishesToAddonsCategoris`;
CREATE TABLE `dishesToAddonsCategoris` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `dishID` INTEGER NOT NULL DEFAULT 0, 
  `addonCategoryID` INTEGER NOT NULL DEFAULT 0, 
  INDEX (`addonCategoryID`), 
  INDEX (`dishID`), 
  PRIMARY KEY (`ID`),
  FOREIGN KEY (dishID) REFERENCES dishes(ID), 
  FOREIGN KEY (addonCategoryID) REFERENCES addonsCategories(ID) 
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'orders'
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `tableNumber` INTEGER NOT NULL DEFAULT 0, 
  `orderText` LONGTEXT, 
  `comments` VARCHAR(255), 
  `time` TIME(), 
  `date` DATE(), 
  PRIMARY KEY (`ID`), 
  INDEX (`tableNumber`)
) ENGINE=myisam DEFAULT CHARSET=utf8;

########################################################################
######################## Table 'dishesToOrders'
DROP TABLE IF EXISTS `dishesToOrders`;
CREATE TABLE `dishesToOrders` (
  `ID` INTEGER NOT NULL AUTO_INCREMENT, 
  `dishID` INTEGER DEFAULT 0, 
  `orderID` INTEGER DEFAULT 0, 
  INDEX (`dishID`), 
  INDEX (`orderID`), 
  PRIMARY KEY (`ID`),
  FOREIGN KEY (dishID) REFERENCES dishes(ID),
  FOREIGN KEY (orderID) REFERENCES orders(ID) 
) ENGINE=myisam DEFAULT CHARSET=utf8;






INSERT INTO `dishesCategories` (`name`)
VALUES ('Pizza'), ('Dania główne'), ('Zupy'), ('Napoje');

INSERT INTO `dishes` (`name`, `price`, `descS`, `descL`, `categoryID`)
VALUES 
  ('Margarita', 15, 'short', 'long', 1),                             #1
  ('Peperoni', 17, 'short', 'long', 1),                               #2
  ('Hawajska', 18, 'short', 'long', 1),                               #3
  ('Kurczak w cieście', 13, 'takie on jest dobry', 'araby z południa będą przychodzić i oddawać swoje żony żeby spróbować tego wspaniałego dania', 1),   #4
  ('Sznycel', 80, 'short', 'long', 1),                                #5
  ('Dewolaj', 15, 'short', 'long', 1),                                #6
  ('Zupa pomidorowa', 5, 'short', 'long', 1),                        #7
  ('Zupa ogórkowa', 5, 'short', 'long', 1),                          #8
  ('Piwo', 5, 'short', 'long', 1),                                   #9
  ('Cola', 4, 'short', 'long', 1),                                   #10
  ('Sok pomarańczowy', 3, 'short', 'long', 1);                       #11


INSERT INTO `addons` (`name`, `price`)
VALUES ('Sos tatarski', 1), ('Sos czosnkowy', 0), ('Sos pomidorowy', 0),      #1-3
('Mała', 0), ('Średnia', 7), ('Duża', 13),                                    #4-6
('Sałatka z kapusty', 0), ('Warzywa gotowane', 0), ('Marchewka', 0),          #7-9
('Ziemniaki', 0), ('Pieczone ziemniaki', 0), ('Frytki', 0), ('Kluski', 0),    #10-13
('Lód', 0);                                                                   #14

INSERT INTO `addonsCategories` (`name`)
VALUES ('Sosy'), ('Rozmiary'), ('Sałatki'), ('Zapychacze'), ('Do napojów');  #1-5

INSERT INTO `addonsCategoriesToAddons` (`addonCategoryID`, `addonID`)
VALUES (1, 1), (1, 2), (1, 3),  
(2, 4), (2, 5), (2, 6),
(3, 7), (3, 8), (3, 9),
(4, 10), (4, 11), (4, 12), (4, 13),
(5, 14);

INSERT INTO `dishesToAddonsCategoris` (`dishID`, `addonCategoryID`)
VALUES (1, 1), (1, 2);


INSERT INTO `orders` (`tableNumber`, `order`, `time`, `date`, `comments`)
VALUES (1, 'opis zamówienia', `21:37:00`, `2018-07-31`, 'z lodem');                                                                 #1
VALUES (1, 'opis zamówienia', `21:38:11`, `2018-07-31`, 'arek pedal');                                                              #2
VALUES (2, 'opis zamówienia', `21:41:22`, `2018-07-31`, 'to ja szukam czy ci chlopi maja proce');                                   #3
VALUES (2, 'opis zamówienia', `21:49:22`, `2018-07-31`, 'rzucam sobie na SQL');                                                     #4
VALUES (2, 'opis zamówienia', `21:49:33`, `2018-07-31`, 'po co ja się w ogóle sile z tymi uwagami, nikt tego nie przeczyta :(');    #5
VALUES (3, 'opis zamówienia', `21:54:44`, `2018-07-31`, 'skończyłem');                                                              #6

INSERT INTO `dishesToOrders` (`dishID`, `orderID`)
VALUES (1, 1);



