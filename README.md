# Automated Municipal Library E-Book Licensing & Digital Asset Management

An industrial-grade, municipal-style digital library management platform and digital asset accounting system built with **Java 27 / 25**, **Spring Boot 4.0.x / 3.4.x**, **Spring Data JPA**, **Hibernate**, **MySQL**, and a responsive municipal frontend.

---

## 1. Project Overview

The **Automated Municipal Library E-Book Licensing & Digital Asset Management System** is designed for modern city library authorities, university library consortia, and municipal digital asset directors. It unifies digital e-book cataloging, publisher license procurement, real-time concurrent user limits, cryptographic digital access key lifecycles, and a complete municipal double-entry accounting engine.

### Core Problems Solved:
1. **Publisher Licensing Caps**: Publisher e-book licenses specify rigid concurrent user limits. The platform strictly enforces concurrency, rejecting checkouts with HTTP 409 Conflict when capacity is exhausted and automatically placing patrons in an ordered digital hold queue.
2. **Access Key Lifecycle**: Checkouts issue time-bound cryptographic access tokens (`EBOOK-ACCESS-XXXXXXXX`). Expired or returned loans automatically revoke tokens and free up license capacity.
3. **Double-Entry Municipal Accounting**: Every procurement purchase order, vendor bill, royalty disbursement, patron membership card, and periodic asset amortization automatically generates audited, balanced journal entries (`Debit = Credit`).
4. **Dynamic Departmental Budgets**: Actual expenditures are dynamically aggregated from posted licensing transactions and compared against planned allocations to track variances and utilization percentages in real time.

---

## 2. Features

- **Executive Municipal Dashboard**:
  - Live metric cards: Total E-books, Active Licenses, Active Loans, Remaining Capacity, Digital Asset Inventory, Licensing Expense, Patron Revenue, Outstanding Payables.
  - Interactive Visual License Capacity Tracker: Real-time progress bars with dynamic color alerting (green `<70%`, yellow `70-90%`, red `100% full capacity`).
  - Interactive Doughnut Chart: Distribution of capitalized assets, revenue, expenditures, and payables.
  - Audited Financial Ledger feed showing recent balanced journal transactions.
- **E-Book Catalog Master (Module 3)**:
  - Full metadata tracking: ISBN, Title, Author, Publisher, Category, Publication Year, Language, Description, and Active Status.
- **Digital License Management & Concurrency Controls (Modules 4 & 5)**:
  - Perpetual vs. Term licenses with configurable concurrent limits.
  - Live capacity monitoring: `(activeCheckouts / concurrentUserLimit) * 100`.
  - Rejection of checkout loans when capacity limit is reached (`409 Conflict`).
- **E-Book Checkouts & Digital Access Keys (Modules 6 & 7)**:
  - Generates unique digital keys (`EBOOK-ACCESS-XXXXXXXX`).
  - Automated return mechanism: frees up license capacity and fulfills the next waiting hold.
  - 1-click batch revocation for expired access keys.
- **Digital Holds Queue (Module 8)**:
  - Enables patrons to wait in an ordered queue when all concurrent slots are utilized.
  - Automatic promotion/fulfillment upon checkout return.
- **Master Data (Modules 2, 9 & 10)**:
  - Contact Master (Patrons as Customers, Publishers as Vendors).
  - Product & Fee Master (Perpetual Licenses, Non-Resident Cards, Academic Passes).
  - Chart of Accounts (Assets, Liabilities, Income, Expenses).
- **Procurement & Vendor Accounting (Modules 14, 15 & 16)**:
  - Purchase Orders $\rightarrow$ Vendor Bills $\rightarrow$ Vendor Payments.
  - Automatic posting of Publisher Royalty Payables and Cash disbursements.
  - Strict validation preventing payments from exceeding outstanding bill balances.
- **Patron Subscriptions & Revenue (Modules 17, 18 & 19)**:
  - Sales Orders $\rightarrow$ Customer Invoices $\rightarrow$ Patron Payments.
  - Non-Resident Subscription Revenue recognition and cash reconciliation.
  - Strict validation preventing payments from exceeding outstanding invoice balances.
- **Audited Financial Journals & Double-Entry Ledger (Module 11)**:
  - Sales, Purchase, Bank, Cash, and General Journals.
  - Mathematical integrity enforcement: $\sum \text{Debit} = \sum \text{Credit}$.
- **Educational License Straight-Line Amortization (Module 13)**:
  - Periodic amortization: $\text{Monthly Amortization} = \frac{\text{Purchase Cost}}{\text{Amortization Period (Months)}}$.
  - Posts balanced journal: `Debit: 5010 Digital Amortization & Licensing Expenses` | `Credit: 1020 Digital Asset Inventory`.
  - Updates remaining asset value on each license.
