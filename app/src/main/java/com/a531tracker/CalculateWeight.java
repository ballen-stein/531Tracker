package com.a531tracker;

import java.math.BigDecimal;
import java.util.ArrayList;

class CalculateWeight {

    private ArrayList<Double> weightArray = new ArrayList<>();

    public CalculateWeight() {
        weightArray.add(45.0);
    }

    public String setAsPounds(int trainingValue, float liftPercent){
        return String.valueOf((int) ( 5 * ( Math.ceil( ( trainingValue * liftPercent ) / 5 ) ) ) );
    }

    public String setAsKilograms(int trainingValue, float liftPercent){
        return String.valueOf(new BigDecimal((trainingValue * liftPercent) / 2.20462).setScale(1, BigDecimal.ROUND_HALF_UP));
    }

    public int weightBreakdown(double weight, boolean firstRun) {
        double fixedWeight;
        if(firstRun) {
            if(weight < 45){
                return 0;
            } else {
                fixedWeight = (weight - 45) / 2;
            }
        } else {
            fixedWeight = weight;
        }

        if(fixedWeight - 45 >= 0) {
            if(checkIfZero(fixedWeight, 45))
                setWeightArray(45);
            else {
                setWeightArray(45);
                weightBreakdown(fixedWeight - 45, false);
            }
        } else if(fixedWeight - 35 >= 0) {
            if(checkIfZero(fixedWeight, 35))
                setWeightArray(35);
            else {
                setWeightArray(35);
                weightBreakdown(fixedWeight - 35, false);
            }
        } else if (fixedWeight - 25 >= 0) {
            if(checkIfZero(fixedWeight, 25)){
                setWeightArray(25);
            } else {
                setWeightArray(25);
                weightBreakdown(fixedWeight - 25, false);
            }
        } else if (fixedWeight - 10 >= 0) {
            if(checkIfZero(fixedWeight, 10))
                setWeightArray(10);
            else {
                setWeightArray(10);
                weightBreakdown(fixedWeight - 10, false);
            }
        } else if (fixedWeight - 5 >= 0) {
            if(checkIfZero(fixedWeight, 5))
                setWeightArray(5);
            else {
                setWeightArray(5);
                weightBreakdown(fixedWeight - 5, false);
            }
        } else if (fixedWeight - 2.5 >= 0){
            setWeightArray(1);
        } else {
            //setWeightArray(45);
        }

        return 1;
    }

    private boolean checkIfZero(double currVal, double returnVal){
        return ((currVal - returnVal) == 0);
    }

    private void setWeightArray(int weight){
        switch (weight){
            case 45:
                weightArray.add(45.0);
                break;
            case 35:
                weightArray.add(35.0);
                break;
            case 25:
                weightArray.add(25.0);
                break;
            case 10:
                weightArray.add(10.0);
                break;
            case 5:
                weightArray.add(5.0);
                break;
            default:
                weightArray.add(2.5);
                break;
        }
    }

    public ArrayList getWeightArray(){
        return weightArray;
    }

    public void resetWeightArray(){
        weightArray = new ArrayList<>();
        weightArray.add(45.0);
    }
}
