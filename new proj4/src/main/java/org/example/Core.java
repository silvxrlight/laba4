
package org.example;

public class Core {
    int id;
    String type;
    String magicalProperties;

    public Core(int id, String type, String magicalProperties) {
        this.id = id;
        this.type = type;
        this.magicalProperties = magicalProperties;
    }

    @Override
    public String toString() {
        return "Сердцевина{ID=" + id + ", тип='" + type + "', магические свойства='" + magicalProperties + "'}";
    }
}