- **Municipal Budgets & Analytic Accounts (Modules 20 & 21)**:
  - Multi-department tracking (e.g., City Digital Media Unit).
  - Rule 8: Actual expenditure dynamically calculated from posted licensing transactions (no hardcoding).
- **Financial & Operational Reports (Module 22)**:
  - Statement of Financial Position (Balance Sheet).
  - Municipal Operating Statement (Profit & Loss).
  - Departmental Budget Performance Report.
  - License Utilization & Concurrency Audit Matrix.
  - Patron Activity & Digital Engagement Audit.

---

## 3. Technology Stack

- **Language**: Java 27 (compatible with JDK 21, 25, 27)
- **Framework**: Spring Boot 3.4.x / 4.0.x
- **Persistence**: Spring Data JPA, Hibernate 6.x
- **Database**: MySQL 8.0+
- **Build Tool**: Apache Maven (Maven Wrapper `mvnw` / `mvnw.cmd` included)
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons, Chart.js 4.4
- **API Architecture**: RESTful JSON APIs with Jakarta Bean Validation

---

## 4. Prerequisites

1. **Java Development Kit (JDK)**: JDK 21, 25, or 27 installed.
2. **MySQL Server**: MySQL 8.0 or MariaDB running on port 3306.
3. **IDE (Optional)**: IntelliJ IDEA (Community or Ultimate), Eclipse, or VS Code.
4. **Internet Connection**: For initial Maven dependency resolution.

---

## 5. Java Setup

Ensure your `JAVA_HOME` environment variable points to your JDK installation (Java 21, 25, or 27):

### Windows (PowerShell):
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot"
java -version
javac -version
```

---

## 6. MySQL Setup

Ensure MySQL Server is running as a service:
```powershell
Get-Service -Name *mysql*
```

If MySQL is stopped, start it:
```powershell
Start-Service -Name MySQL80
```

---

## 7. Database Creation

Connect to MySQL via terminal or MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS library_ebook_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 8. application.properties Configuration

The configuration file is located at `src/main/resources/application.properties`:

```properties
spring.application.name=Municipal Library E-Book Licensing

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/library_ebook_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
# Set your password or override via DB_PASSWORD environment variable
spring.datasource.password=${DB_PASSWORD:root}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA & Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Port
server.port=8080

# Logging
logging.level.com.library.ebook=INFO
logging.level.org.springframework.web=INFO
```

---

## 9. Maven Commands

### Compile and Verify:
```powershell
.\mvnw clean compile
```

### Run Tests:
```powershell
.\mvnw test
```

### Package into Executable JAR:
```powershell
.\mvnw clean package -DskipTests
```

---

## 10. How to Run

### Option A: Using Maven Wrapper (Terminal)
```powershell
# From the project directory:
.\mvnw spring-boot:run
```

### Option B: Running the Packaged JAR
```powershell
java -jar target/municipal-library-ebook-management-1.0.0.jar
```

Once started, the application will initialize demo data and be available at:
`http://localhost:8080`

---

## 11. IntelliJ IDEA Setup

1. Open **IntelliJ IDEA**.
2. Click **File $\rightarrow$ Open...**
3. Select the folder containing `pom.xml`:  
   `c:\Users\Prajit Radhakrishnan\OneDrive\Desktop\Java SpringBoot`
4. IntelliJ will detect the `pom.xml` and import Maven dependencies automatically.
5. In **File $\rightarrow$ Project Structure $\rightarrow$ Project**:
   - Set **SDK** to **Java 21, 25, or 27**.
   - Set **Language level** to **21 - Records, pattern matching, virtual threads** or Default.
6. Locate the main class:
   `src/main/java/com/library/ebook/MunicipalLibraryApplication.java`
7. Click the green **Run ▶** icon next to `public static void main(String[] args)`.
8. The console will display Spring Boot startup banners and load the demo dataset.
9. Open your browser and navigate to `http://localhost:8080`.

---

