package controller.farm.building;

import controller.GameController;
import controller.GameControllerState;
import controller.command.*;
import controller.farm.FarmDemolishController;
import controller.farm.FarmWithFarmerController;
import controller.menu.builder.AlertMenuControllerBuilder;
import controller.menu.builder.PopupMenuControllerBuilder;
import model.InGameTime;
import model.farm.Farm;
import model.farm.Weather;
import model.farm.building.CropField;
import model.farm.building.crop_field_state.NotPlanted;
import model.farm.building.crop_field_state.Planted;
import model.farm.building.crop_field_state.ReadyToHarvest;
import controller.menu.builder.crop_field.CropFieldGrowingMenuControllerBuilder;
import controller.menu.builder.crop_field.HarvestMenuControllerBuilder;
import controller.menu.builder.crop_field.PlantCropMenuControllerBuilder;

public class CropFieldController extends BuildingController<CropField> {
    private final GameController controller;
    private Farm farm;

    public CropFieldController(GameController controller, Farm farm) {
        this.controller = controller;
        this.farm = farm;
    }

    @Override
    public Command getInteractionCommand(CropField cropField) {
        PopupMenuControllerBuilder menuControllerBuilder;

        if (cropField.getState() instanceof NotPlanted) {
            menuControllerBuilder = new PlantCropMenuControllerBuilder(this.controller, this.farm, cropField);
        } else if (cropField.getState() instanceof Planted) {
            menuControllerBuilder = new CropFieldGrowingMenuControllerBuilder(this.controller, this.farm, cropField);
        } else if (cropField.getState() instanceof ReadyToHarvest) {
            menuControllerBuilder = new HarvestMenuControllerBuilder(this.controller, farm.getInventory(), cropField);
        } else {
            // This should never happen
            throw new RuntimeException(
                    "LOGIC ERROR: Unhandled CropFieldState: " + cropField.getState().getClass().toString());
        }

        return new OpenPopupMenuCommand(this.controller, menuControllerBuilder);
    }

    @Override
    public Command getDemolishCommand(CropField cropField) {
        if (this.farm.getBuildings().getCropFields().size() == 1) {
            return new OpenPopupMenuCommand(this.controller, new AlertMenuControllerBuilder(this.controller,
                    "CANNOT DEMOLISH LAST\nCROPFIELD: MUST HAVE\nAT LEAST ONE"));
        } else {
            GameControllerState gameControllerState = this.controller.getGameControllerState();
            if (gameControllerState instanceof FarmDemolishController) {
                gameControllerState = new FarmWithFarmerController((FarmDemolishController) gameControllerState);
            }

            return new CompoundCommand()
                    .addCommand(() -> this.farm.getBuildings().removeCropField(cropField))
                    .addCommand(new SetControllerStateCommand(this.controller, gameControllerState));
        }
    }

    public void reactTimePassed(CropField cropField, InGameTime elapsedTime) {
        cropField.setRemainingTime(cropField.getRemainingTime().subtract(elapsedTime));
        cropField.changeHarvestAmount(calculateWeatherEffect(cropField, elapsedTime, this.farm.getWeather()));
    }

    private double calculateWeatherEffect(CropField cropField, InGameTime elapsedTime, Weather weather) {
        double weatherEffect = elapsedTime.getMinute() * weather.getWeatherEffect();

        // when readyToHarvest only bad effects take place
        // what would be good effect while crop was in growth stage
        // when ready to harvest it will rot it
        if (cropField.getState() instanceof ReadyToHarvest) {
            if (weatherEffect > 0)
                weatherEffect *= -1;
        }

        return weatherEffect;
    }

}
