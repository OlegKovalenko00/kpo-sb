package zoo.domain.zoo;

import zoo.domain.animal.Animal;
import zoo.domain.animal.AnimalType;
import zoo.domain.common.Inventory;
import zoo.domain.thing.Thing;
import zoo.domain.thing.Table;
import zoo.domain.thing.Computer;
import zoo.domain.thing.ThingType;
import zoo.factory.AnimalFactory;
import zoo.vet.VeterinaryClinic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Zoo {

    private final VeterinaryClinic clinic;
    private final AnimalFactory animalFactory;

    private final List<Animal> animals = new ArrayList<>();
    private final List<Inventory> inventory = new ArrayList<>();

    private int nextInventoryId = 1;

    public Zoo(VeterinaryClinic clinic, AnimalFactory animalFactory) {
        this.clinic = clinic;
        this.animalFactory = animalFactory;
    }

    public Animal addAnimal(AnimalType type, String name, int foodPerDay, int kindness) {
        int number = nextInventoryId++;
        Animal animal = animalFactory.createAnimal(type, name, foodPerDay, kindness, number);

        if (!clinic.isHealthy(animal)) {
            return null;
        }

        animals.add(animal);
        inventory.add(animal);
        return animal;
    }

    public Thing addThing(ThingType type, String name) {
        int number = nextInventoryId++;
        Thing thing = switch (type) {
            case TABLE    -> new Table(name, number);
            case COMPUTER -> new Computer(name, number);
        };
        inventory.add(thing);
        return thing;
    }

    public List<Animal> getAllAnimals() {
        return Collections.unmodifiableList(animals);
    }

    public List<Inventory> getAllInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public int getTotalDailyFood() {
        return animals.stream()
                .mapToInt(Animal::getFoodPerDay)
                .sum();
    }

    public List<Animal> getContactZooAnimals() {
        return animals.stream()
                .filter(Animal::isGoodForContactZoo)
                .toList();
    }

    public boolean removeByInventoryNumber(int number) {
        boolean removedAnimal = animals.removeIf(a -> a.getNumber() == number);
        boolean removedInventory = inventory.removeIf(i -> i.getNumber() == number);
        return removedAnimal || removedInventory;
    }
}
