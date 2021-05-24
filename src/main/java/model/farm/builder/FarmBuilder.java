package model.farm.builder;

import model.InGameTime;
import model.farm.*;
import model.farm.building.BuildingSet;
import model.farm.building.House;
import model.farm.building.Market;
import model.farm.building.Warehouse;
import model.farm.building.CropField;
import model.farm.item.Crop;

import java.util.List;
import java.util.Set;

public abstract class FarmBuilder {

    public Farm buildFarm() {
        BuildingSet buildingSet = new BuildingSet();
        buildingSet.setHouse(this.getHouse());
        buildingSet.setMarket(this.getMarket());
        buildingSet.setWarehouse(this.getWarehouse());
        for (CropField cropField: this.getCropFields()) {
            buildingSet.addCropField(cropField);
        }

        Farm farm = new Farm(this.getWidth(), this.getHeight(), buildingSet);
        farm.setFarmer(this.getFarmer());
        farm.setTime(this.getTime());
        farm.setWeatherStates(this.getWeatherStates());
        farm.setWeather(farm.getWeatherStates().get(0)); // TODO this will not be like this
        farm.setCrops(this.getCrops());
        farm.setInventory(this.getInventory());
        farm.setCurrency(this.getCurrency());
        return farm;
    }

    protected abstract Currency getCurrency();

    protected abstract Inventory getInventory();

    protected abstract Warehouse getWarehouse();

    protected abstract Market getMarket();

    protected abstract InGameTime getTime();

    protected abstract List<Weather> getWeatherStates();

    protected abstract Farmer getFarmer();

    protected abstract House getHouse();

    protected abstract Set<CropField> getCropFields();

    protected abstract List<Crop> getCrops();

    protected abstract int getWidth();

    protected abstract int getHeight();
}
