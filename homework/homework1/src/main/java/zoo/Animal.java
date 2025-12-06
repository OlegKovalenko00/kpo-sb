package zoo;

public abstract class Animal implements Alive, Inventory {
    private static int nextInventoryNumber = 1000;
    private final int number;
    private final String name;
    private final int food;
    private boolean healthyFlag;

    protected Animal(String name, int foodKgPerDay, boolean isHealthy) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (foodKgPerDay < 0) throw new IllegalArgumentException("food can't be negative");
        this.name = name;
        this.food = foodKgPerDay;
        this.healthyFlag = isHealthy;
        this.number = nextInventoryNumber++;
    }

    @Override public int getNumber() { return number; }
    @Override public String getName() { return name; }
    @Override public int getFood() { return food; }
    public boolean isHealthyFlag() { return healthyFlag; }
    public void setHealthyFlag(boolean healthyFlag) { this.healthyFlag = healthyFlag; }
    public boolean canBeInContactZoo() { return false; }

    @Override
    public String toString() {
        return String.format("%s \"%s\" (Inv:%d) Food:%dkg/d Healthy:%s",
                this.getClass().getSimpleName(), name, number, food, healthyFlag);
    }
}
