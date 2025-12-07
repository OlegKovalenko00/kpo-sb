package zoo.factory;

import zoo.domain.animal.Animal;
import zoo.domain.animal.AnimalType;

public interface AnimalFactory {
    Animal createAnimal(AnimalType type,
                        String name,
                        int foodPerDay,
                        int kindness,
                        int inventoryNumber);
}
