package com.example.wastewater.service;

import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.AnalyticAccount;
import com.example.wastewater.entity.Budget;
import com.example.wastewater.entity.ChemicalInventory;
import com.example.wastewater.entity.ChemicalSupplier;
import com.example.wastewater.entity.EnvironmentalSettings;
import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.entity.Product;
import com.example.wastewater.entity.TelemetryReading;
import com.example.wastewater.entity.User;
import com.example.wastewater.enums.AccountType;
import com.example.wastewater.enums.FactoryStatus;
import com.example.wastewater.enums.ProductType;
import com.example.wastewater.enums.UserRole;
import com.example.wastewater.repository.AccountRepository;
import com.example.wastewater.repository.AnalyticAccountRepository;
import com.example.wastewater.repository.BudgetRepository;
import com.example.wastewater.repository.ChemicalInventoryRepository;
import com.example.wastewater.repository.ChemicalSupplierRepository;
import com.example.wastewater.repository.EnvironmentalSettingsRepository;
import com.example.wastewater.repository.IndustrialFactoryRepository;
import com.example.wastewater.repository.ProductRepository;
import com.example.wastewater.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Startup component that seeds default master data, demo users, accounts,
 * chemical stock inventory, and telemetry readings into MySQL database upon launch.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EnvironmentalSettingsRepository settingsRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ChemicalSupplierRepository supplierRepository;
    private final IndustrialFactoryRepository factoryRepository;
    private final ChemicalInventoryRepository inventoryRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final BudgetRepository budgetRepository;
    private final TelemetryService telemetryService;

    public DataInitializer(UserRepository userRepository,
                           EnvironmentalSettingsRepository settingsRepository,
                           AccountRepository accountRepository,
                           ProductRepository productRepository,
                           ChemicalSupplierRepository supplierRepository,
                           IndustrialFactoryRepository factoryRepository,
                           ChemicalInventoryRepository inventoryRepository,
                           AnalyticAccountRepository analyticAccountRepository,
                           BudgetRepository budgetRepository,
                           TelemetryService telemetryService) {
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.factoryRepository = factoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.analyticAccountRepository = analyticAccountRepository;
        this.budgetRepository = budgetRepository;
        this.telemetryService = telemetryService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already present in MySQL database
        }

        // 1. Seed System Users
        userRepository.save(new User("admin", "admin123", "Environmental Controller", UserRole.ADMIN));
        userRepository.save(new User("accountant", "acc123", "Utility Accountant", UserRole.ACCOUNTANT));

        // 2. Seed Default Environmental Settings
        EnvironmentalSettings settings = new EnvironmentalSettings();
        settings.setDefaultCodLimit(250.0);
        settings.setFineRate(50.0);
        settings.setCurrency("$");
        settingsRepository.save(settings);

        // 3. Seed Chart of Accounts
        accountRepository.save(new Account("1010", "Treatment Plant Infrastructure", AccountType.ASSET, 500000.0));
        accountRepository.save(new Account("1020", "Bank Account", AccountType.ASSET, 150000.0));
        accountRepository.save(new Account("1030", "Cash", AccountType.ASSET, 25000.0));
        accountRepository.save(new Account("2010", "Chemical Payables", AccountType.LIABILITY, 0.0));
        accountRepository.save(new Account("3010", "Effluent Treatment Fees", AccountType.INCOME, 0.0));
        accountRepository.save(new Account("3020", "Compliance Fines", AccountType.INCOME, 0.0));
        accountRepository.save(new Account("4010", "Chemical Processing Expenses", AccountType.EXPENSE, 0.0));
        accountRepository.save(new Account("4020", "Operational Expenses", AccountType.EXPENSE, 0.0));

        // 4. Seed Products and Services Master
        Product p1 = new Product();
        p1.setProductName("Industrial Effluent Processing");
        p1.setProductCode("SRV-EFF-01");
        p1.setType(ProductType.SERVICE);
        p1.setDescription("Standard municipal wastewater purification service");
        p1.setUnit("Month");
        p1.setPrice(5000.0);
        p1.setTaxPercentage(18.0);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setProductName("Chlorine Chemical Agent");
        p2.setProductCode("CHM-CLR-01");
        p2.setType(ProductType.GOODS);
        p2.setDescription("High purity chlorine agent for wastewater treatment");
        p2.setUnit("Kg");
        p2.setPrice(150.0);
        p2.setTaxPercentage(18.0);
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setProductName("Discharge Quality Certificate");
        p3.setProductCode("SRV-CERT-01");
        p3.setType(ProductType.SERVICE);
        p3.setDescription("Official environmental discharge audit and certification");
        p3.setUnit("Unit");
        p3.setPrice(1200.0);
        p3.setTaxPercentage(18.0);
        productRepository.save(p3);

        // 5. Seed Initial Chemical Inventory (Stock <= Reorder Level triggers Low Stock alert)
        ChemicalInventory inventory = new ChemicalInventory();
        inventory.setProduct(p2);
        inventory.setQuantity(40.0);
        inventory.setReorderLevel(50.0);
        inventory.setUnitCost(150.0);
        inventoryRepository.save(inventory);

        // 6. Seed Chemical Supplier
        ChemicalSupplier supplier = new ChemicalSupplier();
        supplier.setSupplierName("Aqua Chemicals Pvt Ltd");
        supplier.setContactPerson("Rajesh Sharma");
        supplier.setPhone("+91-9876543210");
        supplier.setEmail("info@aquachemicals.com");
        supplier.setAddress("42 Industrial Estate, Zone B");
        supplier.setTaxNumber("TAX-IND-88921");
        supplierRepository.save(supplier);

        // 7. Seed Industrial Factories
        IndustrialFactory factory1 = new IndustrialFactory();
        factory1.setFactoryName("ABC Chemical Industries");
        factory1.setRegistrationNumber("REG-2026-001");
        factory1.setContactPerson("Siddharth Roy");
        factory1.setPhone("+91-9123456780");
        factory1.setEmail("contact@abcchem.com");
        factory1.setAddress("Plot 12, Chemical Zone");
        factory1.setCity("Coimbatore");
        factory1.setIndustryType("Chemical Manufacturing");
        factory1.setPermittedCodLimit(250.0);
        factory1.setStatus(FactoryStatus.ACTIVE);
        factoryRepository.save(factory1);

        IndustrialFactory factory2 = new IndustrialFactory();
        factory2.setFactoryName("GreenTech Manufacturing");
        factory2.setRegistrationNumber("REG-2026-002");
        factory2.setContactPerson("Ananya Sen");
        factory2.setPhone("+91-9811223344");
        factory2.setEmail("env@greentech.io");
        factory2.setAddress("88 Eco Tech Park");
        factory2.setCity("Chennai");
        factory2.setIndustryType("Textile & Dyeing");
        factory2.setPermittedCodLimit(200.0);
        factory2.setStatus(FactoryStatus.ACTIVE);
        factoryRepository.save(factory2);

        IndustrialFactory factory3 = new IndustrialFactory();
        factory3.setFactoryName("Coimbatore Industrial Works");
        factory3.setRegistrationNumber("REG-2026-003");
        factory3.setContactPerson("Karthik Raman");
        factory3.setPhone("+91-9445566778");
        factory3.setEmail("ops@coimbatoreworks.com");
        factory3.setAddress("15 Heavy Industry Sector");
        factory3.setCity("Coimbatore");
        factory3.setIndustryType("Electroplating");
        factory3.setPermittedCodLimit(300.0);
        factory3.setStatus(FactoryStatus.ACTIVE);
        factoryRepository.save(factory3);

        // 8. Seed Analytic Accounts & Budgets
        AnalyticAccount analyticAccount = new AnalyticAccount("East Industrial Effluent Plant", "Primary municipal treatment facility for East industrial corridor", "East Zone Facility");
        analyticAccountRepository.save(analyticAccount);

        Budget budget = new Budget();
        budget.setAnalyticAccount(analyticAccount);
        budget.setFinancialYear("2026-2027");
        budget.setAllocatedAmount(100000.0);
        budget.setActualAmount(24500.0);
        budget.recalculateBudget();
        budgetRepository.save(budget);

        // 9. Seed Telemetry Readings (Triggers automatic compliance evaluation & violation generation)
        TelemetryReading t1 = new TelemetryReading();
        t1.setFactory(factory1);
        t1.setCodValue(180.0);
        t1.setLocation("Outfall Sensor 1");
        t1.setRemarks("Normal operating parameters");
        telemetryService.submitTelemetry(t1);

        TelemetryReading t2 = new TelemetryReading();
        t2.setFactory(factory2);
        t2.setCodValue(360.0); // Exceeds 200 limit -> Auto-creates Violation ($8,000 fine) & flags factory!
        t2.setLocation("Outfall Sensor 2");
        t2.setRemarks("High chemical concentration detected");
        telemetryService.submitTelemetry(t2);

        TelemetryReading t3 = new TelemetryReading();
        t3.setFactory(factory3);
        t3.setCodValue(270.0);
        t3.setLocation("Outfall Sensor 3");
        t3.setRemarks("Compliant discharge");
        telemetryService.submitTelemetry(t3);
    }
}
