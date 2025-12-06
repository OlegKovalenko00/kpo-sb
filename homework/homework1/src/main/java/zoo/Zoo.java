package zoo;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Zoo {
    private final VeterinaryClinic clinic;
    private final List<Inventory> inventory = new ArrayList<>();

    @Inject
    public Zoo(VeterinaryClinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("clinic is null");
        this.clinic = clinic;
    }

    public boolean tryAdmitAnimal(Animal animal) {
        if (animal == null) throw new IllegalArgumentException("animal is null");
        if (!clinic.isHealthy(animal)) return false;
        inventory.add(animal);
        return true;
    }

    public void addThing(Inventory thing) {
        if (thing == null) throw new IllegalArgumentException("thing is null");
        inventory.add(thing);
    }

    public int totalFoodPerDayKg() {
        return inventory.stream()
                .filter(item -> item instanceof Alive)
                .mapToInt(item -> ((Alive) item).getFood())
                .sum();
    }

    public List<Animal> getContactZooCandidates() {
        return inventory.stream()
                .filter(a -> a instanceof Animal)
                .map(a -> (Animal) a)
                .filter(Animal::canBeInContactZoo)
                .collect(Collectors.toList());
    }

    public List<Animal> getAnimals() {
        return inventory.stream()
                .filter(a -> a instanceof Animal)
                .map(a -> (Animal) a)
                .collect(Collectors.toList());
    }

    public List<Inventory> getInventoryItems() {
        return Collections.unmodifiableList(inventory);
    }

    public Optional<Inventory> findInventoryByNumber(int number) {
        return inventory.stream().filter(i -> i.getNumber() == number).findFirst();
    }

    public boolean removeAnimalByNumber(int number) {
        return inventory.removeIf(item -> (item instanceof Animal) && ((Animal) item).getNumber() == number);
    }

    public boolean removeThingByNumber(int number) {
        return inventory.removeIf(item -> (item instanceof Thing) && item.getNumber() == number);
    }
}
