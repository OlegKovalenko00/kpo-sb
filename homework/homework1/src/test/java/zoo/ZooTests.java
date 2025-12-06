package zoo;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ZooTests {

    private VeterinaryClinic clinicMock;
    private Zoo zoo;

    @BeforeEach
    public void setUp() {
        clinicMock = mock(VeterinaryClinic.class);
        zoo = new Zoo(clinicMock);
    }
    @Test
    public void addingThingAddsToInventory() {
        Thing t = new Thing("TestTable");
        zoo.addThing(t);

        assertTrue(zoo.getInventoryItems().stream().anyMatch(i -> i.getNumber() == t.getNumber()));
        assertTrue(zoo.findInventoryByNumber(t.getNumber()).isPresent());
        assertEquals("TestTable", zoo.findInventoryByNumber(t.getNumber()).get().getName());
    }

    @Test
    public void shouldAcceptHealthyAnimal() {
        when(clinicMock.isHealthy(any(Animal.class))).thenReturn(true);
        Animal rabbit = new Rabbit("r", 1, true, 7);

        boolean accepted = zoo.tryAdmitAnimal(rabbit);

        assertTrue(accepted);
        assertEquals(1, zoo.getAnimals().size());
    }

    @Test
    public void shouldRejectUnhealthyAnimal() {
        when(clinicMock.isHealthy(any(Animal.class))).thenReturn(false);
        Animal tiger = new Tiger("t", 5, false);

        boolean accepted = zoo.tryAdmitAnimal(tiger);

        assertFalse(accepted);
        assertEquals(0, zoo.getAnimals().size());
    }

    @Test
    public void totalFoodIsSumOfAliveItemsAndIgnoresThings() {
        when(clinicMock.isHealthy(any(Animal.class))).thenReturn(true);
        zoo.tryAdmitAnimal(new Rabbit("r1", 1, true, 6));
        zoo.tryAdmitAnimal(new Rabbit("r2", 2, true, 3));
        zoo.addThing(new Thing("Table1"));

        assertEquals(3, zoo.totalFoodPerDayKg());
    }

    @Test
    public void removeAnimalByNumber_RemovesOnlyAnimal() {
        when(clinicMock.isHealthy(any(Animal.class))).thenReturn(true);
        Rabbit r = new Rabbit("r", 1, true, 6);
        zoo.tryAdmitAnimal(r);
        Thing t = new Thing("Desk");
        zoo.addThing(t);

        int rNum = r.getNumber();
        int tNum = t.getNumber();

        boolean removedAnimal = zoo.removeAnimalByNumber(rNum);
        assertTrue(removedAnimal);
        assertFalse(zoo.findInventoryByNumber(rNum).isPresent());
        assertTrue(zoo.findInventoryByNumber(tNum).isPresent());
    }

    @Test
    public void removeThingByNumber_RemovesOnlyThing() {
        when(clinicMock.isHealthy(any(Animal.class))).thenReturn(true);
        Rabbit r = new Rabbit("r2", 1, true, 6);
        zoo.tryAdmitAnimal(r);
        Thing t = new Thing("Chair");
        zoo.addThing(t);

        int rNum = r.getNumber();
        int tNum = t.getNumber();

        boolean removedThing = zoo.removeThingByNumber(tNum);
        assertTrue(removedThing);
        assertFalse(zoo.findInventoryByNumber(tNum).isPresent());
        assertTrue(zoo.findInventoryByNumber(rNum).isPresent());
    }
}
