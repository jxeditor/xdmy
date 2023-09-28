package com.xdmy.domain;

/**
 * @Author xz
 * @Date 2022/7/27 12:41
 * @Description 库存
 */
public class Stock {
    int id;
    String product;
    int unitstock;
    double unitprice;
    double purchaseprice;
    int inamount;
    int outamount;
    int stock;
    double money;
    String lastindate;
    String lastoutdate;
    String stockstatus;

    public Stock() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getUnitstock() {
        return unitstock;
    }

    public void setUnitstock(int unitstock) {
        this.unitstock = unitstock;
    }

    public double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(double unitprice) {
        this.unitprice = unitprice;
    }

    public double getPurchaseprice() {
        return purchaseprice;
    }

    public void setPurchaseprice(double purchaseprice) {
        this.purchaseprice = purchaseprice;
    }

    public int getInamount() {
        return inamount;
    }

    public void setInamount(int inamount) {
        this.inamount = inamount;
    }

    public int getOutamount() {
        return outamount;
    }

    public void setOutamount(int outamount) {
        this.outamount = outamount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getLastindate() {
        return lastindate;
    }

    public void setLastindate(String lastindate) {
        this.lastindate = lastindate;
    }

    public String getLastoutdate() {
        return lastoutdate;
    }

    public void setLastoutdate(String lastoutdate) {
        this.lastoutdate = lastoutdate;
    }

    public String getStockstatus() {
        return stockstatus;
    }

    public void setStockstatus(String stockstatus) {
        this.stockstatus = stockstatus;
    }
}
