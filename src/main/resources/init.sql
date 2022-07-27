-- 库存计算SQL逻辑
SELECT *
FROM (
         SELECT t1.id
              ,COALESCE(t1.product,t2.product) product
              ,t1.unitstock
              ,t1.unitprice
              ,t2.inamount
              ,t2.outamount
              ,COALESCE(t1.unitstock,0) + t2.amount stock
              ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + t2.amount)*t1.unitprice,null) money
              ,t2.lastindate
              ,t2.lastoutdate
              ,if(t1.unitprice is not null,'1','0') stockstatus
         FROM (
                  SELECT *
                  FROM stock
              ) t1
                  LEFT JOIN (
             SELECT product
                  ,sum(if(source='入货',amount,0)) inamount
                  ,sum(if(source='出货',amount,0)) outamount
                  ,max(if(source='入货',billdate,null)) lastindate
                  ,max(if(source='出货',billdate,null)) lastoutdate
                  ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount
             FROM (
                      SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source
                      FROM shipment
                      GROUP BY product
                      UNION ALL
                      SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source
                      FROM incoming
                      GROUP BY product
                  ) t1
             GROUP BY product
         ) t2
                            ON t1.product = t2.product
         UNION ALL
         SELECT t1.id
              ,COALESCE(t1.product,t2.product) product
              ,t1.unitstock
              ,t1.unitprice
              ,t2.inamount
              ,t2.outamount
              ,COALESCE(t1.unitstock,0) + t2.amount stock
              ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + t2.amount)*t1.unitprice,null) money
              ,t2.lastindate
              ,t2.lastoutdate
              ,if(t1.unitprice is not null,'1','0') stockstatus
         FROM (
                  SELECT *
                  FROM stock
              ) t1
                  RIGHT JOIN (
             SELECT product
                  ,sum(if(source='入货',amount,0)) inamount
                  ,sum(if(source='出货',amount,0)) outamount
                  ,max(if(source='入货',billdate,null)) lastindate
                  ,max(if(source='出货',billdate,null)) lastoutdate
                  ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount
             FROM (
                      SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source
                      FROM shipment
                      GROUP BY product
                      UNION ALL
                      SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source
                      FROM incoming
                      GROUP BY product
                  ) t1
             GROUP BY product
         ) t2
                             ON t1.product = t2.product
         WHERE t1.product is null
     ) t1
ORDER BY product