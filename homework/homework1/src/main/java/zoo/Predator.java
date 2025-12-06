package zoo;

public abstract class Predator extends Animal {
    protected Predator(String name, int foodKgPerDay, boolean isHealthy) {
        super(name, foodKgPerDay, isHealthy);
    }
}
