package com.inspirado.kuber;

import java.io.Serializable;


public class Lender extends  User {
    boolean selected;
    int borender;
    double distance;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getBorender() {
        return borender;
    }

    public void setBorender(int borender) {
        this.borender = borender;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
