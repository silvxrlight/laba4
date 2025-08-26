
package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Supply {
    int id;
    Date date;
    String supplier;
    List<SupplyComponent> components = new ArrayList<>();

    public Supply(int id, Date date, String supplier) {
        this.id = id;
        this.date = date;
        this.supplier = supplier;
    }

    public void addComponent(SupplyComponent component) {
        components.add(component);
    }

    @Override
    public String toString() {
        return "Поставка №" + id + " от " + date + " (" + supplier + ")";
    }
}
