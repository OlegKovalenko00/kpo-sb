package zoo;

public abstract class Herbivore extends Animal {
    private final int kindness;
    protected Herbivore(String name, int foodKgPerDay, boolean isHealthy, int kindness) {
        super(name, foodKgPerDay, isHealthy);
        this.kindness = Math.max(0, Math.min(10, kindness));
    }
    public int getKindness() { return kindness; }
    @Override public boolean canBeInContactZoo() { return kindness > 5; }
}
