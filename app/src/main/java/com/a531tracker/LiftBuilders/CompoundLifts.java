package com.a531tracker.LiftBuilders;

public class CompoundLifts {
    private String compound_movement;
    private int training_max;
    private int eight_five_reps = 5;
    private int ninety_reps = 3;
    private int ninety_five_reps = 1;
    private Float big_but_boring_weight;

    public String getCompound_movement() {
        return compound_movement;
    }

    public void setCompound_movement(String compound_movement) {
        this.compound_movement = compound_movement;
    }

    public int getTraining_max() {
        return training_max;
    }

    public void setTraining_max(int training_max){
        this.training_max = training_max;
    }

    public Integer getEight_five_reps() {
        return eight_five_reps;
    }

    public void setEight_five_reps(Integer eight_five_reps) {
        this.eight_five_reps = eight_five_reps;
    }

    public Integer getNinety_reps() {
        return ninety_reps;
    }

    public void setNinety_reps(Integer ninety_reps) {
        this.ninety_reps = ninety_reps;
    }

    public Integer getNinety_five_reps() {
        return ninety_five_reps;
    }

    public void setNinety_five_reps(Integer ninety_five_reps) {
        this.ninety_five_reps = ninety_five_reps;
    }

    public void setBig_but_boring_weight(Float big_but_boring_weight) {
        this.big_but_boring_weight = big_but_boring_weight;
    }

    public Float getBig_but_boring_weight() {
        return big_but_boring_weight;
    }
}
