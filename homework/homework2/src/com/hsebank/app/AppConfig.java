package com.hsebank.app;

import com.hsebank.analytics.AnalyticsFacade;
import com.hsebank.export.CsvExportVisitor;
import com.hsebank.export.JsonExportVisitor;
import com.hsebank.export.YamlExportVisitor;
import com.hsebank.factory.DomainFactory;
import com.hsebank.importer.CsvImporter;
import com.hsebank.importer.ImportFacade;
import com.hsebank.importer.JsonImporter;
import com.hsebank.importer.YamlImporter;
import com.hsebank.metrics.MetricsCollector;
import com.hsebank.repository.BankAccountRepository;
import com.hsebank.repository.CategoryRepository;
import com.hsebank.repository.InMemoryBankAccountRepository;
import com.hsebank.repository.InMemoryCategoryRepository;
import com.hsebank.repository.InMemoryOperationRepository;
import com.hsebank.repository.OperationRepository;
import com.hsebank.service.BankAccountService;
import com.hsebank.service.CategoryService;
import com.hsebank.service.OperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.hsebank")
public class AppConfig {

    @Bean
    public DomainFactory domainFactory() {
        return new DomainFactory();
    }

    @Bean
    public BankAccountRepository bankAccountRepository() {
        return new InMemoryBankAccountRepository();
    }

    @Bean
    public CategoryRepository categoryRepository() {
        return new InMemoryCategoryRepository();
    }

    @Bean
    public OperationRepository operationRepository() {
        return new InMemoryOperationRepository();
    }

    @Bean
    public BankAccountService bankAccountService(BankAccountRepository repo,
                                                 DomainFactory factory) {
        return new BankAccountService(repo, factory);
    }

    @Bean
    public CategoryService categoryService(CategoryRepository repo,
                                           DomainFactory factory) {
        return new CategoryService(repo, factory);
    }

    @Bean
    public OperationService operationService(OperationRepository opRepo,
                                             BankAccountRepository bankRepo,
                                             DomainFactory factory) {
        return new OperationService(opRepo, bankRepo, factory);
    }

    // --- аналитика и импорт/экспорт ---

    @Bean
    public AnalyticsFacade analyticsFacade(OperationRepository operationRepo) {
        return new AnalyticsFacade(operationRepo);
    }

    @Bean
    public ImportFacade importFacade(BankAccountService bankService,
                                     CategoryService categoryService,
                                     OperationService operationService) {
        return new ImportFacade(bankService, categoryService, operationService);
    }

    @Bean public CsvImporter csvImporter() { return new CsvImporter(); }
    @Bean public JsonImporter jsonImporter() { return new JsonImporter(); }
    @Bean public YamlImporter yamlImporter() { return new YamlImporter(); }

    @Bean public CsvExportVisitor csvExportVisitor() { return new CsvExportVisitor(); }
    @Bean public JsonExportVisitor jsonExportVisitor() { return new JsonExportVisitor(); }
    @Bean public YamlExportVisitor yamlExportVisitor() { return new YamlExportVisitor(); }

    @Bean
    public MetricsCollector metricsCollector() {
        return new MetricsCollector();
    }
}
