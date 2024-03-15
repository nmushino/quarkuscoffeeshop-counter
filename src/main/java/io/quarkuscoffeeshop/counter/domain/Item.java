package io.quarkuscoffeeshop.counter.domain;

import java.math.BigDecimal;

/**
 * Models the Menu Item
 */
public enum Item {

    //Beverages
    CH99A9(BigDecimal.valueOf(755000)), CP0FB2_BLACK(BigDecimal.valueOf(150000)), CP1FC3_HOME(BigDecimal.valueOf(225000)), CK9FA3(BigDecimal.valueOf(550000)), CS92C3(BigDecimal.valueOf(565000)), CHD89SS9(BigDecimal.valueOf(100000)),

    //Food
    FAMKD8D8(BigDecimal.valueOf(255000)), FAC94S3(BigDecimal.valueOf(10500000)), FASKK9K(BigDecimal.valueOf(1780000)), FAND78K(BigDecimal.valueOf(1580000));

    private BigDecimal price;

  public BigDecimal getPrice() {
    return this.price;
  }

  private Item(BigDecimal price) {
    this.price = price;
  }

}
