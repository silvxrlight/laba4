
package org.example;

public class SupplyComponent {
    public enum ComponentType { WOOD, CORE }

    int id;
    ComponentType type;
    String componentName;
    int quantity;

    public SupplyComponent(int id, ComponentType type, String componentName, int quantity) {
        this.id = id;
        this.type = type;
        this.componentName = componentName;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return type + ": " + componentName + " x" + quantity;
    }
}
