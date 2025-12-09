package org.example.DTO;

// CostDTO.java
public class CostDTO {
    private final double plannedCost;
    private final double actualCost;

    public CostDTO(double plannedCost, double actualCost) {
        this.plannedCost = plannedCost;
        this.actualCost = actualCost;
    }

    public double getPlannedCost() { return plannedCost; }
    public double getActualCost() { return actualCost; }
}
