package controller.menu.builder.stockyard;

import controller.GameController;
import controller.command.*;
import controller.command.controller_state.OpenPopupMenuCommand;
import controller.command.farm.stockyard.BuyAnimalCommand;
import controller.command.farm.stockyard.FeedAnimalsCommand;
import controller.command.farm.stockyard.SellAnimalCommand;
import controller.menu.element.ButtonController;
import controller.menu.builder.PopupMenuControllerBuilder;
import controller.menu.builder.info.AlertMenuControllerBuilder;
import model.Position;
import model.farm.Farm;
import model.farm.building.stockyard.Stockyard;
import model.farm.building.stockyard.StockyardAnimals;
import model.farm.data.Livestock;
import model.farm.data.item.Crop;
import model.menu.Button;
import model.menu.label.Label;

import java.util.List;

public class FeedAnimalsMenuControllerBuilder extends PopupMenuControllerBuilder {
    private final Livestock livestockType;
    private final Farm farm;
    private final Stockyard stockyard;
    private final Crop crop;
    private final StockyardAnimals animals;

    public FeedAnimalsMenuControllerBuilder(GameController gameController, Farm farm, Stockyard stockyard, Crop crop) {
        super(gameController);
        this.farm = farm;
        this.stockyard = stockyard;
        this.animals = stockyard.getAnimals();
        this.livestockType = stockyard.getLivestockType();
        this.crop = crop;
    }

    @Override
    protected List<ButtonController> getButtons() {
        List<ButtonController> buttons = super.getButtons();

        PopupMenuControllerBuilder notEnoughMoneyAlert = new AlertMenuControllerBuilder(this.controller,
                "NOT ENOUGH MONEY");

        PopupMenuControllerBuilder stockyardFullAlert = new AlertMenuControllerBuilder(this.controller,
                "STOCKYARD IS FULL");

        PopupMenuControllerBuilder stockyardEmptyAlert = new AlertMenuControllerBuilder(this.controller,
                "STOCKYARD IS EMPTY");

        PopupMenuControllerBuilder notEnoughCropAlert = new AlertMenuControllerBuilder(this.controller,
                "NOT ENOUGH " + this.stockyard.getLivestockType().getFoodCrop().getName());

        Button buyAnimalButton = new Button(new Position(1, 6), "BUY");
        ConditionalCommand buyAnimalCommand = new ConditionalCommand(() -> this.animals.isFull());
        buyAnimalCommand
                .ifTrue(new OpenPopupMenuCommand(this.controller, stockyardFullAlert))
                .elseIf(() -> !this.farm.getCurrency().canBuy(this.stockyard.getLivestockType().getAnimalBuyPrice()))
                .ifTrue(new OpenPopupMenuCommand(this.controller, notEnoughMoneyAlert))
                .ifFalse(new BuyAnimalCommand(this.farm, this.stockyard.getAnimals(), this.livestockType.getAnimalBuyPrice()));

        buttons.add(new ButtonController(buyAnimalButton, buyAnimalCommand));


        Button sellAnimalButton = new Button(new Position(16, 6), "SELL");
        ConditionalCommand sellAnimalCommand = new ConditionalCommand(() -> this.animals.isEmpty());
        sellAnimalCommand
                .ifTrue(new OpenPopupMenuCommand(this.controller, stockyardEmptyAlert))
                .ifFalse(new SellAnimalCommand(this.farm, this.animals, this.livestockType.getAnimalSellPrice()));

        buttons.add(new ButtonController(sellAnimalButton, sellAnimalCommand));


        Button feedAnimalsButton = new Button(new Position(1, 12), "FEED");
        ConditionalCommand feedAnimalsCommand = new ConditionalCommand(() -> this.animals.isEmpty());
        feedAnimalsCommand
                .ifTrue(new OpenPopupMenuCommand(this.controller, stockyardEmptyAlert))
                .elseIf(() -> this.farm.getInventory().getAmount(this.stockyard.getLivestockType().getFoodCrop()) < this.stockyard.getRequiredFood())
                .ifTrue(new OpenPopupMenuCommand(this.controller, notEnoughCropAlert))
                .ifFalse(new CompoundCommand()
                        .addCommand(new FeedAnimalsCommand(this.stockyard, this.farm.getInventory()))
                        .addCommand(super.getClosePopupMenuCommand()));

        buttons.add(new ButtonController(feedAnimalsButton, feedAnimalsCommand));

        return buttons;
    }

    @Override
    protected List<Label> getLabels() {
        List<Label> labels = super.getLabels();

        labels.add(new Label(
                new Position(1, 4),
                () -> String.format("%1$sS: %2$d/%3$d",
                        this.stockyard.getLivestockType().getAnimalName(),
                        this.animals.getSize(),
                        this.stockyard.getLivestockType().getMaxNumAnimals())
        ));

        labels.add(new Label(new Position(7, 6), "COSTS"));
        labels.add(new Label(
                new Position(7, 7),
                () -> String.format(
                        "%1$5s",
                        this.stockyard.getLivestockType().getAnimalBuyPrice())
        ));

        labels.add(new Label(new Position(23, 6), "RETURN"));
        labels.add(new Label(
                new Position(23, 7),
                () -> String.format(
                        "%1$6s",
                        this.stockyard.getLivestockType().getAnimalSellPrice())
        ));

        labels.add(new Label(
                new Position(1, 10),
                () -> String.format("%1$s: %2$s",
                        this.stockyard.getLivestockType().getFoodCrop().getName(),
                        "x" + this.farm.getInventory().getAmount(this.stockyard.getLivestockType().getFoodCrop()))
        ));

        labels.add(new Label(new Position(8, 12), "NEEDS"));
        labels.add(new Label(
                new Position(8, 13),
                () -> {
                    if (this.animals.isEmpty()) {
                        return this.stockyard.getLivestockType().getAnimalName() + "S";
                    } else {
                        return this.stockyard.getLivestockType().getFoodCrop().getName()
                                + " x"
                                + this.stockyard.getLivestockType().getRequiredFood() * this.animals.getSize();
                    }
                }
        ));

        labels.add(new Label(
                new Position(1, 16),
                () -> {
                    if (this.animals.isEmpty()) {
                        return "STOCKYARD IS EMPTY";
                    } else {
                        return String.format("PRODUCES: %1$s %2$s IN %3$s",
                                this.stockyard.getLivestockType().getProducedItem().getName(),
                                "x" + this.stockyard.getLivestockType().getProducedItem().getBaseProducedAmount() * this.animals.getSize(),
                                this.stockyard.getLivestockType().getProducedItem().getProductionTime().getTimerString());
                    }
                }
        ));

        return labels;
    }

    @Override
    protected int getHeight() {
        return 18;
    }

    @Override
    protected int getWidth() {
        return 30;
    }

    @Override
    protected String getTitle() {
        return "STOCKYARD";
    }
}
