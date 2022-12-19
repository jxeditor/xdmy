package com.xdmy.domain;

/**
 * @Author xz
 * @Date 2022/7/26 10:49
 * @Description 出货
 */
public class Shipment {
    int id;
    String odd;
    String customer;
    String product;
    String billdate;
    int amount;
    double unitprice;
    double money;
    String paystatus;
    double boardcost;
    double fireproofboardcost;
    double costmoney;
    double profit;
    String remark;

    public Shipment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOdd() {
        return odd;
    }

    public void setOdd(String odd) {
        this.odd = odd;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(double unitprice) {
        this.unitprice = unitprice;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    public double getBoardcost() {
        return boardcost;
    }

    public void setBoardcost(double boardcost) {
        this.boardcost = boardcost;
    }

    public double getFireproofboardcost() {
        return fireproofboardcost;
    }

    public void setFireproofboardcost(double fireproofboardcost) {
        this.fireproofboardcost = fireproofboardcost;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getCostmoney() {
        return costmoney;
    }

    public void setCostmoney(double costmoney) {
        this.costmoney = costmoney;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

