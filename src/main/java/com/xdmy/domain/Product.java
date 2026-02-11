package com.xdmy.domain;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品实体类
 */
public class Product {
    private int id;
    private String productName;
    private double suggestedPrice;
    private double costPrice;
    private int maintainMaterial;
    private String createTime;
    private String updateTime;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(double suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public int getMaintainMaterial() {
        return maintainMaterial;
    }

    public void setMaintainMaterial(int maintainMaterial) {
        this.maintainMaterial = maintainMaterial;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
