package controller.farm;

import controller.GameController;
import model.InGameTime;
import model.farm.Farm;
import model.farm.building.crop_field.CropField;
import model.farm.building.crop_field.crop.Crop;
import model.farm.building.crop_field.crop.GrowthStage;
import model.farm.building.crop_field.state.Planted;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class FarmControllerTest {
    private Farm farm;
    private CropField cropField;
    private FarmController controller;
    private GameController gameController;

    @BeforeEach
    public void setUp() {
        farm = new Farm(6, 8);
        cropField = Mockito.mock(CropField.class);
        Mockito.when(cropField.getRemainingTime()).thenReturn(new InGameTime(0));
        farm.getBuildings().addCropField(cropField);
        gameController = Mockito.mock(GameController.class);
        controller = new FarmController(farm, gameController, 1);
    }

    @Test
    public void reactTimePassed() {
        farm.getTime().set(new InGameTime(0, 0, 0));
        controller.reactTimePassed(67000);
        Assertions.assertEquals(new InGameTime(0, 1, 7), farm.getTime());
        controller.reactTimePassed(1000);
        Assertions.assertEquals(new InGameTime(0, 1, 8), farm.getTime());
        controller.reactTimePassed(1700);
        Assertions.assertEquals(new InGameTime(0, 1, 9), farm.getTime());
        controller.reactTimePassed(400);
        Assertions.assertEquals(new InGameTime(0, 1, 10), farm.getTime());
    }

    @Test
    public void reactTimePassedNotifyCropField() {
        controller.reactTimePassed(67000);
        Mockito.verify(cropField, Mockito.times(1)).setRemainingTime(Mockito.any(InGameTime.class));
    }
}