## 12. API Endpoint Examples

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/ebooks` | Retrieve all catalog e-books |
| `POST` | `/api/ebooks` | Add a new e-book |
| `GET` | `/api/ebooks/licenses` | Retrieve all digital licenses |
| `POST` | `/api/ebooks/licenses` | Create a new digital license |
| `GET` | `/api/ebooks/licenses/utilization` | Concurrency & utilization metrics |
| `POST` | `/api/ebooks/checkout` | Checkout an e-book (issues access key) |
| `POST` | `/api/ebooks/checkout/{id}/return` | Return loan & free license capacity |
| `POST` | `/api/ebooks/access-keys/revoke-expired`| Revoke all expired access keys |
| `GET` | `/api/ebooks/holds` | Get active digital holds queue |
| `POST` | `/api/ebooks/holds` | Place a digital hold |
| `GET` | `/api/contacts` | Retrieve patrons and publishers |
| `POST` | `/api/contacts` | Create a new contact |
| `GET` | `/api/products` | Retrieve products & card passes |
| `GET` | `/api/accounts` | Retrieve Chart of Accounts & balances |
| `GET` | `/api/journals/entries` | Retrieve audited double-entry journal entries |
| `POST` | `/api/journals/entries` | Post balanced manual journal entry |
| `POST` | `/api/amortization/{id}` | Run straight-line amortization for license |
| `POST` | `/api/amortization/run-all` | Batch amortize all active licenses |
| `GET` | `/api/purchase-orders` | Retrieve publisher purchase orders |
| `POST` | `/api/purchase-orders` | Create publisher purchase order |
| `GET` | `/api/vendor-bills` | Retrieve publisher vendor bills |
| `POST` | `/api/vendor-bills` | Post publisher vendor bill & journal |
| `POST` | `/api/vendor-payments` | Pay vendor bill & post bank journal |
| `GET` | `/api/invoices` | Retrieve patron subscription invoices |
| `POST` | `/api/invoices` | Issue patron invoice & recognize revenue |
| `POST` | `/api/customer-payments` | Record patron membership fee payment |
| `GET` | `/api/budgets` | Retrieve departmental budgets & actuals |
| `POST` | `/api/budgets` | Create departmental budget allocation |
| `GET` | `/api/reports/dashboard` | Executive KPI dashboard summary |
| `GET` | `/api/reports/balance-sheet` | Statement of Financial Position |
| `GET` | `/api/reports/profit-and-loss` | Operating Statement (Surplus / Deficit) |
| `GET` | `/api/reports/budget-report` | Departmental budget variance report |
| `GET` | `/api/reports/patron-activity` | Patron activity and loan engagement |

### Sample Checkout Request:
```bash
POST /api/ebooks/checkout
Content-Type: application/json

{
  "patronId": 1,
  "licenseId": 1
}
```

### Sample Response:
```json
{
  "id": 8,
  "patron": { "id": 1, "name": "Alice Walker" },
  "ebook": { "id": 1, "title": "Introduction to Digital Science" },
  "license": { "id": 1, "licenseNumber": "LIC-0001", "activeCheckoutCount": 4 },
  "accessKey": "EBOOK-ACCESS-9C84BA21",
  "checkoutDate": "2026-09-26T12:45:00",
  "expiryDate": "2026-10-10T12:45:00",
  "status": "ACTIVE"
}
```

---

## 13. Default Localhost URL

- **Web Dashboard**: [http://localhost:8080](http://localhost:8080)
- **REST Endpoints**: `http://localhost:8080/api/...`

---

## 14. Folder Structure

