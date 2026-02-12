package com.xdmy.domain;

/**
 * @Author xz
 * @Date 2026/2/10 10:00
 * @Description 原材料库存
 */
public class MaterialStock {
    int id;
    String materialName;
    int unitstock;
    String company_name;

    public MaterialStock() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getUnitstock() {
        return unitstock;
    }

    public void setUnitstock(int unitstock) {
        this.unitstock = unitstock;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
}
