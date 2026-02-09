-- 初始化stock表数据脚本
USE xdmy;

-- 更新现有stock记录的inamount、outamount、lastindate、lastoutdate字段
UPDATE stock s
SET 
    inamount = COALESCE((SELECT SUM(amount) FROM incoming WHERE product = s.product AND is_delete = 0), 0),
    outamount = COALESCE((SELECT SUM(amount) FROM shipment WHERE product = s.product AND is_delete = 0), 0),
    lastindate = COALESCE((SELECT MAX(billdate) FROM incoming WHERE product = s.product AND is_delete = 0), ''),
    lastoutdate = COALESCE((SELECT MAX(billdate) FROM shipment WHERE product = s.product AND is_delete = 0), ''),
    purchaseprice = COALESCE((SELECT unitprice FROM incoming WHERE product = s.product AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), s.unitprice);

-- 插入新的stock记录，对于那些在shipment或incoming中存在但在stock中不存在的产品
INSERT INTO stock(product, unitstock, unitprice, purchaseprice, inamount, outamount, lastindate, lastoutdate, stockstatus)
SELECT 
    t1.product,
    0 AS unitstock,
    COALESCE((SELECT unitprice FROM incoming WHERE product = t1.product AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), 0) AS unitprice,
    COALESCE((SELECT unitprice FROM incoming WHERE product = t1.product AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), 0) AS purchaseprice,
    COALESCE((SELECT SUM(amount) FROM incoming WHERE product = t1.product AND is_delete = 0), 0) AS inamount,
    COALESCE((SELECT SUM(amount) FROM shipment WHERE product = t1.product AND is_delete = 0), 0) AS outamount,
    COALESCE((SELECT MAX(billdate) FROM incoming WHERE product = t1.product AND is_delete = 0), '') AS lastindate,
    COALESCE((SELECT MAX(billdate) FROM shipment WHERE product = t1.product AND is_delete = 0), '') AS lastoutdate,
    '1' AS stockstatus
FROM (
    SELECT product FROM shipment WHERE is_delete = 0 GROUP BY product
    UNION
    SELECT product FROM incoming WHERE is_delete = 0 GROUP BY product
) t1
WHERE t1.product NOT IN (SELECT product FROM stock);

-- 计算并更新unitstock字段，确保库存为正
UPDATE stock
SET unitstock = ABS(inamount - outamount)
WHERE unitstock + (inamount - outamount) < 0;
