package controller.menu.builder;

import controller.GameController;
import controller.RealTimeToInGameTimeConverter;
import controller.command.*;
import controller.menu.ButtonController;
import model.Position;
import model.farm.building.House;
import model.menu.Button;

import java.util.ArrayList;
import java.util.List;

public class StopSleepMenuControllerBuilder extends PopupMenuControllerBuilder {
    private RealTimeToInGameTimeConverter timeConverter;

    public StopSleepMenuControllerBuilder(GameController controller, RealTimeToInGameTimeConverter timeConverter) {
        super(controller);
        this.timeConverter = timeConverter;
    }

    @Override
    protected List<ButtonController> getButtons() {
        List<ButtonController> buttons = new ArrayList<>();

        Button sleepButton = new Button(new Position(1, 5), "STOP SLEEP");
        Command sleepCommand = new CompoundCommand()
                .addCommand(new SetTimeRateCommand(timeConverter, 1))
                .addCommand(this.getClosePopupMenuCommand());

        buttons.add(new ButtonController(sleepButton, sleepCommand));
        return buttons;
    }

    @Override
    protected int getHeight() {
        return 10;
    }

    @Override
    protected int getWidth() {
        return 20;
    }

    @Override
    protected String getTitle() {
        return "HOUSE";
    }
}