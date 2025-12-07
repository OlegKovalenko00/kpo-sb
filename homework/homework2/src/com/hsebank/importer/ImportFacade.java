package com.hsebank.importer;

import com.hsebank.domain.Operation;
import com.hsebank.service.BankAccountService;
import com.hsebank.service.CategoryService;
import com.hsebank.service.OperationService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class ImportFacade {

    private final BankAccountService bankAccountService;
    private final CategoryService categoryService;
    private final OperationService operationService;

    public ImportFacade(BankAccountService bankAccountService,
                        CategoryService categoryService,
                        OperationService operationService) {
        this.bankAccountService = bankAccountService;
        this.categoryService = categoryService;
        this.operationService = operationService;
    }


    public void importOperations(Importer importer, Path file) throws Exception {
        List<Map<String, Object>> rows = importer.importAsListOfMaps(file);
        for (Map<String, Object> row : rows) {
            String typeStr = asString(row.get("type"));
            String accountId = firstNotNull(
                    asString(row.get("bankAccountId")),
                    asString(row.get("accountId"))
            );
            String categoryId = asString(row.get("categoryId"));
            String amountStr = asString(row.get("amount"));
            String dateStr = asString(row.get("date"));
            String desc = asString(row.get("description"));
            if (desc != null && desc.isBlank()) desc = null;

            if (typeStr == null || accountId == null || categoryId == null ||
                    amountStr == null || dateStr == null) {
                continue;
            }

            Operation.Type type = Operation.Type.valueOf(typeStr.toUpperCase());
            BigDecimal amount = new BigDecimal(amountStr);
            LocalDate date = LocalDate.parse(dateStr);

            operationService.create(
                    type,
                    accountId.trim(),
                    categoryId.trim(),
                    amount,
                    date,
                    desc
            );
        }
    }

    private static String asString(Object o) {
        return o == null ? null : o.toString();
    }

    private static String firstNotNull(String a, String b) {
        return a != null ? a : b;
    }
}
