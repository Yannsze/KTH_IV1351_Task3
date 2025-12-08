package org.example.model;

import org.example.DTO.CostDTO;

public class CostImplementation implements CostDTO {
    private final double planned;
    private final double actual;

    public CostImplementation(double planned, double actual) {
        this.planned = planned;
        this.actual = actual;
    }

    public double getPlannedCost() {
        return planned;
    }
    public double getActualCost() {
        return actual;
    }
}
