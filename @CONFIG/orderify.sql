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
  `order` LONGTEXT, 
  `comments` VARCHAR(255), 
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
VALUES ('Dania główne');

INSERT INTO `dishes` (`name`, `descS`, `descL`, `categoryID`)
VALUES ('Kurczak w cieście', 'takie on jest dobry', 'araby z południa będą przychodzić i oddawać swoje żony żeby spróbować tego wspaniałego dania', 1);


INSERT INTO `addons` (`name`)
VALUES ('Sałatka z kapusty'), ('Warzywa gotowane'), ('Sos tatarski'), ('Sos czosnkowy'), ('Sos a co cię to obchodzi');

INSERT INTO `addonsCategories` (`name`)
VALUES ('Sałatki'), ('Sosy');

INSERT INTO `addonsCategoriesToAddons` (`addonCategoryID`, `addonID`)
VALUES (1, 1), (1, 2), (2, 3), (2, 4), (2, 5);

INSERT INTO `dishesToAddonsCategoris` (`dishID`, `addonCategoryID`)
VALUES (1, 1), (1, 2);



INSERT INTO `orders` (`tableNumber`, `order`, `comments`)
VALUES (1, 'opis zamówienia', 'z lodem');

INSERT INTO `dishesToOrders` (`dishID`, `orderID`)
VALUES (1, 1);



