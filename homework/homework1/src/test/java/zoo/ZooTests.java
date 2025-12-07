package zoo;

import org.junit.jupiter.api.Test;
import zoo.domain.animal.Animal;
import zoo.domain.animal.AnimalType;
import zoo.domain.thing.Thing;
import zoo.domain.thing.ThingType;
import zoo.domain.zoo.Zoo;
import zoo.factory.SimpleAnimalFactory;
import zoo.vet.VeterinaryClinic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ZooTests {

    static class AcceptAllClinic implements VeterinaryClinic {
        @Override
        public boolean isHealthy(zoo.domain.animal.Animal animal) {
            return true;
        }
    }

    static class RejectAllClinic implements VeterinaryClinic {
        @Override
        public boolean isHealthy(zoo.domain.animal.Animal animal) {
            return false;
        }
    }

    @Test
    void addAnimal_acceptAllClinic_animalIsAdded() {
        Zoo zoo = new Zoo(new AcceptAllClinic(), new SimpleAnimalFactory());

        Animal a = zoo.addAnimal(AnimalType.RABBIT, "Bunny", 2, 8);

        assertNotNull(a);
        assertEquals(1, zoo.getAllAnimals().size());
        assertEquals("Bunny", a.getName());
        assertEquals(2, a.getFoodPerDay());
    }

    @Test
    void addAnimal_rejectAllClinic_animalIsNotAdded() {
        Zoo zoo = new Zoo(new RejectAllClinic(), new SimpleAnimalFactory());

        Animal a = zoo.addAnimal(AnimalType.RABBIT, "Bunny", 2, 8);

        assertNull(a);
        assertTrue(zoo.getAllAnimals().isEmpty());
    }

    @Test
    void totalFood_isSumOfAllAnimals() {
        Zoo zoo = new Zoo(new AcceptAllClinic(), new SimpleAnimalFactory());

        zoo.addAnimal(AnimalType.RABBIT, "Bunny", 2, 8);
        zoo.addAnimal(AnimalType.MONKEY, "Chichi", 3, 7);
        zoo.addAnimal(AnimalType.TIGER, "Sherkhan", 10, 0);

        assertEquals(2 + 3 + 10, zoo.getTotalDailyFood());
    }

    @Test
    void contactZooAnimals_onlyKindHerbo() {
        Zoo zoo = new Zoo(new AcceptAllClinic(), new SimpleAnimalFactory());

        zoo.addAnimal(AnimalType.RABBIT, "Добрый кролик", 2, 9); // в контактный
        zoo.addAnimal(AnimalType.RABBIT, "Злой кролик", 2, 3);   // не идёт
        zoo.addAnimal(AnimalType.TIGER, "Тигр", 10, 0);          // хищник

        List<Animal> contact = zoo.getContactZooAnimals();

        assertEquals(1, contact.size());
        assertEquals("Добрый кролик", contact.get(0).getName());
    }

    @Test
    void inventory_containsAnimalsAndThings_andRemoveByNumberWorks() {
        Zoo zoo = new Zoo(new AcceptAllClinic(), new SimpleAnimalFactory());

        Animal a1 = zoo.addAnimal(AnimalType.RABBIT, "Кролик", 2, 8);
        Thing t1 = zoo.addThing(ThingType.TABLE, "Столик");

        List<zoo.domain.common.Inventory> all = zoo.getAllInventory();
        assertEquals(2, all.size());

        int animalNumber = a1.getNumber();
        assertTrue(zoo.removeByInventoryNumber(animalNumber));

        all = zoo.getAllInventory();
        assertEquals(1, all.size());
        assertEquals(t1.getNumber(), all.get(0).getNumber());
    }
}
