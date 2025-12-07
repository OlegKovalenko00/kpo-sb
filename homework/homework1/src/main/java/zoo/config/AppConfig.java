package zoo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zoo.cli.ConsoleRunner;
import zoo.domain.zoo.Zoo;
import zoo.factory.AnimalFactory;
import zoo.factory.SimpleAnimalFactory;
import zoo.vet.VeterinaryClinic;
import zoo.vet.VeterinaryClinicImpl;

import java.util.Scanner;

@Configuration
public class AppConfig {

    @Bean
    public VeterinaryClinic veterinaryClinic() {
        return new VeterinaryClinicImpl();
    }

    @Bean
    public AnimalFactory animalFactory() {
        return new SimpleAnimalFactory();
    }

    @Bean
    public Zoo zoo(VeterinaryClinic clinic, AnimalFactory animalFactory) {
        return new Zoo(clinic, animalFactory);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public ConsoleRunner consoleRunner(Zoo zoo, Scanner scanner) {
        return new ConsoleRunner(zoo, scanner);
    }
}

