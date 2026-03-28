package it.polimi.ingsw.am55;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Double> elements = new ArrayList<>();
    public void add(String pc, double v) throws IllegalArgumentException {
        if (v<0) {
            throw new IllegalArgumentException("v cannot be negative");
        }
        elements.add(v);



    }

    public double getTotal() {
        double total = 0.0;
        for (Double element : elements) {
            total += element;
        }
        if (elements.size() > 3) {
            total=total*0.9;
        }
        return total;

    }
}
