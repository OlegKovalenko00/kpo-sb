# HSE Bank – модуль «Учет финансов»
## Инструкция по запуску

### 1. Требования

- Java 17+  
  Проверить версию:

  ```bash
  java -version

    Gradle-wrapper уже лежит в репозитории (gradlew, gradlew.bat, папка gradle/).

Проект собран вокруг консольного приложения com.hsebank.app.Main, которое поднимает Spring-контекст и запускает ConsoleApplication с текстовым меню.
2. Сборка проекта

Из корня проекта (папка homework2):

./gradlew clean build

В результате собирается fat-jar (внутри уже есть все зависимости, включая Spring):

build/libs/homework2-1.0-SNAPSHOT.jar

3. Запуск через Gradle (рекомендуется для проверки)

./gradlew run

После запуска появится консольное меню:

=== HSE Bank Finances ===
1) Create account
2) List accounts
3) Create category
4) List categories
5) Add operation
6) List operations
7) Analytics: net for period
8) Analytics: group by category for period
9) Import operations (csv/json/yaml)
10) Export operations (csv/json/yaml)
11) Recalculate balances from history
0) Exit
   Your choice:

Ввод команд — с клавиатуры, через числа меню.
4. Запуск через jar

Если нужен отдельный запуск без Gradle:

java -jar build/libs/homework2-1.0-SNAPSHOT.jar

Поведение и меню полностью совпадают с ./gradlew run.
5. Основные сценарии для проверки

   Создание счёта

        Пункт 1) Create account

        Ввести название счёта, например: Main account.

   Создание категорий

        Пункт 3) Create category

        Примеры:

            тип: INCOME, название: Salary;

            тип: EXPENSE, название: Cafe.

   Добавление операций

        Пункт 5) Add operation

        Указать:

            тип (INCOME / EXPENSE);

            bankAccountId (см. List accounts);

            categoryId (см. List categories);

            сумму;

            дату (YYYY-MM-DD);

            описание (можно пустым).

   Просмотр данных

        2) List accounts — счета и их балансы;

        4) List categories — категории;

        6) List operations — операции.

   Аналитика

        7) Analytics: net for period — доходы/расходы и разница за период;

        8) Analytics: group by category for period — суммы по категориям.

   Импорт операций

        9) Import operations (csv/json/yaml)

        Формат файла выбирается в консоли (csv, json, yaml), далее указывается путь к файлу.

   Экспорт операций

        10) Export operations (csv/json/yaml)

        Выбор формата и указание пути для сохранения.

   Пересчёт балансов

        11) Recalculate balances from history

        Балансы счетов пересчитываются по истории операций.

6. Структура проекта

   src/com/hsebank/app — точка входа (Main), консольное приложение (ConsoleApplication), конфиг Spring (AppConfig).

   src/com/hsebank/domain — доменные классы (BankAccount, Category, Operation).

   src/com/hsebank/service — бизнес-логика (сервисы).

   src/com/hsebank/repository — интерфейсы и in-memory реализации репозиториев.

   src/com/hsebank/factory — фабрика доменных объектов (DomainFactory).

   src/com/hsebank/analytics — аналитика (AnalyticsFacade).

   src/com/hsebank/importer — импорт (Template Method + фасад).

   src/com/hsebank/export — экспорт (Visitor).

   src/com/hsebank/command — команды (Command + Decorator).

   src/com/hsebank/metrics — сборщик метрик (MetricsCollector).