package zoo;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Optional;

public class Main {
    private static void createThingInteractive(Scanner scanner, Zoo zoo) {
        System.out.println("Введите тип вещи: thing, table, computer");
        System.out.print("Тип: ");
        String type = readLineSafe(scanner);
        if (type == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        type = type.trim().toLowerCase();

        System.out.print("Наименование: ");
        String name = readLineSafe(scanner);
        if (name == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        name = name.trim();
        if (name.isEmpty()) name = "Unnamed";

        Inventory thing;
        switch (type) {
            case "table":
                thing = new Table(name);
                break;
            case "computer":
                thing = new Computer(name);
                break;
            case "thing":
                thing = new Thing(name);
                break;
            default:
                System.out.println("Неизвестный тип вещи. Использую общий Thing.");
                thing = new Thing(name);
        }

        zoo.addThing(thing);
        System.out.println("Вещь добавлена: " + thing.getName() + " (Inv:" + thing.getNumber() + ")");
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        Zoo zoo = injector.getInstance(Zoo.class);

        boolean demo = false;
        for (String a : args) if ("--demo".equalsIgnoreCase(a)) demo = true;

        if (demo) {
            zoo.addThing(new Thing("Office Table #1"));
            zoo.addThing(new Computer("Reception PC"));

            Animal healthyRabbit = new Rabbit("Bunny", 1, true, 8);
            Animal sickTiger = new Tiger("Tigro", 6, false);

            System.out.println("Попытка принять здорового кролика:");
            System.out.println("  " + healthyRabbit);
            System.out.println(zoo.tryAdmitAnimal(healthyRabbit) ? "  => Принят" : "  => Отклонён");

            System.out.println("Попытка принять больного тигра:");
            System.out.println("  " + sickTiger);
            System.out.println(zoo.tryAdmitAnimal(sickTiger) ? "  => Принят" : "  => Отклонён");
        } else {
            System.out.println("Запуск без демо-данных. Чтобы включить демо, перезапустите с аргументом --demo");
        }

        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println();
                System.out.println("Выберите действие:");
                System.out.println("1 - Добавить животное");
                System.out.println("2 - Суммарное потребление еды (кг/сут)");
                System.out.println("3 - Список животных в контактный зоопарк");
                System.out.println("4 - Вывести инвентарь (вещи + животные)");
                System.out.println("5 - Удалить животное по инв. номеру");
                System.out.println("6 - Удалить вещь по инв. номеру");
                System.out.println("7 - Добавить вещь (Table / Computer / Thing)");
                System.out.println("0 - Выйти");
                System.out.print(">> ");

                String cmd = readLineSafe(scanner);
                if (cmd == null) {
                    System.out.println();
                    System.out.println("Ввод завершён (EOF). Завершение программы.");
                    break;
                }
                cmd = cmd.trim();

                if ("0".equals(cmd)) break;
                switch (cmd) {
                    case "1":
                        createAnimalInteractive(scanner, zoo);
                        break;
                    case "2":
                        System.out.println("Всего требуется " + zoo.totalFoodPerDayKg() + " кг в сутки.");
                        break;
                    case "3":
                        List<Animal> list = zoo.getContactZooCandidates();
                        System.out.println("Животные, пригодные для контактного зоопарка:");
                        for (Animal a : list) {
                            System.out.println(" - " + a + (a instanceof Herbivore ? " (Kindness:" + ((Herbivore) a).getKindness() + ")" : ""));
                        }
                        break;
                    case "4":
                        System.out.println("Инвентаризационные объекты на балансе:");
                        for (Inventory i : zoo.getInventoryItems()) {
                            System.out.println(" - " + i.getName() + " (Inv:" + i.getNumber() + ")");
                        }
                        break;
                    case "5":
                        System.out.print("Введите инв. номер животного для удаления: ");
                        String numStr5 = readLineSafe(scanner);
                        if (numStr5 == null) {
                            System.out.println("Ввод завершён. Отмена.");
                            break;
                        }
                        try {
                            int num = Integer.parseInt(numStr5.trim());
                            Optional<Inventory> found = zoo.findInventoryByNumber(num);
                            if (found.isEmpty() || !(found.get() instanceof Animal)) {
                                System.out.println("Животное с таким инв. номером не найдено.");
                            } else {
                                Animal a = (Animal) found.get();
                                System.out.println("Найдено животное: " + a);
                                System.out.print("Подтвердить удаление животного? (y/n): ");
                                String conf = readLineSafe(scanner);
                                if (conf == null) {
                                    System.out.println("Ввод завершён. Отмена.");
                                    break;
                                }
                                conf = conf.trim().toLowerCase();
                                if (conf.equals("y") || conf.equals("yes")) {
                                    boolean removed = zoo.removeAnimalByNumber(num);
                                    System.out.println(removed ? "Животное удалено." : "Ошибка удаления.");
                                } else {
                                    System.out.println("Удаление отменено.");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Неверный номер.");
                        }
                        break;
                    case "6":
                        System.out.print("Введите инв. номер вещи для удаления: ");
                        String numStr6 = readLineSafe(scanner);
                        if (numStr6 == null) {
                            System.out.println("Ввод завершён. Отмена.");
                            break;
                        }
                        try {
                            int num = Integer.parseInt(numStr6.trim());
                            Optional<Inventory> found = zoo.findInventoryByNumber(num);
                            if (found.isEmpty() || !(found.get() instanceof Thing)) {
                                System.out.println("Вещь с таким инв. номером не найдена.");
                            } else {
                                Thing t = (Thing) found.get();
                                System.out.println("Найдена вещь: " + t);
                                System.out.print("Подтвердить удаление вещи? (y/n): ");
                                String conf = readLineSafe(scanner);
                                if (conf == null) {
                                    System.out.println("Ввод завершён. Отмена.");
                                    break;
                                }
                                conf = conf.trim().toLowerCase();
                                if (conf.equals("y") || conf.equals("yes")) {
                                    boolean removed = zoo.removeThingByNumber(num);
                                    System.out.println(removed ? "Вещь удалена." : "Ошибка удаления.");
                                } else {
                                    System.out.println("Удаление отменено.");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Неверный номер.");
                        }
                        break;
                    case "7":
                        createThingInteractive(scanner, zoo);
                        break;

                    default:
                        System.out.println("Неизвестная команда");
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Входной поток был закрыт. Завершение.");
        } finally {
            scanner.close();
        }
    }

    private static void createAnimalInteractive(Scanner scanner, Zoo zoo) {
        System.out.println("Введите тип животного: rabbit, monkey, tiger, wolf");
        System.out.print("Тип: ");
        String type = readLineSafe(scanner);
        if (type == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        type = type.trim().toLowerCase();

        System.out.print("Имя: ");
        String name = readLineSafe(scanner);
        if (name == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        name = name.trim();
        if (name.isEmpty()) name = "Unnamed";

        System.out.print("Сколько кг ест в сутки (целое): ");
        String foodStr = readLineSafe(scanner);
        if (foodStr == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        int food;
        try {
            food = Integer.parseInt(foodStr.trim());
        } catch (Exception e) {
            food = 1;
        }

        System.out.print("Животное здорово? (y/n): ");
        String healthyInput = readLineSafe(scanner);
        if (healthyInput == null) {
            System.out.println("Ввод завершён. Отмена операции.");
            return;
        }
        boolean isHealthy = healthyInput.trim().toLowerCase().equals("y") || healthyInput.trim().toLowerCase().equals("yes");

        Integer kindness = null;
        if ("rabbit".equals(type) || "monkey".equals(type)) {
            System.out.print("Уровень доброты (0..10): ");
            String kindStr = readLineSafe(scanner);
            if (kindStr == null) {
                System.out.println("Ввод завершён. Отмена операции.");
                return;
            }
            try {
                kindness = Integer.parseInt(kindStr.trim());
            } catch (Exception e) {
                kindness = 0;
            }
        }

        try {
            Animal animal = AnimalFactory.create(type, name, food, isHealthy, kindness);
            boolean accepted = zoo.tryAdmitAnimal(animal);
            System.out.println(accepted ? "Животное принято на баланс." : "Животное отклонено ветклиникой.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static String readLineSafe(Scanner scanner) {
        try {
            if (!scanner.hasNextLine()) return null;
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
