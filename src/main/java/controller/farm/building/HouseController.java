package controller.farm.building;

import controller.GameController;
import controller.RealTimeToInGameTimeConverter;
import controller.command.Command;
import controller.command.SetControllerStateCommand;
import controller.menu.builder.SleepMenuControllerBuilder;
import controller.menu.builder.MenuControllerBuilder;
import model.Position;
import model.farm.building.House;

public class HouseController extends BuildingController<House> {
    private final GameController controller;
    private RealTimeToInGameTimeConverter timeConverter;

    public HouseController(GameController controller, RealTimeToInGameTimeConverter timeConverter) {
        this.controller = controller;
        this.timeConverter = timeConverter;
    }

    @Override
    public Command getInteractionCommand(House house) {
        MenuControllerBuilder menuControllerBuilder;
        menuControllerBuilder = new SleepMenuControllerBuilder(this.controller, timeConverter, house);

        return new SetControllerStateCommand(this.controller, menuControllerBuilder.buildMenu(new Position(1,1)));
    }
}
