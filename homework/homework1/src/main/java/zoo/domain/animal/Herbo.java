package zoo.domain.animal;

public abstract class Herbo extends Animal {

    private final int kindness;

    protected Herbo(String name, int number, int foodPerDay, int kindness) {
        super(name, number, foodPerDay);
        this.kindness = kindness;
    }

    public int getKindness() {
        return kindness;
    }

    @Override
    public boolean isGoodForContactZoo() {
        return kindness > 5;
    }
}