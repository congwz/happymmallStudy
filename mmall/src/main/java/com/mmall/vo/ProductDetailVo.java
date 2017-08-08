package com.mmall.vo;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/7/3 0003.
 */
public class ProductDetailVo {

    private Integer id;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    private Integer categoryId;
    private String name;
    private String subtitle;
    private String subImages;
    private String mainImage;

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    private String detail;  //富文本
    private BigDecimal price;  //BigDecimal可以避免计算时丢失精度
    private Integer stock;  //库存
    private Integer status;  //状态
    private String createTime;
    private String updateTime;

    private String imageHost;  //图片服务器的前缀
    private Integer parentCategotyId;  //父分类

    //填充ge、tset方法
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getParentCategotyId() {
        return parentCategotyId;
    }

    public void setParentCategotyId(Integer parentCategotyId) {
        this.parentCategotyId = parentCategotyId;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
