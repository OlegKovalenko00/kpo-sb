package zoo.factory;

import zoo.domain.animal.*;

public class SimpleAnimalFactory implements AnimalFactory {

    @Override
    public Animal createAnimal(AnimalType type,
                               String name,
                               int foodPerDay,
                               int kindness,
                               int inventoryNumber) {

        return switch (type) {
            case MONKEY -> new Monkey(name, inventoryNumber, foodPerDay, kindness);
            case RABBIT -> new Rabbit(name, inventoryNumber, foodPerDay, kindness);
            case TIGER  -> new Tiger(name, inventoryNumber, foodPerDay);
            case WOLF   -> new Wolf(name, inventoryNumber, foodPerDay);
        };
    }
}
