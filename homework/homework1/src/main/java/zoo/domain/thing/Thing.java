package zoo.domain.thing;

import zoo.domain.common.Inventory;

public abstract class Thing implements Inventory {

    private final String name;
    private final int number;

    protected Thing(String name, int number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }
}
