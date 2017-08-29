package com.uiyeestudio.scan.store.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Michael on 2017/8/25.
 */

@Entity
public class History {
    @Id
    private Long id;

    @Index
    private String barcode;

    private String title;

    private double price;

    private String shopName;

    private String pic;

    private String itemId;

    @Generated(hash = 837997317)
    public History(Long id, String barcode, String title, double price,
            String shopName, String pic, String itemId) {
        this.id = id;
        this.barcode = barcode;
        this.title = title;
        this.price = price;
        this.shopName = shopName;
        this.pic = pic;
        this.itemId = itemId;
    }

    @Generated(hash = 869423138)
    public History() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
