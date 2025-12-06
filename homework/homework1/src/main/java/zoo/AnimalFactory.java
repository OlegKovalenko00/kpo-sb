package zoo;

public final class AnimalFactory {
    private AnimalFactory() {}
    public static Animal create(String type, String name, int foodKgPerDay, boolean isHealthy, Integer kindness) {
        if (type == null) throw new IllegalArgumentException("type is null");
        switch (type.trim().toLowerCase()) {
            case "rabbit": return new Rabbit(name, foodKgPerDay, isHealthy, kindness != null ? kindness : 0);
            case "monkey": return new Monkey(name, foodKgPerDay, isHealthy, kindness != null ? kindness : 0);
            case "tiger": return new Tiger(name, foodKgPerDay, isHealthy);
            case "wolf": return new Wolf(name, foodKgPerDay, isHealthy);
            default: throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}