```
c:/Users/Prajit Radhakrishnan/OneDrive/Desktop/Java SpringBoot/
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── library/
        │           └── ebook/
        │               ├── MunicipalLibraryApplication.java
        │               ├── controller/
        │               │   ├── AccessKeyController.java
        │               │   ├── AccountController.java
        │               │   ├── AmortizationController.java
        │               │   ├── BudgetController.java
        │               │   ├── CheckoutController.java
        │               │   ├── ContactController.java
        │               │   ├── CustomerPaymentController.java
        │               │   ├── EbookController.java
        │               │   ├── HoldController.java
        │               │   ├── InvoiceController.java
        │               │   ├── JournalController.java
        │               │   ├── LicenseController.java
        │               │   ├── ProductController.java
        │               │   ├── PurchaseOrderController.java
        │               │   ├── ReportController.java
        │               │   ├── SalesOrderController.java
        │               │   ├── VendorBillController.java
        │               │   ├── VendorPaymentController.java
        │               │   └── ViewController.java
        │               ├── dto/
        │               │   ├── ApiResponse.java
        │               │   ├── AmortizationResponse.java
        │               │   ├── BalanceSheetDto.java
        │               │   ├── BudgetReportDto.java
        │               │   ├── CheckoutRequest.java
        │               │   ├── DashboardStatsDto.java
        │               │   ├── HoldRequest.java
        │               │   ├── JournalEntryRequest.java
        │               │   ├── JournalLineRequest.java
        │               │   ├── LicenseUtilizationDto.java
        │               │   ├── PatronActivityDto.java
        │               │   └── ProfitAndLossDto.java
        │               ├── entity/
        │               │   ├── AccessKey.java
        │               │   ├── AccessKeyStatus.java
        │               │   ├── AccountType.java
        │               │   ├── AnalyticAccount.java
        │               │   ├── BillStatus.java
        │               │   ├── Budget.java
        │               │   ├── ChartOfAccount.java
        │               │   ├── Checkout.java
        │               │   ├── CheckoutStatus.java
        │               │   ├── Contact.java
        │               │   ├── ContactType.java
        │               │   ├── CustomerInvoice.java
        │               │   ├── CustomerPayment.java
        │               │   ├── Ebook.java
        │               │   ├── EbookHold.java
        │               │   ├── HoldStatus.java
        │               │   ├── InvoiceStatus.java
        │               │   ├── Journal.java
        │               │   ├── JournalEntry.java
        │               │   ├── JournalItem.java
        │               │   ├── JournalType.java
        │               │   ├── License.java
        │               │   ├── LicenseStatus.java
        │               │   ├── LicenseType.java
        │               │   ├── OrderStatus.java
        │               │   ├── PaymentMethod.java
        │               │   ├── PaymentStatus.java
        │               │   ├── Product.java
        │               │   ├── ProductType.java
        │               │   ├── PurchaseOrder.java
        │               │   ├── SalesOrder.java
        │               │   ├── VendorBill.java
        │               │   └── VendorPayment.java
        │               ├── exception/
        │               │   ├── BusinessValidationException.java
        │               │   ├── ErrorResponse.java
        │               │   ├── GlobalExceptionHandler.java
        │               │   ├── InvalidPaymentException.java
        │               │   ├── LicenseExpiredException.java
        │               │   ├── LicenseInactiveException.java
        │               │   ├── LicenseLimitExceededException.java
        │               │   ├── ResourceNotFoundException.java
        │               │   └── UnbalancedJournalException.java
        │               ├── initializer/
        │               │   └── DataInitializer.java
        │               ├── repository/
        │               │   ├── AccessKeyRepository.java
        │               │   ├── AnalyticAccountRepository.java
        │               │   ├── BudgetRepository.java
        │               │   ├── ChartOfAccountRepository.java
        │               │   ├── CheckoutRepository.java
        │               │   ├── ContactRepository.java
        │               │   ├── CustomerInvoiceRepository.java
        │               │   ├── CustomerPaymentRepository.java
        │               │   ├── EbookHoldRepository.java
        │               │   ├── EbookRepository.java
        │               │   ├── JournalEntryRepository.java
        │               │   ├── JournalItemRepository.java
        │               │   ├── JournalRepository.java
        │               │   ├── LicenseRepository.java
        │               │   ├── ProductRepository.java
        │               │   ├── PurchaseOrderRepository.java
        │               │   ├── SalesOrderRepository.java
        │               │   ├── VendorBillRepository.java
        │               │   └── VendorPaymentRepository.java
        │               └── service/
        │                   ├── AccessKeyService.java
        │                   ├── AccountingService.java
        │                   ├── AmortizationService.java
        │                   ├── BudgetService.java
        │                   ├── CheckoutService.java
        │                   ├── ContactService.java
        │                   ├── EbookService.java
        │                   ├── HoldService.java
        │                   ├── InvoiceService.java
        │                   ├── LicenseService.java
        │                   ├── PaymentService.java
        │                   ├── ProductService.java
        │                   ├── PurchaseOrderService.java
        │                   ├── ReportService.java
        │                   ├── SalesOrderService.java
        │                   ├── VendorBillService.java
        │                   └── VendorPaymentService.java
        └── resources/
            ├── application.properties
            └── static/
                ├── css/
                │   └── style.css
                ├── js/
                │   └── app.js
                └── index.html
```

---

## 15. Database Explanation

The platform uses relational persistence with Spring Data JPA and MySQL:
- `ebooks`: Master metadata for institutional digital books.
- `contacts`: Unified table for Patrons (`CUSTOMER`) and Publishers (`VENDOR`).
- `ebook_licenses`: Digital contracts linking books and publishers, containing concurrent user caps, costs, and remaining asset valuations.
- `checkouts`: Active and returned loans tied to patrons and licenses.
- `access_keys`: Cryptographic access tokens issued per checkout with expiration timestamps.
- `ebook_holds`: First-come, first-served queue positions for oversubscribed licenses.
- `products`: Product catalog for license goods and annual membership cards.
- `chart_of_accounts`: Master accounting accounts (Assets, Liabilities, Income, Expenses).
- `journals` & `journal_entries` & `journal_items`: Double-entry transaction ledger with debit/credit balance enforcement.
- `purchase_orders` & `vendor_bills` & `vendor_payments`: Publisher accounts payable workflow.
- `sales_orders` & `customer_invoices` & `customer_payments`: Patron accounts receivable and subscription revenue workflow.
- `analytic_accounts` & `budgets`: Departmental budget allocations and dynamic variance calculations.

