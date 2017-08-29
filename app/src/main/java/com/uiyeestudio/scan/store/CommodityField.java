package com.uiyeestudio.scan.store;

/**
 * Created by Michael on 2017/8/28.
 */

public enum CommodityField {

    ID("id"),
    BARCODE("barcode"),
    TITLE("title"),
    PRICE("price"),
    SHOP_NAME("shopName"),
    PIC("pic"),
    ITEM_ID("itemId");

    private String field;
    private CommodityField(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }

}
