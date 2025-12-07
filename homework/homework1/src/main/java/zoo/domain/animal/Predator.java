package zoo.domain.animal;

public abstract class Predator extends Animal {

    protected Predator(String name, int number, int foodPerDay) {
        super(name, number, foodPerDay);
    }

    @Override
    public boolean isGoodForContactZoo() {
        return false;
    }
}