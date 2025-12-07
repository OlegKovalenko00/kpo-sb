package com.hsebank.app;

import com.hsebank.analytics.AnalyticsFacade;
import com.hsebank.command.AddOperationCommand;
import com.hsebank.command.Command;
import com.hsebank.command.CreateAccountCommand;
import com.hsebank.command.CreateCategoryCommand;
import com.hsebank.command.TimedCommandDecorator;
import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;
import com.hsebank.export.ExportVisitor;
import com.hsebank.export.CsvExportVisitor;
import com.hsebank.export.JsonExportVisitor;
import com.hsebank.export.YamlExportVisitor;
import com.hsebank.importer.CsvImporter;
import com.hsebank.importer.ImportFacade;
import com.hsebank.importer.JsonImporter;
import com.hsebank.importer.YamlImporter;
import com.hsebank.metrics.MetricsCollector;
import com.hsebank.service.BankAccountService;
import com.hsebank.service.CategoryService;
import com.hsebank.service.OperationService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public final class ConsoleApplication {

    private final BankAccountService bankAccountService;
    private final CategoryService categoryService;
    private final OperationService operationService;
    private final AnalyticsFacade analyticsFacade;
    private final ImportFacade importFacade;
    private final CsvImporter csvImporter;
    private final JsonImporter jsonImporter;
    private final YamlImporter yamlImporter;
    private final ExportVisitor csvExportVisitor;
    private final ExportVisitor jsonExportVisitor;
    private final ExportVisitor yamlExportVisitor;
    private final MetricsCollector metricsCollector;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApplication(BankAccountService bankAccountService,
                              CategoryService categoryService,
                              OperationService operationService,
                              AnalyticsFacade analyticsFacade,
                              ImportFacade importFacade,
                              CsvImporter csvImporter,
                              JsonImporter jsonImporter,
                              YamlImporter yamlImporter,
                              CsvExportVisitor csvExportVisitor,
                              JsonExportVisitor jsonExportVisitor,
                              YamlExportVisitor yamlExportVisitor,
                              MetricsCollector metricsCollector) {
        this.bankAccountService = bankAccountService;
        this.categoryService = categoryService;
        this.operationService = operationService;
        this.analyticsFacade = analyticsFacade;
        this.importFacade = importFacade;
        this.csvImporter = csvImporter;
        this.jsonImporter = jsonImporter;
        this.yamlImporter = yamlImporter;
        this.csvExportVisitor = csvExportVisitor;
        this.jsonExportVisitor = jsonExportVisitor;
        this.yamlExportVisitor = yamlExportVisitor;
        this.metricsCollector = metricsCollector;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": runWithTiming(new CreateAccountCommand(bankAccountService, ask("Account name"))); break;
                    case "2": listAccounts(); break;
                    case "3": createCategory(); break;
                    case "4": listCategories(); break;
                    case "5": addOperation(); break;
                    case "6": listOperations(); break;
                    case "7": analyticsNet(); break;
                    case "8": analyticsByCategory(); break;
                    case "9": importOperationsMenu(); break;
                    case "10": exportOperationsMenu(); break;
                    case "11": recalcBalances(); break;
                    case "0": System.out.println("Bye!"); return;
                    default: System.out.println("Unknown choice"); break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== HSE Bank Finances ===");
        System.out.println("1) Create account");
        System.out.println("2) List accounts");
        System.out.println("3) Create category");
        System.out.println("4) List categories");
        System.out.println("5) Add operation");
        System.out.println("6) List operations");
        System.out.println("7) Analytics: net for period");
        System.out.println("8) Analytics: group by category for period");
        System.out.println("9) Import operations (csv/json/yaml)");
        System.out.println("10) Export operations (csv/json/yaml)");
        System.out.println("11) Recalculate balances from history");
        System.out.println("0) Exit");
        System.out.print("Your choice: ");
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private void runWithTiming(Command command) {
        new TimedCommandDecorator(command, metricsCollector).execute();
    }

    private void listAccounts() {
        Collection<BankAccount> list = bankAccountService.list();
        System.out.println("Accounts:");
        for (BankAccount a : list) {
            System.out.println(a.getId() + " | " + a.getName() + " | " + a.getBalance());
        }
    }

    private void createCategory() {
        String name = ask("Category name");
        System.out.print("Type (INCOME/EXPENSE): ");
        String t = scanner.nextLine().trim().toUpperCase();
        Category.Type type = Category.Type.valueOf(t);
        runWithTiming(new CreateCategoryCommand(categoryService, type, name));
    }

    private void listCategories() {
        Collection<Category> list = categoryService.list();
        System.out.println("Categories:");
        for (Category c : list) {
            System.out.println(c.getId() + " | " + c.getType() + " | " + c.getName());
        }
    }

    private void addOperation() {
        System.out.print("Type (INCOME/EXPENSE): ");
        Operation.Type type = Operation.Type.valueOf(scanner.nextLine().trim().toUpperCase());
        String accountId = ask("Bank account id");
        String categoryId = ask("Category id");
        BigDecimal amount = new BigDecimal(ask("Amount"));
        LocalDate date = LocalDate.parse(ask("Date (YYYY-MM-DD)"));
        String desc = ask("Description (optional, can be empty)");
        if (desc.isEmpty()) desc = null;

        Command cmd = new AddOperationCommand(operationService, type, accountId, categoryId, amount, date, desc);
        runWithTiming(cmd);
    }

    private void listOperations() {
        Collection<Operation> list = operationService.list();
        System.out.println("Operations:");
        for (Operation o : list) {
            System.out.println(o.getId() + " | " + o.getType() + " | acc=" + o.getBankAccountId()
                    + " | cat=" + o.getCategoryId() + " | " + o.getAmount()
                    + " | " + o.getDate() + " | " + o.getDescription());
        }
    }

    private LocalDate askDate(String label) {
        return LocalDate.parse(ask(label + " (YYYY-MM-DD)"));
    }

    private void analyticsNet() {
        LocalDate from = askDate("From");
        LocalDate to = askDate("To");
        System.out.println("Net for period: " + analyticsFacade.netForPeriod(from, to));
    }

    private void analyticsByCategory() {
        LocalDate from = askDate("From");
        LocalDate to = askDate("To");
        Map<String, java.math.BigDecimal> map = analyticsFacade.groupByCategory(from, to);
        System.out.println("Net by category:");
        for (Map.Entry<String, java.math.BigDecimal> e : map.entrySet()) {
            System.out.println("categoryId=" + e.getKey() + " -> " + e.getValue());
        }
    }

    private void importOperationsMenu() throws Exception {
        String pathStr = ask("File path");
        System.out.print("Format (csv/json/yaml): ");
        String fmt = scanner.nextLine().trim().toLowerCase();
        Path path = Path.of(pathStr);

        switch (fmt) {
            case "csv":
                runWithTiming(() -> importFacade.importOperations(csvImporter, path));
                break;
            case "json":
                runWithTiming(() -> importFacade.importOperations(jsonImporter, path));
                break;
            case "yaml":
            case "yml":
                runWithTiming(() -> importFacade.importOperations(yamlImporter, path));
                break;
            default:
                System.out.println("Unknown format");
        }
    }

    private void exportOperationsMenu() throws IOException {
        String pathStr = ask("Output file path");
        System.out.print("Format (csv/json/yaml): ");
        String fmt = scanner.nextLine().trim().toLowerCase();
        Path path = Path.of(pathStr);
        Collection<Operation> ops = operationService.list();

        String out;
        switch (fmt) {
            case "csv":
                out = csvExportVisitor.exportOperations(ops);
                break;
            case "json":
                out = jsonExportVisitor.exportOperations(ops);
                break;
            case "yaml":
            case "yml":
                out = yamlExportVisitor.exportOperations(ops);
                break;
            default:
                System.out.println("Unknown format");
                return;
        }
        Files.writeString(path, out);
        System.out.println("Exported to " + path);
    }

    private void recalcBalances() {
        runWithTiming(operationService::recalculateAllBalancesFromHistory);
        System.out.println("Balances recalculated from operations history.");
    }
}
