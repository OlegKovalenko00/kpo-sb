package zoo;

public class Thing implements Inventory {
    private static int nextThingNumber = 1;
    private final int number;
    private final String name;
    public Thing(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
        this.number = nextThingNumber++;
    }
    @Override public int getNumber() { return number; }
    @Override public String getName() { return name; }
    @Override public String toString() { return String.format("%s (Inv:%d)", name, number); }
}
