CREATE DATABASE IF NOT EXISTS `Orderify`;

USE `Orderify`;

DROP TABLE IF EXISTS `tables`;
DROP TABLE IF EXISTS `dishesCategories`;
DROP TABLE IF EXISTS `dishes`;
DROP TABLE IF EXISTS `addonsCategories`;
DROP TABLE IF EXISTS `addons`;
DROP TABLE IF EXISTS `addonsCategorisToDishes`;
DROP TABLE IF EXISTS `wishes`;
DROP TABLE IF EXISTS `addonsToWishes`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `wishesToOrders`;

DROP TABLE IF EXISTS `dishesToOrders`;
DROP TABLE IF EXISTS `addonsToOrder`;

DROP TABLE IF EXISTS `addonsCategoriesToAddons`;
DROP TABLE IF EXISTS `dishesToAddonsCategoris`;


CREATE TABLE `tables` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(255) NOT NULL, 
    PRIMARY KEY (`ID`)
)   ENGINE=myisam DEFAULT CHARSET=utf8;


CREATE TABLE `dishesCategories` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(255) NOT NULL, 
    `imgPath` VARCHAR(255), 
    PRIMARY KEY (`ID`)
)   ENGINE=myisam DEFAULT CHARSET=utf8;


CREATE TABLE `dishes` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(255) NOT NULL, 
    `price` INTEGER NOT NULL DEFAULT 0, 
    `descS` VARCHAR(255), 
    `descL` LONGTEXT, 
    `categoryID` INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (`ID`),
    FOREIGN KEY (categoryID) REFERENCES dishesCategories(ID) 
)   ENGINE=myisam DEFAULT CHARSET=utf8;

CREATE TABLE `addonsCategories` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(255) NOT NULL, 
    `multiChoice` BOOLEAN DEFAULT false, 
    PRIMARY KEY (`ID`)
)   ENGINE=myisam DEFAULT CHARSET=utf8;

CREATE TABLE `addons` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(255) NOT NULL, 
    `price` INTEGER NOT NULL DEFAULT 0, 
    `addonCategoryID` INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (`ID`),
    FOREIGN KEY (addonCategoryID) REFERENCES addonsCategories(ID)
)   ENGINE=myisam DEFAULT CHARSET=utf8;


CREATE TABLE `addonsCategorisToDishes` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `dishID` INTEGER NOT NULL DEFAULT 0, 
    `addonCategoryID` INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (`ID`),
    FOREIGN KEY (dishID) REFERENCES dishes(ID), 
    FOREIGN KEY (addonCategoryID) REFERENCES addonsCategories(ID) 
)   ENGINE=myisam DEFAULT CHARSET=utf8;


CREATE TABLE `wishes` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `dishID` INTEGER DEFAULT 0, 
    `amount` INTEGER DEFAULT 1, 
    `orderID` INTEGER DEFAULT 0, 
    INDEX (`dishID`), 
    PRIMARY KEY (`ID`), 
    FOREIGN KEY (dishID) REFERENCES dishes(ID),
    FOREIGN KEY (orderID) REFERENCES orders(ID)
)   ENGINE=myisam DEFAULT CHARSET=utf8;


CREATE TABLE `addonsToWishes` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `wishID` INTEGER NOT NULL DEFAULT 0, 
    `addonID` INTEGER NOT NULL DEFAULT 0, 
    PRIMARY KEY (`ID`),
    FOREIGN KEY (wishID) REFERENCES wishes(ID),
    FOREIGN KEY (addonID) REFERENCES addons(ID)
)   ENGINE=myisam DEFAULT CHARSET=utf8;

CREATE TABLE `orders` (
    `ID` INTEGER NOT NULL AUTO_INCREMENT, 
    `time` TIME, 
    `date` DATE, 
    `tableID` INTEGER NOT NULL DEFAULT 0, 
    `comments` VARCHAR(255), 
    PRIMARY KEY (`ID`), 
    FOREIGN KEY (tableID) REFERENCES tables(ID)
)   ENGINE=myisam DEFAULT CHARSET=utf8;




