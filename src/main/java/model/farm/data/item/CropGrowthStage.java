package model.farm.data.item;

import gui.Color;
import model.InGameTime;

import java.io.Serializable;

public class CropGrowthStage implements Comparable<CropGrowthStage>, Serializable {
    private final InGameTime stageStartTime;
    private final char stageChar;
    private final Color stageColor;

    public CropGrowthStage(InGameTime stageStartTime, char stageChar, Color stageColor) {
        this.stageStartTime = stageStartTime;
        this.stageChar = stageChar;
        this.stageColor = stageColor;
    }

    public CropGrowthStage() {
        this(new InGameTime(0), ' ', Color.BLACK);
    }

    public static CropGrowthStage parseGrowthStage(String s) {
        String tokens[] = s.split(" ");
        InGameTime stageTime = InGameTime.parseTimerString(tokens[0]);
        char stageChar = tokens[1].charAt(0);
        Color stageColor = new Color(tokens[2]);
        return new CropGrowthStage(stageTime, stageChar, stageColor);
    }

    public InGameTime getStageStartTime() {
        return this.stageStartTime;
    }

    public char getStageChar() {
        return this.stageChar;
    }

    public Color getStageColor() {
        return this.stageColor;
    }

    @Override
    public int compareTo(CropGrowthStage growthStage) {
        return this.stageStartTime.compareTo(growthStage.stageStartTime);
    }
}