
package org.example;

import java.util.Date;

public class Wand {
    int id;
    Date creationDate;
    boolean inStock;
    Wood wood;
    Core core;
    Customer owner;

    public Wand(int id, Date creationDate, boolean inStock, Wood wood, Core core) {
        this.id = id;
        this.creationDate = creationDate;
        this.inStock = inStock;
        this.wood = wood;
        this.core = core;
    }

    public void sellTo(Customer customer) {
        this.owner = customer;
        this.inStock = false;
    }

    @Override
    public String toString() {
        return "Палочка{id=" + id + ", В наличии=" + inStock + ", Древесина=" + wood +
               ", Сердцевина=" + core + ", Владелец=" + (owner != null ? owner.name : "None") + "}";
    }
}