INSERT INTO `tables` (`name`)
VALUES  ('Przy oknie'), ('Przy barze'), ('Na środku'), ('W kącie');

INSERT INTO `dishesCategories` (`name`)
VALUES  ('Pizza'), ('Dania główne'), ('Zupy'), ('Napoje');

INSERT INTO `dishes` (name, price, descS, descL, categoryID)
VALUES  ('Margarita', 15, 'short', 'long', 1),                              #1
        ('Peperoni', 17, 'short', 'long', 1),                               #2
        ('Hawajska', 18, 'short', 'long', 1),                               #3
        ('Kurczak w cieście', 13, 'takie on jest dobry', 'araby z południa będą przychodzić i oddawać swoje żony żeby spróbować tego wspaniałego dania', 2),   #4
        ('Sznycel', 80, 'short', 'long', 2),                                #5
        ('Dewolaj', 15, 'short', 'long', 2),                                #6
        ('Zupa pomidorowa', 5, 'short', 'long', 3),                         #7
        ('Zupa ogórkowa', 5, 'short', 'long', 3),                           #8
        ('Piwo', 5, 'short', 'long', 4),                                    #9
        ('Cola', 4, 'short', 'long', 4),                                    #10
        ('Sok pomarańczowy', 3, 'short', 'long', 4);                        #11

INSERT INTO `addonsCategories` (`name`, `multiChoice`) 
VALUES  ('Sosy', true), 
        ('Rozmiary', false), 
        ('Sałatki', false), 
        ('Zapychacze', false), 
        ('Do napojów', false); 

INSERT INTO `addons` (`name`, `price`, `addonCategoryID`)
VALUES  ('Sos tatarski', 1, 1), ('Sos czosnkowy', 0, 1), ('Sos pomidorowy', 0, 1),              #1-3
        ('Mała', 0, 2), ('Średnia', 7, 2), ('Duża', 13, 2),                                     #4-6
        ('Sałatka z kapusty', 0, 3), ('Warzywa gotowane', 0, 3), ('Marchewka', 0, 3),           #7-9
        ('Ziemniaki', 0, 4), ('Pieczone ziemniaki', 0, 4), ('Frytki', 0, 4), ('Kluski', 0, 4),  #10-13
        ('Lód', 0, 5);                                                                          #14

INSERT INTO `addonsCategorisToDishes` (`dishID`, `addonCategoryID`)
VALUES  (1, 1), (1, 2),
        (2, 1), (2, 2),
        (3, 1), (3, 2),
        (4, 3), (4, 4),
        (5, 3), (5, 4),
        (6, 3), (6, 4),
        (10, 5), (10, 5),
        (11, 5), (11, 5);

INSERT INTO `wishes` (`dishID`, `amount`, `orderID`)
VALUES  (1, 1, 1), (9, 1, 1),                #1-2
        (5, 1, 2), (11, 1, 2),               #3-4
        (7, 1, 3),                           #5
        (9, 5, 4),                           #6
        (6, 1, 5), (5, 1, 5), (10, 2, 5);    #7-9

INSERT INTO `addonsToWishes` (`wishID`, `addonID`)
VALUES  (1, 2), (1, 3),
        (3, 7), (3, 10),
        (4, 14),
        (7, 9), (7, 12),
        (8, 7), (8, 10),
        (9, 14);

INSERT INTO `orders` (`time`, `date`, `tableID`, `comments`)
VALUES  ('21:37:00', '2018-07-31', 1, 'z lodem'),                                                                 #1
        ('21:38:11', '2018-07-31', 1, 'arek pedal'),                                                              #2
        ('21:41:22', '2018-07-31', 2, 'to ja szukam czy ci chlopi maja proce'),                                   #3
        ('21:49:22', '2018-07-31', 2, 'rzucam sobie na SQL'),                                                     #4
        ('21:49:33', '2018-07-31', 2, 'po co ja się w ogóle sile z tymi uwagami, nikt tego nie przeczyta :(');    #5
 
