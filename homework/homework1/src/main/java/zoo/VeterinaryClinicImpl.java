package zoo;

public class VeterinaryClinicImpl implements VeterinaryClinic {
    @Override
    public boolean isHealthy(Animal animal) {
        if (animal == null) return false;
        return animal.isHealthyFlag();
    }
}
