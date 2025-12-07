package zoo.cli;

import zoo.domain.animal.Animal;
import zoo.domain.animal.AnimalType;
import zoo.domain.common.Inventory;
import zoo.domain.thing.Thing;
import zoo.domain.thing.ThingType;
import zoo.domain.zoo.Zoo;

import java.util.List;
import java.util.Scanner;

public class ConsoleRunner {

    private final Zoo zoo;
    private final Scanner scanner;

    public ConsoleRunner(Zoo zoo, Scanner scanner) {
        this.zoo = zoo;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleAddAnimal();
                case "2" -> handleAddThing();
                case "3" -> handleShowAnimals();
                case "4" -> handleShowTotalFood();
                case "5" -> handleShowContactZooAnimals();
                case "6" -> handleShowInventory();
                case "7" -> handleRemoveByNumber();
                case "0" -> running = false;
                default -> System.out.println("Неизвестная команда, попробуйте ещё раз.");
            }
        }
        System.out.println("Выход из программы.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Московский зоопарк ===");
        System.out.println("1. Добавить животное");
        System.out.println("2. Добавить вещь");
        System.out.println("3. Показать всех животных");
        System.out.println("4. Показать суммарное количество корма в сутки");
        System.out.println("5. Показать животных для контактного зоопарка");
        System.out.println("6. Показать весь инвентарь (животные + вещи)");
        System.out.println("7. Удалить объект по инвентарному номеру");
        System.out.println("0. Выход");
        System.out.print("Ваш выбор: ");
    }

    private void handleAddAnimal() {
        AnimalType type = askAnimalType();
        if (type == null) {
            System.out.println("Тип животного не выбран, операция отменена.");
            return;
        }

        System.out.print("Введите имя животного: ");
        String name = scanner.nextLine().trim();

        int foodPerDay = askInt("Введите количество корма в кг/сутки: ");

        int kindness = 0;
        // доброта нужна только для травоядных
        if (type == AnimalType.MONKEY || type == AnimalType.RABBIT) {
            kindness = askInt("Введите уровень доброты (0..10): ");
        }

        Animal animal = zoo.addAnimal(type, name, foodPerDay, kindness);
        if (animal == null) {
            System.out.println("Животное НЕ принято в зоопарк (не прошло проверку у ветеринара).");
        } else {
            System.out.println("Животное добавлено. Инвентарный номер: " + animal.getNumber());
        }
    }

    private void handleAddThing() {
        ThingType type = askThingType();
        if (type == null) {
            System.out.println("Тип вещи не выбран, операция отменена.");
            return;
        }

        System.out.print("Введите наименование вещи: ");
        String name = scanner.nextLine().trim();

        Thing thing = zoo.addThing(type, name);
        System.out.println("Вещь добавлена. Инвентарный номер: " + thing.getNumber());
    }

    private void handleShowAnimals() {
        List<Animal> animals = zoo.getAllAnimals();
        if (animals.isEmpty()) {
            System.out.println("Животных пока нет.");
            return;
        }
        System.out.println("Список животных:");
        for (Animal a : animals) {
            System.out.println(
                    "- " + a.getName() +
                            " (№ " + a.getNumber() + "), " +
                            "корм: " + a.getFoodPerDay() + " кг/сутки"
            );
        }
    }

    private void handleShowTotalFood() {
        int total = zoo.getTotalDailyFood();
        System.out.println("Суммарное количество корма для всех животных: " + total + " кг/сутки");
    }

    private void handleShowContactZooAnimals() {
        List<Animal> contact = zoo.getContactZooAnimals();
        if (contact.isEmpty()) {
            System.out.println("Животных для контактного зоопарка пока нет.");
            return;
        }
        System.out.println("Животные, которые могут быть помещены в контактный зоопарк:");
        for (Animal a : contact) {
            System.out.println("- " + a.getName() + " (№ " + a.getNumber() + ")");
        }
    }

    private void handleShowInventory() {
        List<Inventory> items = zoo.getAllInventory();
        if (items.isEmpty()) {
            System.out.println("Инвентарь пуст.");
            return;
        }
        System.out.println("Инвентарные объекты (животные и вещи):");
        for (Inventory item : items) {
            System.out.println("- " + item.getName() + " (№ " + item.getNumber() + ")");
        }
    }

    private void handleRemoveByNumber() {
        int number = askInt("Введите инвентарный номер для удаления: ");
        boolean removed = zoo.removeByInventoryNumber(number);
        if (removed) {
            System.out.println("Объект с номером " + number + " удалён.");
        } else {
            System.out.println("Объект с таким номером не найден.");
        }
    }

    private AnimalType askAnimalType() {
        System.out.println("Выберите тип животного:");
        System.out.println("1. Обезьяна");
        System.out.println("2. Кролик");
        System.out.println("3. Тигр");
        System.out.println("4. Волк");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> AnimalType.MONKEY;
            case "2" -> AnimalType.RABBIT;
            case "3" -> AnimalType.TIGER;
            case "4" -> AnimalType.WOLF;
            default -> null;
        };
    }

    private ThingType askThingType() {
        System.out.println("Выберите тип вещи:");
        System.out.println("1. Стол");
        System.out.println("2. Компьютер");
        System.out.print("Ваш выбор: ");
        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> ThingType.TABLE;
            case "2" -> ThingType.COMPUTER;
            default -> null;
        };
    }

    private int askInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }
}
