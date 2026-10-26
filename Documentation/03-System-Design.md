# System design

```mermaid
flowchart TD
 A[Launch] --> B[Login]
 B --> C{Role}
 C -->|Owner| D[Inventory and management]
 C -->|Cashier| E[Time In]
 E --> F[Scan or tap products]
 F --> G{Valid stock and payment}
 G -->|Yes| H[Atomic FIFO sale and receipt]
 G -->|No| F
 H --> I[Time Out]
 D --> J[Reports and offline insights]
```

Compose screens call ViewModels, then repositories. Room transactions enforce stock and shift rules. Room stores users, categories, products, receipts, lines, audit entries, batches, shifts and suppliers. Each receipt line snapshots its batch date/cost. Shift totals are derived from receipts. LocalAiService uses JNI/llama.cpp with pre-calculated facts. WorkManager handles daily summaries.

```mermaid
erDiagram
 CATEGORY ||--o{ PRODUCT : contains
 PRODUCT ||--o{ BATCH : receives
 SHIFT ||--o{ TRANSACTION : groups
 TRANSACTION ||--o{ LINE_ITEM : contains
 BATCH ||--o{ LINE_ITEM : supplies
 USER ||--o{ SHIFT : works
```

Historical user/product/batch relationships use snapshots rather than deleting receipts. Supplier names are copied into batches, so deleting a supplier does not erase history.
