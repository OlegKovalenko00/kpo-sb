package zoo.domain.animal;

import zoo.domain.common.Alive;
import zoo.domain.common.Inventory;

public abstract class Animal implements Alive, Inventory {

    private final String name;
    private final int number;
    private final int foodPerDay;

    protected Animal(String name, int number, int foodPerDay) {
        this.name = name;
        this.number = number;
        this.foodPerDay = foodPerDay;
    }

    @Override
    public int getFoodPerDay() {
        return foodPerDay;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isGoodForContactZoo() {
        return false;
    }
}

