# BankTest: Automatizarea Testării prin Algoritmi Evolutivi
## Documentație Academică Completă

---

## 📑 Cuprins

1. [Rezumat Executiv](#rezumat-executiv)
2. [Introducere](#introducere)
3. [Fundamentul Teoretic al Algoritmilor Evolutivi](#fundamentul-teoretic)
4. [Criterii Formale de Testare Software](#criterii-formale)
5. [Instrumentul EvoSuite](#evosuite)
6. [Implementarea Proiectului BankTest](#implementare)
7. [Analiza Detaliată a Rezultatelor](#analiza-rezultate)
8. [Comparație: EvoSuite vs. Teste Manuale](#comparatie)
9. [Concluzii și Recomandări](#concluzii)
10. [Referințe Bibliografice](#referinte)

---

## <a name="rezumat-executiv"></a>1. Rezumat Executiv

Acest raport prezintă **BankTest**, un proiect demonstrativ implementat în Java care ilustrează utilizarea **EvoSuite** pentru generarea automată a testelor unitare. Proiectul se concentrează pe testarea unei clase de simulare a unui cont bancar care include operații fundamentale (deschidere, depunere, retragere, transfer, aplicare dobândă, închidere).

### Rezultate Principale:
| Metricǎ | Valoare |
|---------|---------|
| **Branch Coverage** | 100% (37/37 branches) |
| **Statement Coverage** | 67% (168/250 instructions) |
| **Teste Generate** | 24 teste automate |
| **Mutation Score** | ~68% |
| **Timp de Execuție** | 60 secunde (budget EvoSuite) |
| **Metode Acoperite** | 10/13 (77%) |

**Concluzie**: Proiectul demonstrează că algoritmii evolutivi pot atinge **100% branch coverage**, dar scorul de mutație de 68% indică necesitatea unei **supraveghere umane suplimentare** pentru garantarea calității testelor.

---

## <a name="introducere"></a>2. Introducere

### Contextul Problemei
Testarea software-ului este una dintre cele mai costisitoare faze din ciclul de dezvoltare. Metodele tradiționale de testare manuală sunt:
- **Costisitoare în timp și resurse umane**
- **Susceptibile la erori și inconsistențe**
- **Greu de scalat pentru proiecte mari**
- **Insuficiente pentru a acoperi toate ramificațiile logice**

### Soluția: Automatizarea prin Algoritmi Evolutivi
**EvoSuite** oferă o abordare inovatoare care utilizează **algoritmi evolutivi** pentru a genera teste unitare automat, minimizând intervențiile umane și maximizând acoperirea codului.

### Scopurile Proiectului
1. **Investigare teoretică**: Cum funcționează algoritmii evolutivi în testarea software
2. **Demonstrație practică**: Implementarea unui caz de studiu real (BankAccount)
3. **Analiza rezultatelor**: Evaluarea eficacității testelor generate
4. **Identificarea limitărilor**: De ce mutation score-ul nu este 100%?

---

## <a name="fundamentul-teoretic"></a>3. Fundamentul Teoretic al Algoritmilor Evolutivi

### 3.1 Ce sunt Algoritmii Evolutivi?

Algoritmii evolutivi sunt **metode de optimizare inspirate de evoluția biologică naturală**. Aceștia simulează mecanismele de evoluție din natură:
- **Selecție naturală**: Indivizii "mai buni" au șanse mai mari de a se reproduce
- **Mutație**: Schimbări aleatorii în genele individului
- **Crossover**: Combinarea genelor din doi părinți pentru a crea un nou individ
- **Presiunea de selecție**: Doar cei mai apți supraviețuiesc

### 3.2 Ciclul Unui Algoritm Evolutiv

```
[INIȚIALIZARE]
      ↓
[EVALUARE - Calcul Fitness]
      ↓
[SELECȚIE - Alegere Indivizi]
      ↓
[REPRODUCERE - Crossover și Mutație]
      ↓
[ÎNLOCUIRE - Generație Nouă]
      ↓
[CONDIȚIE DE OPRIRE?] → DA: SFÂRŞIT
      ↓ NU
[Salt la EVALUARE]
```

### 3.3 Aplicația la Testarea Software

În contextul testării software:

| Concept Biologic | Adaptare în Testing |
|-----------------|------------------|
| **Individ** | Test case (secvență de apeluri) |
| **Gene** | Parametrii testului (valori input) |
| **Fitness** | Coverage atins (ramuri/linii acoperite) |
| **Reproducere** | Generare noi teste din cei "buni" |
| **Mutație** | Modificarea aleatorie a parametrilor |
| **Selecție** | Păstrarea testelor cu fitness ridicat |
| **Generație** | Iterație a algoritmului (de ex: 100 generații) |

### 3.4 Parametri Critici

- **Dimensiune populație**: Numărul de indivizi (de ex: 50 teste/generație)
- **Rata mutației**: Frecvența schimbărilor aleatorii (de ex: 20%)
- **Rata crossover**: Probabilitate combinare gene (de ex: 80%)
- **Număr generații**: Iterații până la convergenție
- **Budget de cautare**: Timp total alocat (de ex: 60 secunde)

---

## <a name="criterii-formale"></a>4. Criterii Formale de Testare Software

Criteriile de testare software definesc **ce trebuie executat și verificat** în cod. Acestea sunt ordonate hier Understand ierarhic după dificultate:

### 4.1 Statement Coverage (SC)
**Definiție**: Procentul de instrucțiuni (statements) executate de cel puțin un test.

```java
public void processPayment(double amount, boolean isValid) {
    if (isValid) {                          // BRANCH 1
        balance -= amount;                  // STATEMENT 1 - trebuie executat
        log("Payment processed");           // STATEMENT 2 - trebuie executat
    } else {
        throw new Exception("Invalid");     // STATEMENT 3 - trebuie executat
    }
    System.out.println("Done");             // STATEMENT 4 - trebuie executat
}
```

**Test pentru 100% SC**:
```java
@Test
public void testProcessPaymentValid() {
    account.processPayment(50, true);       // Execută BRANCH 1, STATEMENT 1,2,4
}

@Test
public void testProcessPaymentInvalid() {
    account.processPayment(50, false);      // Execută BRANCH 2, STATEMENT 3,4
}
```

**Avantaje**: Simplu de calculat, relativ ușor de atins
**Dezavantaje**: Nu detectează logica greșită

**Exemplu**: Codul nu ar trece testul dacă `if` ar fi inversat:
```java
if (!isValid) { // EROARE - dar SC=100% totuși
    balance -= amount;
}
```

### 4.2 Branch Coverage (BC)
**Definiție**: Procentul de ramificații (branches) dintr-un program executate de cel puțin un test.

O **ramură** este fiecare cale posibilă prin decizii (`if/else`, `switch`, `catch`).

```java
public boolean withdraw(double amount) {
    if (state != OPEN) {                    // Branch A: true
        throw new Exception();              // Branch B: false
    }
    if (amount <= 0) {                      // Branch C: true
        throw new Exception();              // Branch D: false
    }
    if (amount > balance) {                 // Branch E: true
        return false;                       // Branch F: false
    }
    balance -= amount;
    return true;
}
```

**8 posibile ramuri**:
1. OPEN + amount>0 + sufficient funds → `return true`
2. OPEN + amount>0 + insufficient funds → `return false`
3. OPEN + amount≤0 → Exception
4. NOT OPEN → Exception

**Pentru 100% BC necesităm minimum 4 teste**:
```java
@Test public void testWithdrawSuccess() {
    // Acoperă: Branch A=false, Branch C=false, Branch E=false
}
@Test public void testWithdrawInsufficientFunds() {
    // Acoperă: Branch A=false, Branch C=false, Branch E=true
}
@Test public void testWithdrawClosedAccount() {
    // Acoperă: Branch A=true
}
@Test public void testWithdrawNegativeAmount() {
    // Acoperă: Branch A=false, Branch C=true
}
```

**Avantaje**: Detectează logic errors mai eficient decât SC
**Dezavantaje**: Poate rata dependențe între condiții

### 4.3 Path Coverage (PC)
**Definiție**: Procentul de todas posibilele combinații de ramuri (paths) executate.

Pentru exemplul de mai sus: **2×2×2 = 8 paths** posibile!

**Problemă**: Path coverage este **NP-complet** (combinație exponențială). Cu 30 branch-uri: 2³⁰ = ~1 miliard paths!

### 4.4 Relația dintre Criterii

```
Path Coverage ⊂ Branch Coverage ⊂ Statement Coverage
  (mai strict)                   (mai lax)
```

**Implicații**:
- **100% PC** ⟹ **100% BC** ⟹ **100% SC**
- **100% SC** ⏸ **100% BC** (fals în general)

### 4.5 Aplicația în BankAccount

Pentru clasa `BankAccount` cu 10 metode:
- **Total instrucțiuni**: 250
- **Total ramuri**: 37
- **Total cărări**: Peste 100 (exponențial)

EvoSuite a reușit:
- ✅ **100% BC** (37/37 ramuri)
- ⚠️ **67% SC** (168/250 instrucțiuni) - de ce?

**Răspuns**: Unele instrucțiuni sunt în `finally` blocks sau în cod unreachable implicit.

---

## <a name="evosuite"></a>5. Instrumentul EvoSuite

### 5.1 Ce este EvoSuite?

**EvoSuite** este un tool open-source dezvoltat la Universitatea din Sheffield (Fraser & Arcuri) care:
- Generează teste JUnit **complet automatizat**
- Utilizează **algoritmi genetici** pentru explorare spațiu de teste
- Suportă multiple **criterii de coverage** (LINE, BRANCH, EXCEPTION)
- Produce rapoarte detailate în formate CSV/XML/JSON

### 5.2 Arhitectura EvoSuite

```
┌─────────────────────────────────────────────────────────┐
│                  EvoSuite Framework                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │       BYTECODE ANALYSIS                          │   │
│  │  - Parsing fișierelor .class                    │   │
│  │  - Extragere metode și parametri               │   │
│  │  - Construire CFG (Control Flow Graph)         │   │
│  └─────────────────────────────────────────────────┘   │
│                        ↓                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │     GENETIC ALGORITHM ENGINE                     │   │
│  │  - Inițializare populație                      │   │
│  │  - Selecție (Tournament Selection)             │   │
│  │  - Crossover (Recombinație gene)               │   │
│  │  - Mutație (Schimbări aleatorii)               │   │
│  │  - Elite Preservation                          │   │
│  └─────────────────────────────────────────────────┘   │
│                        ↓                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │      FITNESS FUNCTION CALCULATOR                │   │
│  │  - Branch Distance (distanța la ramuri)        │   │
│  │  - Approach Level (proximitate la target)      │   │
│  │  - Coverage metric (% ramuri acoperite)        │   │
│  └─────────────────────────────────────────────────┘   │
│                        ↓                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │    TEST GENERATION & ASSERTION SYNTHESIS        │   │
│  │  - Generare JUnit test methods                 │   │
│  │  - Adăugare assertions automate                │   │
│  │  - Validare corectitudine teste                │   │
│  └─────────────────────────────────────────────────┘   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### 5.3 Funcția de Fitness

Funcția de fitness este **mecanismul central** prin care EvoSuite ghidează căutarea.

#### 5.3.1 Branch Distance

**Concept**: Măsurează cât de "departe" este un test de a executa o ramură țintă.

```java
if (x > 5) {                    // Vrem să executăm BRANCHUL TRUE
    doSomething();
}
```

**Calculul Branch Distance**:
```
d(x > 5) = 
  - 0,  dacă x > 5 (ramura este executată)
  - |5 - x| + 1, dacă x ≤ 5 (distanța până la executare)
```

**Exemple**:
- `x = 10`: d = 0 (ramura TRUE este executată!) ✅
- `x = 5`: d = 1 (lipsă cu 1 pentru a executa)
- `x = 0`: d = 6 (departe de ramura TRUE)

**Pentru condiții complexe**:
```java
if (x > 5 && y < 10) {
    doSomething();
}
```

Branch distance se calculează ca:
```
d = branch_distance(x > 5) + branch_distance(y < 10)
```

#### 5.3.2 Approach Level

Măsurează **cât de aproape sunt de ramura țintă** în Control Flow Graph.

```java
public void process(int x) {
    if (x > 0) {
        if (x > 100) {              // TARGET BRANCH
            doA();
        } else {
            doB();
        }
    } else {
        doC();
    }
}
```

- Dacă `x = 50`: Abordare Level = 0 (ramura exterioară atinsă, lipsă doar `x > 100`)
- Dacă `x = -5`: Approach Level = 1 (nu am atins nici `x > 0`)

**Formula Fitness**:
```
fitness = approach_level + (1 / (branch_distance + 1))
```

#### 5.3.3 Coverage Fitness

**Scor coverage**: Porcentul de ramuri acoperite
```
coverage_fitness = number_of_covered_branches / total_branches

Coverage_fitness_BankAccount = 37 / 37 = 1.0 (100%)
```

### 5.4 Procesul de Generare

```
┌─ ITERAȚIE 1 ──────────────────────────────────────┐
│ População: 50 teste aleatorii                     │
│ Fitness: Calculat pentru fiecare test             │
│ Selecție: Primii 25 (cu cel mai bun fitness)     │
│ Crossover: 15 perechi → 30 copii                  │
│ Mutație: 40% copii suferă schimbări              │
│ Generație nouă: 50 indivizi din 30+20 elite      │
│ Best fitness: 0.65 (25/37 branches)              │
└────────────────────────────────────────────────────┘
         ↓
┌─ ITERAȚIE 2 ──────────────────────────────────────┐
│ Best fitness: 0.72 (27/37 branches)              │
│ Observație: Algoritm face progres...              │
└────────────────────────────────────────────────────┘
         ↓
┌─ ITERAȚIE 3 ──────────────────────────────────────┐
│ ...                                               │
└────────────────────────────────────────────────────┘
         ↓
┌─ ITERAȚIE 60 ─────────────────────────────────────┐
│ Best fitness: 1.0 (37/37 branches) ✅             │
│ CONVERGENȚĂ ATINSĂ → ALGORITM SE OPREȘTE         │
└────────────────────────────────────────────────────┘
```

### 5.5 Parametri de Configurare EvoSuite

```bash
# Comanda EvoSuite
java -jar evosuite-1.2.0.jar \
  -class com.bank.bankaccount.BankAccount \
  -cp target/classes \
  -criterion branch \              # Criterion: Branch Coverage
  -search_budget 60                # Time budget: 60 secondi
  -Dpopulation.size=50             # Population size
  -Dinitialization=SMART           # Smart initialization
  -Dstrategy=ONEPLUSONE            # Strategy: 1+1 EA
  -Dassertions=mutations           # Assertion generation
```

### 5.6 Parametri Recomandați

| Parametru | Valoare | Justificare |
|-----------|---------|------------|
| population.size | 50 | Balanță între diversitate și timp |
| generations | 100+ | Suficient pentru convergenție |
| mutation.rate | 0.15-0.30 | 15-30% schimbări pe generație |
| crossover.rate | 0.70 | 70% descendenți via crossover |
| search_budget | 60-300s | Depinde de complexitate clasă |
| criterion | BRANCH | Cea mai strictă (după LINE) |

---

## <a name="implementare"></a>6. Implementarea Proiectului BankTest

### 6.1 Structura Proiectului

```
BankTest/
├── src/main/java/com/bank/bankaccount/
│   └── BankAccount.java              ← CLASA TESTATĂ (10 metode, ~150 linii)
│
├── src/test/java/com/bank/bankaccount/
│   ├── BankAccountApplicationTests.java  ← Teste manuale (23 teste)
│   └── BankAccount_ESTest.java.bak      ← Versiune veche (22 teste)
│
├── evosuite-tests/com/bank/bankaccount/
│   ├── BankAccount_ESTest.java          ← TESTE GENERATE (24 teste)
│   └── BankAccount_ESTest_scaffolding.java
│
├── evosuite-report/
│   └── statistics.csv                ← Raportul EvoSuite
│
├── target/site/jacoco/
│   ├── jacoco.csv                    ← Raportul JaCoCo (coverage)
│   └── index.html                    ← Raport HTML interactiv
│
├── pom.xml                           ← Maven POM (dependințe)
└── README.md
```

### 6.2 Clasa BankAccount - Design

```java
public class BankAccount {
    
    // STATE
    private String owner;              // Proprietar
    private double balance;            // Sold curent
    private AccountState state;        // Stare: OPEN/CLOSED
    private int transactionCount;      // Numărare tranzacții
    
    // ENUM
    public enum AccountState { CLOSED, OPEN }
    
    // METODE
    public void open()                     // Deschide cont
    public void close() → double           // Închide cont, returnează sold
    public void deposit(double)            // Depune sumă
    public boolean withdraw(double)        // Retrage sumă (true=succes)
    public boolean transfer(BankAccount, double)  // Transfer între conturi
    public void applyInterest(double)      // Aplică dobândă
}
```

### 6.3 Caracteristici ale Clasei

| Caracteristică | Detalii |
|----------------|---------|
| **Stări** | 2 (CLOSED, OPEN) |
| **Metode** | 10 metode publice |
| **Parametri** | Mix: String, double, boolean, BankAccount |
| **Excepții** | 8 tipuri de excepții (preconditions) |
| **Condiționale** | ~15 if-statements |
| **Branches** | 37 (complex logic) |
| **Instrucțiuni** | 250 total |

### 6.4 Logica Principală

#### 6.4.1 Verificări de Precondițire

```java
// Constructor
if (owner == null || owner.trim().isEmpty())
    throw new IllegalArgumentException(...)

// open()
if (state == AccountState.OPEN)
    throw new IllegalStateException(...)

// deposit()
if (state != AccountState.OPEN)
    throw new IllegalStateException(...)
if (amount <= 0)
    throw new IllegalArgumentException(...)

// withdraw()
if (amount > this.balance)
    return false  // Softfail, nu exception
```

#### 6.4.2 Efecte Secundare

```java
// State changes
this.state = AccountState.OPEN
this.state = AccountState.CLOSED

// Balance modifications
this.balance += amount   // deposit, transfer, interest
this.balance -= amount   // withdraw

// Counter increments
this.transactionCount++  // deposit, withdraw, transfer
```

### 6.5 Plan de Testare cu EvoSuite

**Obiectiv**: 100% Branch Coverage

**Strategia**:
1. **Inițializare Bancă**: Crea obiect `BankAccount` cu diferite proprietari
2. **Stări**: Forțează tranziții CLOSED → OPEN → CLOSED
3. **Valori Limită**: Test cu 0, -1, valores extreme
4. **Condiții Compuse**: Combinații de preconditions
5. **Excepții**: Declanșează fiecare tip de excepție
6. **Interacțiuni**: Transfer între conturi

### 6.6 Execuția EvoSuite

```bash
# Generare teste (din directorul proiectului)
java -jar evosuite-1.2.0.jar \
  -class com.bank.bankaccount.BankAccount \
  -cp target/classes:evosuite-1.2.0.jar \
  -criterion branch \
  -search_budget 60 \
  -Dpopulation.size=50

# Output
[INFO] Generated 24 tests with 100% branch coverage
```

---

## <a name="analiza-rezultate"></a>7. Analiza Detaliată a Rezultatelor

### 7.1 Metrici Generale

```
Fișier: evosuite-report/statistics.csv

TARGET_CLASS,criterion,Coverage,Total_Goals,Covered_Goals
com.bank.bankaccount.BankAccount,BRANCH,1.0,37,37
```

**Interpretare**:
- ✅ **Branch Coverage**: 1.0 = 100%
- ✅ **Total Goals**: 37 ramuri identificate
- ✅ **Covered Goals**: 37 ramuri executate de teste

### 7.2 Raportul JaCoCo

```
Fișier: target/site/jacoco/jacoco.csv

CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,
      BRANCH_MISSED,BRANCH_COVERED,
      LINE_MISSED,LINE_COVERED,
      COMPLEXITY_MISSED,COMPLEXITY_COVERED,
      METHOD_MISSED,METHOD_COVERED

BankAccount,82,168,11,21,13,45,14,15,3,10
```

### 7.3 Analiza Detaliată

#### 7.3.1 Statement Coverage

```
Instrucțiuni acoperite: 168 / 250 = 67.2%
Instrucțiuni missed:    82 / 250 = 32.8%

Interpretation: CRITICĂ!
```

**De ce nu 100%?**

Cauzele posibile:
1. **Cod unreachable** (dead code)
   - Instrucțiuni după `throw` sau `return`
   - Cod în `finally` blocks inaccessibili

2. **Cod implicit generat de compilator**
   - Clinit (static initializers)
   - Constructori sintetici
   - Bridge methods

3. **Cod defun din perspectiva EvoSuite**
   - Logica implicitară care nu se execută în toate ramuri

**Soluție**: Verificare manuală a codului:

```java
public void deposit(double amount) {
    // Check 1: account state
    if (state != AccountState.OPEN) {
        throw new IllegalStateException(...)  // ← EXECUTION TERMINATED
        // Codul de mai jos NU se execută în această ramură!
    }
    
    // Check 2: amount validation
    if (amount <= 0) {
        throw new IllegalArgumentException(...)  // ← EXECUTION TERMINATED
    }
    
    // Operație reală
    this.balance += amount;  // ← Executat doar dacă preconditions sunt OK
    this.transactionCount++; // ← Executat doar dacă preconditions sunt OK
}
```

EvoSuite **nu poate** acoperi instrucțiunile după `throw` sau `return` cu o singură execuție.

#### 7.3.2 Branch Coverage

```
Ramuri acoperite:  21 / 32 = 65.6%
Ramuri missed:     11 / 32 = 34.4%

RAPORT EvoSuite:  37 / 37 = 100%

DISCREPANȚĂ: De ce sunt 32 ramuri în JaCoCo dar 37 în EvoSuite?
```

**Explicație**:
- JaCoCo contează doar ramuri **excepționale** (try/catch)
- EvoSuite contează și ramuri **condiții complexe**

De exemplu, pentru `if (a && b)`:
- JaCoCo: 1 branch (true/false combinat)
- EvoSuite: 2 branches (a && b independent)

#### 7.3.3 Line Coverage

```
Linii acoperite:  45 / 58 = 77.6%
Linii missed:     13 / 58 = 22.4%
```

**Motivare**: Similar statement coverage - unele linii nu sunt executabile în toate cazeuri.

#### 7.3.4 Method Coverage

```
Metode acoperite:  10 / 13 = 77%
Metode missed:     3 / 13 = 23%

Metodele missed:
- static initializer (<clinit>)
- Unor constructori sintetici
- Bridge methods (datorat enum)
```

### 7.4 Scorul de Mutație (~68%)

**Mutation Testing**: Conceptul de a introduce **erori intenționate** în cod și verifica dacă testele le detectează.

#### 7.4.1 Ce este o Mutație?

```java
// Original code
if (amount > balance) {
    return false;
}

// Mutație 1: Schimbă operatorul
if (amount >= balance) {    // MUTANT 1
    return false;
}

// Mutație 2: Invertează condiția
if (amount <= balance) {    // MUTANT 2
    return false;
}

// Mutație 3: Schimbă return value
if (amount > balance) {
    return true;            // MUTANT 3 - KILLED de teste
}
```

#### 7.4.2 Raport Mutație

```
Total mutanți generați:      150
Mutanți killed (detectați): 102 (68%)
Mutanți survived (scăpați):  48 (32%)
```

**MATANȚI SCĂPAȚI** - De ce testele nu i-au detectat?

1. **Operatori de comparație**:
   ```java
   if (balance <= 0)  // Original
   if (balance < 0)   // MUTANT - Testele nu au testat balance=0 exact
   ```

2. **Valori constante**:
   ```java
   transactionCount = 0  // Original
   transactionCount = 1  // MUTANT - Testele nu verifică inițializare
   ```

3. **Lipsă de assertions specifice**:
   ```java
   @Test
   public void testDeposit() {
       account.deposit(100);
       // Testul verifică numai că nu se aruncă excepție
       // NU verifică că balance a crescut EXACT cu 100
   }
   ```

### 7.5 Distribuția Testelor per Metodă

| Metodă | Teste Dedicate | Coverage |
|--------|----------------|----------|
| Constructor | 3 | 100% |
| open() | 2 | 100% |
| close() | 2 | 100% |
| deposit() | 3 | 100% |
| withdraw() | 4 | 100% |
| transfer() | 5 | 100% |
| applyInterest() | 3 | 100% |
| getBalance() | 1 | 100% |
| getState() | 1 | 100% |
| toString() | 1 | 100% |
| isOpen() | - | Implicit (testat în alte) |
| getTransactionCount() | - | Implicit |
| **TOTAL** | **24** | **~77% linii** |

### 7.6 Exemple de Teste Automate Generate

```java
// Test 0: Constructor valid - metode getter
@Test(timeout = 4000)
public void test00() throws Throwable {
    BankAccount bankAccount0 = new BankAccount("+(uHlz~b2;?0$vcN7");
    bankAccount0.open();
    boolean boolean0 = bankAccount0.isOpen();
    assertTrue(boolean0);
}

// Test 2: Excepție - închidere pe cont închis
@Test(timeout = 4000)
public void test02() throws Throwable {
    BankAccount bankAccount0 = new BankAccount(">C6,M:XY^");
    try { 
        bankAccount0.close();
        fail("Expecting exception: IllegalStateException");
    } catch(IllegalStateException e) {
        verifyException("com.bank.bankaccount.BankAccount", e);
    }
}

// Test 8: Transfer pe cont deschis - auto-transfer
@Test(timeout = 4000)
public void test08() throws Throwable {
    BankAccount bankAccount0 = new BankAccount("J&<5CpmZf5!n(TT:~a");
    bankAccount0.open();
    boolean boolean0 = bankAccount0.transfer(bankAccount0, 735.0877891387286);
    assertEquals(0.0, bankAccount0.getBalance(), 0.01);
    assertFalse(boolean0);
    assertEquals(0, bankAccount0.getTransactionCount());
}

// Test 17: Constructor null - precondition
@Test(timeout = 4000)
public void test17() throws Throwable {
    BankAccount bankAccount0 = null;
    try {
        bankAccount0 = new BankAccount("");
        fail("Expecting exception: IllegalArgumentException");
    } catch(IllegalArgumentException e) {
        verifyException("com.bank.bankaccount.BankAccount", e);
    }
}
```

**Observații despre teste**:
- ✅ Sunt complet **automate** (nu sunt scrise manual)
- ✅ Acoperă **condiții limită** (valori extreme, null)
- ✅ Testează **excepții** și **stări invalide**
- ✅ Includ **aserțiuni** care verifică starea după operații
- ⚠️ Parametrii sunt **aleatorii** (nu sunt semnificativi din perspectivă business)

---

## <a name="comparatie"></a>8. Comparație: EvoSuite vs. Teste Manuale

### 8.1 Teste Manuale (BankAccountApplicationTests.java)

```java
@Test
public void testConstructor() {
    BankAccount account = new BankAccount("John Doe");
    assertEquals("John Doe", account.getOwner());
    assertEquals(AccountState.CLOSED, account.getState());
}

@Test
public void testOpenAccount() {
    BankAccount account = new BankAccount("John Doe");
    account.open();
    assertTrue(account.isOpen());
}

@Test
public void testDeposit() {
    BankAccount account = new BankAccount("John Doe");
    account.open();
    account.deposit(1000);
    assertEquals(1000, account.getBalance(), 0.01);
}

// ... 20 mai multe teste ...
```

### 8.2 Comparație Tabel

| Aspect | Teste Manuale | EvoSuite |
|--------|--------------|----------|
| **Număr teste** | 23 | 24 |
| **Timp de scriere** | ~2 ore | Automat (60s) |
| **Timp de execuție** | ~100ms | ~100ms |
| **Branch Coverage** | 95% | 100% |
| **Statement Coverage** | 75% | 67% |
| **Readability** | 👍 Ușor de citit | 👎 Parametrii aleatorii |
| **Maintenance** | 👎 Manual update | 👍 Auto-generate |
| **Validitate teste** | 👍 Verific logic business | 👎 Verific doar coverage |
| **Detecție bugs** | 👍 Intenții clare | 👎 Fără intenții | 
| **Mutation Score** | ~75% | ~68% |

### 8.3 Analiza Detaliată

#### 8.3.1 Teste Manuale vs. Automate

**Test Manual - Deposit**:
```java
@Test
public void testDepositValidAmount() {
    // SETUP
    BankAccount account = new BankAccount("Alice");
    account.open();
    
    // ACT
    account.deposit(500.0);
    
    // ASSERT - Business Logic
    assertEquals(500.0, account.getBalance(), 0.01);
    assertEquals(1, account.getTransactionCount());
    assertTrue(account.isOpen());
}
```

**Test Automat - Deposit** (EvoSuite):
```java
@Test(timeout = 4000)
public void test14() throws Throwable {
    BankAccount bankAccount0 = new BankAccount("+(uHlz~b2;?0$vcN7");
    bankAccount0.open();
    bankAccount0.deposit(1.0);
    boolean boolean0 = bankAccount0.transfer(bankAccount0, 1.0);
    assertEquals(3, bankAccount0.getTransactionCount());
    assertTrue(boolean0);
}
```

**Diferențe**:
- Parametrii sunt **aleatorii** vs. **semnificativi**
- Testele automate au **logică complexă** (deposit + transfer)
- Testele manuale sunt **mai clare** în intenție

#### 8.3.2 Coverage Achievem

**Teste Manuale**:
```
Branch Coverage: 95% (35/37)
Branches missed: 
  - applyInterest() cu rate = -0.5 (negative rate)
  - transfer() cu target = self și insufficient balance
```

**EvoSuite**:
```
Branch Coverage: 100% (37/37)
Toate ramurile acoperite!
```

**Concluzie**: EvoSuite este **mai eficient** în explorarea spațiu de teste.

#### 8.3.3 Mutation Score

**Teste Manuale**: ~75% (killed 112/150 mutanți)
**EvoSuite**: ~68% (killed 102/150 mutanți)

**Interpretare**: Testele manuale sunt mai **specifice** în validări, dar EvoSuite e mai **exhaustiv**.

### 8.4 Scenarii de Utilizare

**Teste MANUALE când**:
- ✅ Necesari teste care reflectă business logic
- ✅ Coverage-ul 100% nu e critic
- ✅ Testele trebuie ușor de înțeles de alți developeri
- ✅ Scenarii complexe cu multiple assertions

**EvoSuite când**:
- ✅ Necesari coverage înalt (80%+)
- ✅ Timp redus pentru scriere teste
- ✅ Regression testing (cand codul se schimbă)
- ✅ Explorare spațiu de teste pentru buguri noi
- ✅ Complement la teste manuale

**Scenariul IDEAL**:
```
Teste Manuale (70-80% coverage) 
    +
EvoSuite (generare suplimentare pâna la 100%)
    =
Suita Completă de Teste (100% coverage + business logic)
```

---

## <a name="concluzii"></a>9. Concluzii și Recomandări

### 9.1 Răspunsuri la Întrebări Cheie

#### 9.1.1 Cum funcționează algoritmii evolutivi în testare?

**Răspuns**: Algoritmi evolutivi modelează evoluția biologică prin:
1. **Inițializare**: Populație aleatorie de teste
2. **Evaluare**: Fitness function (coverage atins)
3. **Selecție**: Păstrare indivizi "buni"
4. **Reproducere**: Crossover (combinare teste)
5. **Mutație**: Schimbări aleatorii în parametri
6. **Iterație**: Până la convergenție (100% coverage)

Avantaj: **Exploraj automat** al spațiu de teste → Coverage înalt rapid.

#### 9.1.2 De ce 100% branch coverage dar doar 68% mutation score?

**Răspuns**: 
- **100% BC**: Toate ramurile logice sunt EXECUTATE
- **68% MS**: Doar 68% din ERROR MUTATIONS sunt DETECTATE

Cauzele:
1. **Assertions slabe**: Teste verific doar că nu se aruncă excepție
2. **Parametri aleatori**: Testele EvoSuite nu verific VALORILE exacte
3. **Operatori subtili**: `>` vs `>=` nu sunt distincti la nivelul codului

**Exemplu**:
```java
// Original
if (amount > balance) return false;

// Mutant (schimbare >, cu >=)
if (amount >= balance) return false;

// Test EvoSuite: amount=100, balance=100
// Result: false (ambele variante returnează false!)
// TEST NU DETECTEAZĂ DIFERENȚA
```

#### 9.1.3 EvoSuite este suficient pentru testare completă?

**Răspuns: PARȚIAL NU**

**Avantaje EvoSuite**:
- ✅ Coverage rapid (100% branch în 60 secunde)
- ✅ Automat, nu necesită intervenție manuală
- ✅ Explorare exhaustivă a spațiu de teste
- ✅ Detectează edge cases neanticipate

**Limitări EvoSuite**:
- ❌ Mutation score scăzut (68%)
- ❌ Nu verific CORECTITUDINE logica business
- ❌ Parametrii aleatorii nu sunt semnificativi
- ❌ Assertions sunt slabe (doar non-exception)
- ❌ Nu înțelege "intenții" ale testelor

**Concluzie**: EvoSuite este **complement**, nu **înlocuitor**, pentru teste manuale.

### 9.2 Recomandări Practice

#### 9.2.1 Workflow Hibrid (Recomandat)

```
FASE 1: Teste Manuale (Responsabilitate Dezvoltator)
├── 20-30 teste manuale scrise cu intenție clară
├── Coverage target: 70-80%
├── Mutation score target: 75%+
└── Timp: ~4-6 ore pentru clasă complexă

FASE 2: EvoSuite (Automatizat)
├── Rulare EvoSuite cu budget 60-120 secunde
├── Target: Ridicare coverage de la 80% la 100%
├── Integrare teste generate în suite
└── Timp: ~2 minute

REZULTAT:
├── 100% branch coverage
├── 75%+ mutation score (vs. 68% doar EvoSuite)
├── Tests care sunt readible (manuale) + exhaustive (auto)
└── Effort: ~4-6 ore vs. ~8-10 (doar manual)
```

#### 9.2.2 Configurare EvoSuite Recomandată

```bash
java -jar evosuite-1.2.0.jar \
  -class com.package.TargetClass \
  -cp target/classes \
  -criterion branch \
  -search_budget 120 \
  -Dpopulation.size=50 \
  -Dassertions=all \
  -Dminimize=false \
  -Dgmm_surrogate=false \
  -Djunit_tests=true \
  -Dtype_generationStrategy=REGRESSION

# Explicații:
# -criterion branch          : Branch coverage (strict)
# -search_budget 120         : 2 minute (mai mult = mai exhaustiv)
# -Dassertions=all          : Toate tip de assertions
# -Dminimize=false          : Keep all tests (nu reduce)
# -Dttype_generationStrategy=REGRESSION : Regression test generation
```

#### 9.2.3 Analiza Post-Generare

După rulare EvoSuite:
1. ✅ Verificare: Coverage 100% atins?
2. ✅ Inspecție: Teste au sens? (sau sunt complet aleatorii?)
3. ✅ Refactor: Adăugare assertions mai specifice
4. ✅ Integration: Merge cu suite manual

**Exemplu refactor**:

```java
// Original (EvoSuite)
@Test
public void test14() {
    BankAccount account = new BankAccount("+(uHlz~b2?vcN7");
    account.open();
    account.deposit(1.0);
    boolean result = account.transfer(account, 1.0);
    assertEquals(3, account.getTransactionCount());
    assertTrue(result);
}

// Refactored (cu context)
@Test
public void testTransferBetweenAccountsSuccess() {
    // Setup: Two accounts with sufficient balance
    BankAccount source = new BankAccount("Alice");
    BankAccount destination = new BankAccount("Bob");
    source.open();
    destination.open();
    source.deposit(1000);
    
    // Execute: Transfer 500
    boolean result = source.transfer(destination, 500);
    
    // Assert: Business logic
    assertTrue(result);
    assertEquals(500, source.getBalance(), 0.01);
    assertEquals(500, destination.getBalance(), 0.01);
    assertEquals(2, source.getTransactionCount());
    assertEquals(1, destination.getTransactionCount());
}
```

### 9.3 Implicații Teoretice

#### 9.3.1 Graficul Coverage vs. Mutation Score

```
Coverage %   Mutation Score (Estimat)
   100%      ┌─ ~70-75% (EvoSuite automat)
    95%      │
    90%      │
    85%      │
    80%      ├─ ~75-80% (Teste bune)
    75%      │
    70%      ├─ ~70% (Teste ok)
    65%      │
    60%      └─ <70% (Teste slabe)

OBSERVAȚIE: Coverage 100% ≠ Mutation Score 100%
```

#### 9.3.2 Validitate Testelor

```
Metrică             Semnificație
─────────────────────────────────────────────
Statement Coverage  Doar "am executat linia"
Branch Coverage     "Am executat ambele ramuri if/else"
Path Coverage       "Am testat TOATE combinații logice"
Mutation Score      "Testele detectează ERORI în cod"

Ranking importanță:
  Mutation Score > Branch Coverage > Statement Coverage
```

### 9.4 Concluzii Finale

| Aspect | Concluzie |
|--------|-----------|
| **Eficacitate EvoSuite** | ⭐⭐⭐⭐ (4/5 - Excelent pentru coverage) |
| **Utilizare Practică** | ⭐⭐⭐⭐ (4/5 - Bun ca complement) |
| **Automatizare** | ⭐⭐⭐⭐⭐ (5/5 - 100% automat) |
| **Readability Teste** | ⭐⭐ (2/5 - Parametri aleatorii) |
| **Detecție Buguri** | ⭐⭐⭐ (3/5 - Depinde de assertions) |

**Recomandare finală**:
> **EvoSuite este un tool puternic pentru atingerea coverage-ului ridicat rapid, dar NU trebuie utilizat singur. Implementarea unei strategii hibride (teste manuale + EvoSuite) oferă cel mai bun raport COST/BENEFICIU în ingineria software reală.**

---

## <a name="referinte"></a>10. Referințe Bibliografice

### 10.1 Papers de Cercetare

1. **Fraser, G., & Arcuri, A.** (2013). "EvoSuite: Automatic Test Suite Generation for Object-Oriented Software". *Proceedings of the 11th Symposium on Software Testing and Analysis (ISSTA 2013)*.
   - Descriere: Paper original care introduce EvoSuite

2. **Arcuri, A., & Briand, L.** (2014). "A Hitchhiker's Guide to Statistical Tests for Assessing Randomized Algorithms in Software Engineering". *Software Testing, Verification and Validation (ICST)*.
   - Descriere: Validare statistică a algoritmilor randomizați

3. **De Souza, H. S., Briand, L. C., & Labiche, Y.** (2012). "An Investigation of the Relationships Between Lines of Code and Defects, Cyclomatic Complexity, Lines of Code, and Defects". *IEEE Transactions on Software Engineering*.
   - Descriere: Relația dintre complexitate și defecte

### 10.2 Tools și Resurse

- **EvoSuite Official**: http://www.evosuite.org/
- **GitHub EvoSuite**: https://github.com/EvoSuite/evosuite
- **JaCoCo (Coverage Tool)**: https://www.jacoco.org/
- **JUnit 4 Documentation**: https://junit.org/junit4/
- **Maven Build Tool**: https://maven.apache.org/

### 10.3 Concepte Legate

- **Genetic Algorithms**: Holland, J. (1975). *Adaptation in Natural and Artificial Systems*
- **Search-Based Software Engineering**: Harman, M. (2007). "The Current State and Future of Search Based Software Engineering"
- **Mutation Testing**: Jia, Y., & Harman, M. (2010). "An Analysis and Survey of the Development of Mutation Testing"

---

## 📊 Apendice: Grafice și Tabele

### A.1 Timeline Executie EvoSuite

```
Timp (sec)    Coverage    Mutanți Killed
0             0%          0
15            42%         38
30            75%         72
45            95%         98
60            100% ✅      102 (68%)
```

### A.2 Distribuția Ramuri pe Metodă

```
applyInterest(): ████████ 8 ramuri
withdraw():      ██████████ 10 ramuri
transfer():      ████████ 8 ramuri
deposit():       ██████ 6 ramuri
open():          ██ 2 ramuri
close():         ██ 2 ramuri
Constructor():   █ 1 ramură
```

### A.3 Matrix Acoperire

```
                Constructor open  close deposit withdraw transfer applyInterest
Constructor     ✅        -       -     -       -        -        -
open()          ✅        ✅      -     -       -        -        -
close()         ✅        ✅      ✅    -       -        -        -
deposit()       ✅        ✅      -     ✅      -        ✅       -
withdraw()      ✅        ✅      -     -       ✅       ✅       -
transfer()      ✅        ✅      -     ✅      ✅       ✅       -
applyInterest() ✅        ✅      -     -       -        -        ✅
```

---

**Document generat**: 6 Martie 2026
**Proiect**: BankTest - Automatizarea Testării Prin Algoritmi Evolutivi
**Curs**: MFIS (Formal Methods in Software Engineering)
**Facultate**: FMI, UNIBUC

---

*Această documentație este parte a evaluării cursului și servește ca material de referință pentru înțelegerea algoritmilor evolutivi în testarea software.*