---

## 16. Business Workflow

1. **Procurement**:
   Director creates Purchase Order $\rightarrow$ Vendor Bill posted $\rightarrow$ Creates Journal Entry:
   - Debit: `1020 Digital Asset Inventory` (or Licensing Expense)
   - Credit: `2010 Publisher Royalty Payables`
2. **Disbursement**:
   Disbursement officer pays vendor $\rightarrow$ Creates Journal Entry:
   - Debit: `2010 Publisher Royalty Payables`
   - Credit: `1010 Cash`
3. **Licensing**:
   E-Book License created with concurrent capacity (e.g. 5 concurrent readers).
4. **Circulation & Concurrency**:
   Patron requests checkout $\rightarrow$ System verifies `activeCheckouts < concurrentUserLimit`.
   - If capacity available: Issues checkout loan, increments count, and generates `EBOOK-ACCESS-XXXXXXXX`.
   - If capacity full: Returns HTTP 409 Conflict, prompts patron to place a Digital Hold.
5. **Returns & Queue Fulfillment**:
   Patron returns e-book $\rightarrow$ Active checkout decremented, digital key revoked, next waiting hold in queue marked `FULFILLED`.
6. **Subscriptions**:
   Non-resident patron purchases Annual Card $\rightarrow$ Invoice posted:
   - Debit: `1030 Patron Receivable`
   - Credit: `4010 Non-Resident Subscription Revenue`
   - Payment received: Debit `Cash`, Credit `Patron Receivable`.
7. **Asset Amortization**:
   Periodic straight-line amortization runs $\rightarrow$ Calculates `Purchase Cost / Months`:
   - Debit: `5010 Digital Amortization & Licensing Expenses`
   - Credit: `1020 Digital Asset Inventory`
   - Updates remaining asset value on license.

---

## 17. Accounting Explanation

The system adheres strictly to the fundamental accounting equation:
$$\text{Assets} = \text{Liabilities} + \text{Fund Balance (Equity)}$$

### Standard Ledger Accounts:
- **1010 Cash** (Asset): Operating funds.
- **1020 Digital Asset Inventory** (Asset): Capitalized digital e-book licenses.
- **1030 Patron Receivable** (Asset): Billed but unpaid patron fees.
- **2010 Publisher Royalty Payables** (Liability): Outstanding publisher debt.
- **4010 Non-Resident Subscription Revenue** (Income): Card fees earned.
- **4020 Late Access Fees** (Income): Digital overdue penalties.
- **5010 Digital Amortization & Licensing Expenses** (Expense): Depreciated asset cost.
- **5020 Server Costs** (Expense): Cloud and infrastructure expenses.

Every transaction requires $\sum \text{Debit} = \sum \text{Credit}$. Unbalanced manual or automated transactions are rejected with `UnbalancedJournalException`.

---

## 18. License Concurrency Explanation

Unlike physical books, digital e-books are governed by license agreements stipulating a maximum concurrent reader count:

$$\text{Capacity Available} = \text{Concurrent User Limit} - \text{Active Checkouts}$$
$$\text{Utilization \%} = \left(\frac{\text{Active Checkouts}}{\text{Concurrent User Limit}}\right) \times 100$$

- **Constraint**: If `activeCheckouts >= concurrentUserLimit`, another checkout cannot occur. Attempted checkouts return HTTP 409 Conflict.
- **Holds Integration**: When capacity is zero, the system prevents checkout and queues patrons by order of timestamp. Returning a checkout automatically fulfills the first waiting hold.

---

## 19. Troubleshooting

1. **MySQL Access Denied (`ERROR 1045`)**:
   - Verify your MySQL password in `src/main/resources/application.properties`.
   - Set environment variable `DB_PASSWORD=your_password` or update `spring.datasource.password=your_password`.
2. **Port 8080 Already in Use**:
   - Change `server.port=8081` in `application.properties`.
3. **Maven Build Fails on Java Version**:
   - Ensure JDK 21+ is installed and `JAVA_HOME` points to your JDK directory.
4. **IntelliJ Cannot Resolve Spring Boot Imports**:
   - Right-click `pom.xml` $\rightarrow$ **Maven $\rightarrow$ Reload Project**.
   - Go to **File $\rightarrow$ Invalidate Caches / Restart**.
