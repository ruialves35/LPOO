package controller.farm.building;

import controller.GameController;
import controller.GameControllerState;
import controller.command.*;
import controller.farm.FarmDemolishController;
import controller.farm.FarmWithFarmerController;
import controller.command.Command;
import controller.command.NoOperationCommand;
import controller.farm.AnimalController;
import controller.menu.builder.PopupMenuControllerBuilder;
import controller.menu.builder.stockyard.FeedAnimalsMenuControllerBuilder;
import model.InGameTime;
import model.farm.Farm;
import model.farm.Animal;
import model.farm.building.Stockyard;
import model.farm.building.stockyard_state.NotProducing;

public class StockyardController extends BuildingController<Stockyard> {
    private final GameController controller;
    private final AnimalController animalController;
    private Farm farm;

    public StockyardController(GameController controller, Farm farm) {
        this.controller = controller;
        this.animalController = new AnimalController();
    }

    public void resetLastMovement() {
        animalController.reset();
    }

    @Override
    public Command getInteractionCommand(Stockyard stockyard) {
        PopupMenuControllerBuilder menuControllerBuilder;
        menuControllerBuilder = new FeedAnimalsMenuControllerBuilder(this.controller, stockyard, stockyard.getLivestockType().getFoodCrop());

        if (stockyard.getState() instanceof NotProducing) {
            menuControllerBuilder = new FeedAnimalsMenuControllerBuilder(this.controller, stockyard, stockyard.getLivestockType().getFoodCrop());
        }
        // TODO
        System.out.println("Stockyard interaction not implemented yet");
        return new OpenPopupMenuCommand(this.controller, menuControllerBuilder);
    }

    @Override
    public Command getDemolishCommand(Stockyard stockyard) {
        GameControllerState gameControllerState = this.controller.getGameControllerState();
        if (gameControllerState instanceof FarmDemolishController) {
            gameControllerState = new FarmWithFarmerController((FarmDemolishController) gameControllerState);
        }

        return new CompoundCommand()
                .addCommand(() -> this.farm.getBuildings().removeStockyard(stockyard))
                .addCommand(new SetControllerStateCommand(this.controller, gameControllerState));
    }

    public void reactTimePassed(Stockyard stockyard, InGameTime elapsedTime) {
        animalController.reactTimePassed(stockyard, elapsedTime);

    }
}
