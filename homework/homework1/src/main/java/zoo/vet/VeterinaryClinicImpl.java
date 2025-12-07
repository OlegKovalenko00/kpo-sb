package zoo.vet;

import org.springframework.stereotype.Component;
import zoo.domain.animal.Animal;

@Component
public class VeterinaryClinicImpl implements VeterinaryClinic {

    @Override
    public boolean isHealthy(Animal animal) {
        return animal.getFoodPerDay() > 0;
    }
}
