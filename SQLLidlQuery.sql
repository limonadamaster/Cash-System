CREATE TABLE products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
	PLU_identificator int NOT NULL,
	sales int NOT NULL,
);



INSERT INTO products (product_name, price,PLU_identificator,sales)
VALUES ('Pink Tomato', 2.99,62,0);

INSERT INTO products (product_name, price,PLU_identificator,sales)
VALUES ('Cucumbers on KG', 3.99,202,0);

INSERT INTO products (product_name, price,PLU_identificator,sales)
VALUES ('Oranges on KG',3.19,262,0);

INSERT INTO products (product_name, price,PLU_identificator,sales)
VALUES ('Onion', 0.99,680,0);

UPDATE products SET sales = sales + 1 

select * from products

drop table  products


CREATE TABLE Transactions(
	ID INT PRIMARY KEY IDENTITY(1,1),
	amount DECIMAL(5,2),
	customer_id INT,
	FOREIGN KEY(customer_id) REFERENCES Users(user_id)
);

SELECT*FROM Users
SELECT * FROM Transactions

INSERT INTO Transactions (amount,customer_id)
VALUES  (15.00,(SELECT user_id FROM Users WITH(NOLOCK) WHERE username='12')),
		(57.00,(SELECT user_id FROM Users WITH(NOLOCK) WHERE username='20')),
		(32.00,(SELECT user_id FROM Users WITH(NOLOCK) WHERE username='13')),
		(35.00,(SELECT user_id FROM Users WITH(NOLOCK) WHERE username='20'));


CREATE TABLE Bills(
	BillID INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	BillData VARCHAR(32),
	BillMinutes VARCHAR(32),
);


DROP TABLE 
DROP TABLE Bills
SELECT * FROM BILL_PRODUCT
SELECT * FROM Bills
CREATE TABLE BILL_PRODUCT(
   BillID INT ,
   ProductID INT,
   FOREIGN KEY (BillID) REFERENCES Bills(BillID),
   FOREIGN KEY (ProductID) REFERENCES Products(product_id),
   PriceAtTimeOfSale DECIMAL(10, 2),
   Quantity INT ,
);

MERGE INTO BILL_PRODUCT AS Target
USING (
    VALUES (120,3, 2.99)
) AS Source (BillID, ProductID, PriceAtTimeOfSale)
ON Target.BillID = Source.BillID AND Target.ProductID = Source.ProductID
WHEN MATCHED THEN
    UPDATE SET Quantity = Quantity + 1
WHEN NOT MATCHED THEN
    INSERT (BillID, ProductID, PriceAtTimeOfSale, Quantity)
    VALUES (Source.BillID, Source.ProductID, Source.PriceAtTimeOfSale, 1);


DROP TABLE BILL_PRODUCT


INSERT INTO BILL_PRODUCT (BillID, ProductID, PriceAtTimeOfSale)
VALUES
    (120, 1,(SELECT price FROM Products WHERE PLU_identificator = 62));
  

	BEGIN TRANSACTION 

	ROLLBACK TRANSACTION


	SELECT B.BillID, B.BillData, B.BillMinutes,P.product_name, BP.PriceAtTimeOfSale,P.PLU_identificator,BP.Quantity
	FROM Bills B
	JOIN BILL_PRODUCT BP ON B.BillID = BP.BillID
	JOIN Products P ON BP.ProductID = P.product_id
	WHERE B.BillMinutes LIKE '%12:55:22%' AND B.BillData = '19-09-2023';


	SELECT * FROM Bills

	SELECT BP.Quantity
FROM Bills B
JOIN BILL_PRODUCT BP ON B.BillID = BP.BillID
JOIN Products P ON BP.ProductID = P.product_id
WHERE B.BillMinutes LIKE '%12:04:13 %' AND B.BillData = '19-09-2023';

	
DELETE FROM BILL_PRODUCT
WHERE BillID IN (
    SELECT B.BillID
    FROM Bills B
    WHERE B.BillData ='19-09-2023' AND B.BillMinutes LIKE '%12:04:13 %' 
) 
AND ProductID IN (
    SELECT P.product_id
    FROM Products P
    WHERE P.PLU_identificator = 680
);

SELECT * FROM Bills

BEGIN TRANSACTION; -- Start a transaction to ensure consistency

-- Decrease Quantity by 1 for rows where Quantity > 1 and for a specific product
UPDATE BILL_PRODUCT
SET Quantity = Quantity - 1
FROM BILL_PRODUCT
JOIN Bills B ON B.BillID = BILL_PRODUCT.BillID
JOIN Products P ON BILL_PRODUCT.ProductID = P.product_id
WHERE B.BillMinutes LIKE '%12:04:13%' 
  AND B.BillData ='19-09-2023'
  AND Quantity > 1
  AND P.product_id = (SELECT product_id FROM ProdUcts WHERE PLU_identificator =262); -- Specify the product_id for the specific product

-- Delete rows with Quantity = 1 for the same product and criteria
DELETE FROM BILL_PRODUCT
WHERE Quantity = 1
  AND EXISTS (
    SELECT 1
    FROM Bills B
    WHERE B.BillID = BILL_PRODUCT.BillID
    AND B.BillMinutes LIKE '%12:55:22%' 
    AND B.BillData = '19-09-2023'
  )
  AND ProductID = (SELECT product_id FROM ProdUcts WHERE PLU_identificator =262); -- Specify the product_id for the specific product

COMMIT TRANSACTION; -- Commit the transaction if everything succeeds

ROLLBACK TRANSACTION

	SELECT BP.Quantity
FROM Bills B
JOIN BILL_PRODUCT BP ON B.BillID = BP.BillID
JOIN Products P ON BP.ProductID = P.product_id
WHERE B.BillMinutes LIKE '%12:55:22%' 
AND B.BillData = '19-09-2023'
AND P.product_id = (SELECT product_id FROM Products WHERE PLU_identificator = 262)
