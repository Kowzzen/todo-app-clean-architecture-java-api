# Clean Code

## 0. Introduction

A big part of this course comes from the two following books :

![Clean Code](https://lananas.cloud/s/ek8CrgwkXrej9wf/download "Clean Code")

![Clean Architecture](https://lananas.cloud/s/qsWYr82iwRpAPZW/download "Clean Architecture")

### Code smells

"Code smells are usually not bugs; they are not technically incorrect and do not prevent the program from functioning. Instead, they indicate weaknesses in design that may slow down development or increase the risk of bugs or failures in the future. Bad code smells can be an indicator of factors that contribute to technical debt"

- Rigidity. The software is difficult to change. A small change causes a cascade of subsequent changes.
- Fragility. The software breaks in many places due to a single change.
- Immobility. You cannot reuse parts of the code in other projects because of involved risks and high effort.
- Needless Complexity.
- Needless Repetition.
- Opacity. The code is hard to understand

#

![WTF per Minute](https://lananas.cloud/s/4gzmmxzJo2HdKAT/download "WTF per Minute")

### General rules

- Follow standard conventions
- Keep it simple stupid. Simpler is always better. Reduce complexity as much as possible.
- Boy scout rule. Leave the campground cleaner than you found it.
- Always look for the root cause of a problem.

### Disclaimers
- **There is no silver bullet**. Do not take all the principles you are going to learn as an absolute truth. Consider them as **guidelines**.
- **Pragmatism** should rule over applying "best practice" blindlessly

### SOLID Principles

#### Single Responsibility
There should never be more than one reason for a class to change.

#### Open/Closed Principle
Software entities should be open for extension, but closed for modification.

#### Liskov Substitution
Functions that use pointers or references to base classes must be able to use objects of derived classes without knowing it.

#### Interface Segregation
Clients should not be forced to depend upon interfaces that they do not use.

#### Dependency Inversion
Depend upon abstractions, not concretes.

A visual guide :

https://medium.com/backticks-tildes/the-s-o-l-i-d-principles-in-pictures-b34ce2f1e898

### A quick word about AI

- AI is the calculator of the 21th century. You will, in fact. always have one in your pocket.
- AI will be 100% accurate only when they understand 100% of the problems you are solving with your software. Until this happens, you have the final word on what is the most relevant code to write, considering the problems you are solving.

### If you had to remember one word from this course

`P R A G M A T I S M`

## 1. Understandability tips

### Be consistent : If you do something a certain way, do all similar things in the same way.

Do not do:

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

if (names.contains("Bob")) {
    System.out.println("Bob is in the list.");
}

if (names.indexOf("Charlie") != -1) {
    System.out.println("Charlie is in the list.");
}
```

Instead, do:

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

if (names.contains("Bob")) {
    System.out.println("Bob is in the list.");
}

if (names.contains("Charlie")) {
    System.out.println("Charlie is in the list.");
}
```

### Use explanatory variables.

Don't do :

```java
public boolean isElligibleForDiscount(LocalDateTime d) {
    LocalDateTime n = LocalDateTime.now();

    int a = (int) ChronoUnit.YEARS.between(d, n);

    boolean b = (a < 18 || a > 66);

    boolean b2 = d.getDayOfMonth() == n.getDayOfMonth() && d.getMonth() == n.getMonth();

    return b || b2;
}
```

Instead, do:

```java
public boolean isElligibleForDiscount(LocalDateTime customerBirthdate) {
    LocalDateTime today = LocalDateTime.now();

    int customerAge = (int) ChronoUnit.YEARS.between(customerBirthdate, today);

    boolean isJunior = customerAge < 18;
    boolean isSenior = customerAge > 66;

    boolean hasTheirBirthdayToday = customerBirthdate.getDayOfMonth() == today.getDayOfMonth() && customerBirthdate.getMonth() == today.getMonth();

    return isJunior || isSenior || hasTheirBirthdayToday;
}
```

### Encapsulate key conditions.

Conditions are hard to keep track of. Put the processing for them in one place.

Don't do:

```java
public double computeDiscount(double basePrice, Customer customer) {
    if (LocalDateTime.now().isEqual(LocalDateTime.parse("2025-28-11"))) {
        if (customer.getLoyaltyCardNumber().startsWith("GOLD")) {
            return computeBlackFridayDiscountForPremiumLoyaltyUser(basePrice);
        }
        else if (customer.getLoyaltyCardNumber().startsWith("STD")) {
            return computeBlackFridayDiscountForStandardLoyaltyUser(basePrice);
        }
        else {
            return computeBlackFridayDiscountForNoLoyaltyUser(basePrice);
        }
    }
    else {
        if (basePrice < 50) {
            return 0;
        }
        else if (basePrice < 100) {
            return 0.1 * basePrice;
        }
        else {
            return 0.25 * basePrice;
        }
    }
}
```

Instead, do:

```java
public double computeDiscount(double basePrice, Customer customer) {
    var todayIsBlackFriday = LocalDateTime.now().isEqual(LocalDateTime.parse("2025-28-11"));
    var customerHasGoldLoyaltyCard = customer.getLoyaltyCardNumber().startsWith("GOLD");
    var customerHasStandardLoyaltyCard = customer.getLoyaltyCardNumber().startsWith("STD");

    if (todayIsBlackFriday) {
        if (customerHasGoldLoyaltyCard) {
            return computeBlackFridayDiscountForPremiumLoyaltyUser(basePrice);
        }
        else if (customerHasStandardLoyaltyCard) {
            return computeBlackFridayDiscountForStandardLoyaltyUser(basePrice);
        }
        else {
            return computeBlackFridayDiscountForNoLoyaltyUser(basePrice);
        }
    }
    else {
        if (basePrice < 50) {
            return 0;
        }
        else if (basePrice < 100) {
            return 0.1 * basePrice;
        }
        else {
            return 0.25 * basePrice;
        }
    }
}
```

Note that the code above is still not perfect, but extracting and putting a name on some of the key conditions already makes it much more understandable.

### Prefer dedicated value objects to primitive type.

Do not do this:

```java
public class Program {

    public void ProcessCodePostal(int userInput) {
        
        String codeDepartement = StringUtils.leftPad(Integer.toString(userInput), 5, "0").substring(0, 2);

        if (codeDepartement.equals("92")) {
            System.out.println("Hauts de Seine");
        }
        else {
            System.out.println("Pas Hauts de Seine");
        }
    }
}
```

But this:

```java
public class Program {

    public void ProcessCodePostal(int userInput) {
        
        var codePostal = new CodePostal(userInput);

        if (codePostal.GetCodeDepartement().equals("92")) {
            System.out.println("Hauts de Seine");
        }
        else {
            System.out.println("Pas Hauts de Seine");
        }
    }
}

public class CodePostal {
    
    private final String value;

    public CodePostal(String value) {
        this.value = value.trim();
    }

    public CodePostal(int value) {
        super(StringUtils.leftPad(Integer.toString(value), 5, "0"));
    }

    public String GetValue() {
        return value;
    }

    public String GetCodeDepartement() {
        return value.substring(0, 2);
    }
}
```

### Avoid logical dependency.

Don't write methods which works correctly depending on something else in the same class.

Don't do:

```java
public class LogFile {
    private boolean isOpen = false;

    public void open() {
        // do some IO stuff
        isOpen = true;
    }

    public void writeLog(String text) {
        if (!isOpen) {
            throw new IllegalStateException("LogFile must be open first!");
        }
        // do some IO stuff
    }
}
```

Do:

```java
public class LogFile {
    public LogFileStream open() {
        return new LogFileStream();
    }
}

public class LogFileStream {
    public void writeLog(String text) {
        // do some IO stuff
    }
}
```

### Avoid negative conditionals.

```java
public class AudioFilePlayer {

    public void play(String audioFilePath) {

        boolean audioFileNotFound = false;
        AudioStream audioStream = null;

        try {
            audioStream = this.audioFileReader.loadAsStream(audioFilePath);
        } catch (IOException exception) {
            audioFileNotFound = true;
        }

        boolean soundIsDisabled = this.volumeSettings.isMuted || this.volumeSettings.volumeLevel == 0;

        if (!audioFileNotFound && !soundIsDisabled) {
            this.nativeMediaPlayer.playAdio(audioStream);
        }
    }
}
```

```java
public class AudioFilePlayer {

    public void play(String audioFilePath) {

        boolean audioFileFound;

        AudioStream audioStream = null;
        try {
            audioStream = this.audioFileReader.loadAsStream(audioFilePath);
            audioFileFound = true;
        } catch (IOException exception) {
            audioFileFound = false;
        }

        boolean isNotMuted = ! this.volumeSettings.isMuted;
        boolean soundIsEnabled = isNotMuted && this.volumeSettings.volumeLevel > 0;

        if (audioFileFound && soundIsEnabled) {
            this.nativeMediaPlayer.playAdio(audioStream);
        }
    }
}
```

### Reduce nesting

![Reduce nesting](https://miro.medium.com/v2/resize:fit:4800/format:webp/1*g4NuK2wpgB5hn_46bvzPmQ.png "Reduce nesting")


Become a Never Nester : https://www.youtube.com/watch?v=CFRhGnuXG-4

Turn this nest hell :

```cs
public void StartCar(Car car, CarKey key)
{
	if(!car.Started) {

		if(car.GasTank.Level > 0) {

			car.Brakes.Engage();

			if(TurnOnCar(car, key)) {

				car.GearStick.EnsureInPark();
				car.Ignition.Activate();
				car.HandBreak.Release();

				if(!car.IsAutomatic) {
					car.GearStick.MoveToNeautral(); 
					car.Clutch.Engage();
					car.GearStick.MoveToGear(1); 
					car.Accelerator.ApplyLowRevs();
					car.Clutch.StartReleasing();
					car.Clutch.Release();
					car.Accelerator.Engage();
				}
			}	
			else {
				throw new CarStartFailureException($"The given key {key} doesn't work for car {car}");
			}			
		}
		else {
			throw new CarStartFailureException("No gas");
		}
	}
}
```

Into this :

```cs   
public void StartCar(Car car, CarKey key)
{
	if(car.Started)
		return;

	if(car.GasTank.Level == 0)
		throw new CarStartFailureException("No gas");

	StartEngine(car);

	if(car.IsManual)
		StartManualInFirst(car);
}

public void StartEngine(Car car) {
	
	car.Brakes.Engage();

	if(!TurnOnCar(car, key))
		throw new CarStartFailureException($"The given key {key} doesn't work for car {car}");

	car.GearStick.EnsureInPark();
	car.Ignition.Activate();
	car.HandBreak.Release();
}

public void StartManualInFirst(Car car)
{
	car.GearStick.MoveToNeautral(); 
	car.Clutch.Engage();
	car.GearStick.MoveToGear(1); 
	car.Accelerator.ApplyLowRevs();
	car.Clutch.StartReleasing();
	car.Clutch.Release();
	car.Accelerator.Engage();
}
```

## 2. Names rules

### Choose descriptive and unambiguous names.

Make meaningful distinction. Use `moneyAmountInEuros` instead of `money` for example.

What do you think is the easiest to understand between this :

```cs
// c# 

public class Event
{
    public string Name { get; set; }
    
    public int Value { get; set; }
}

public class History 
{
    public readonly List<Event> Events = [];
    public int Start { get; set; }
    public int End { get; set; }
    
    public int Duration => End - Start;
    
    public Event? GetEvent()
    {
        return Events.MaxBy(e => e.Value);
    }
}
```

and this :

```cs
// c# 

public class HistoricalEvent
{
    public string Name { get; set; }
    
    public int FameLevel { get; set; }
}

public class HistoricalPeriod 
{
    public readonly List<HistoricalEvent> Events = [];
    public int StartYear { get; set; }
    public int EndYear { get; set; }

    public int DurationInYears => EndYear - StartYear;
    
    public HistoricalEvent? GetMostFamousEvent()
    {
        return Events.MaxBy(e => e.FameLevel);
    }
}
```

Here are some examples of **do's and don'ts** for each topic:  

### Use Pronounceable Names  
**Do:**  
- `customerName` instead of `cstmrNm`  
- `totalAmount` instead of `ttlAmt`  
- Reason: Pronounceable names make the code easier to read and discuss.  

**Don't:**  
- `xkcd` or `cntNbr`  
- Using abbreviations that are not universally recognized.  

### Use Searchable Names  
**Do:**  
- Use descriptive variable names like `userEmail`, `productPrice`, or `maxSpeed`.  
- Keep function names like `calculateDistance` instead of just `calc`.  
- Reason: Makes it easy to find occurrences in a large codebase.  

**Don't:**  
- Single-letter variable names like `x`, `y`, or `a`, especially for significant variables.  
- Using generic names like `temp`, `var`, or `data`.  

### Replace Magic Numbers with Named Constants  
**Do:**  
- `const int MAX_USERS = 100;`  
- `const double PI = 3.14159;`  
- Reason: Improves readability and makes it clear what the value represents.  

**Don't:**  
- Use raw numbers directly in the code, like `if (users > 100)`.  
- Scattering the same number throughout the code without explanation.  

### Avoid Encodings. Don't Append Prefixes or Type Information  
**Do:**  
- `userName` instead of `strUserName`  
- `totalCount` instead of `intTotalCount`  
- Reason: Type systems or IDEs usually provide type information, and adding prefixes makes the name longer and harder to read.  

**Don't:**  
- Hungarian notation like `strName`, `intAge`, or `btnSubmit`.  
- Prefixes or suffixes that indicate type or UI element (like `m_`, `btn`, or `lbl`).  

## 3. Functions rules

### Functions should be small and do one thing

Do not do this :

```cs
public void ProcessOrder(Order order)
{
    // Validate order
    if (order == null || order.Items.Count == 0)
    {
        Console.WriteLine("Invalid order.");
        return;
    }

    // Calculate total price
    decimal total = 0;
    foreach (var item in order.Items)
    {
        total += item.Price * item.Quantity;
    }

    // Apply discount if applicable
    if (order.IsPremiumCustomer)
    {
        total *= 0.9m; // 10% discount
    }

    // Log the order
    Console.WriteLine($"Order processed for {order.CustomerName} with total: {total:C}");

    // Update database
    Database.Save(order);
}
```

Do this instead:

```cs
public void ProcessOrder(Order order)
{
    if (!ValidateOrder(order)) return;

    decimal total = CalculateTotal(order);
    total = ApplyDiscountIfPremium(order, total);

    LogOrder(order.CustomerName, total);
    SaveOrder(order);
}

private bool ValidateOrder(Order order)
{
    if (order == null || order.Items.Count == 0)
    {
        Console.WriteLine("Invalid order.");
        return false;
    }
    return true;
}

private decimal CalculateTotal(Order order)
{
    return order.Items.Sum(item => item.Price * item.Quantity);
}

private decimal ApplyDiscountIfPremium(Order order, decimal total)
{
    return order.IsPremiumCustomer ? total * 0.9m : total;
}

private void LogOrder(string customerName, decimal total)
{
    Console.WriteLine($"Order processed for {customerName} with total: {total:C}");
}

private void SaveOrder(Order order)
{
    Database.Save(order);
}
```

### Functions should have descriptive names

```js
// C#

// DO NOT DO THIS
public int C(int a, int b)
{
    return a * b;
}

// DO THIS
public int CalculateArea(int width, int height)
{
    return width * height;
}
```

### Functions should have no side effect

... or rather ... they should have no **untold** side effect.

A function should fall in one of these 2 categories : 

- have **no side effect** and **returns something**
- have **side effect** and returns **nothing (or the result of the side effect)**

A function which has a side effect AND returns something should have a perfectly clear naming of what it does.

See for examples : 

```cs
// C#

public class ShoppingCart 
{
	private List<CartItem> _items;

	// returns the result of the side effect. Could also return void.
	public CartItem AddItem(Product product, uint quantity)
	{
        var item = new CartItem(item, quantity);
    	_items.Add(item);

        return item;
	}

	// Pure method
	public CartItem? GetItem(ProductCode productCode)
	{
    	return _items.FirstOrDefault(item => item.Product.Code == productCode)
	}
    
    // the name of this method makes clear that it MAY have a side effect.
	public CartItem GetOrAddItem(ProductCode productCode, uint quantityIfAdd)
	{
        return GetItem(productCode) ?? AddItem(productCode, quantityIfAdd);
	}
}
```

## 4. Comments rules

## General Principle
Comments are an essential part of writing clear and maintainable code. However, they should be used wisely and purposefully. The goal is to make your code self-explanatory, not cluttered. Use comments to explain why, not what.

## Always try to explain yourself in code.
Well-written code should be as self-explanatory as possible. Instead of relying on comments to explain your logic, write your code in a way that it’s clear and intuitive.

**Bad Example:**
```cs
// All players start with 100 HP
var hp = 100;

// the damage of a player is the sum of their dexterity and their strength scores
var dmg = dx + str;
```

**Good Example:**
```csharp
var healthPoints = GameConstants.DefaultHealthPoints;

var playerDamage = dexterityScore + strengthScore;
```

Instead of writing comments that explain simple operations, choose meaningful variable names and structure the code to be self-explanatory.

### Don't be redundant.
Avoid comments that just repeat what the code itself clearly states.

**Bad Example:**
```csharp
int age = 25;  // Set age to 25
```
**Good Example:**
```csharp
int age = 25;
```

###  Don't add obvious noise.
Comments that state the obvious just clutter the code.

**Bad Example:**
```csharp
// Return true if the user is authenticated
return isAuthenticated;
```

### Don't comment out code. Just remove.
If the code is not needed, delete it. Version control exists to recover it if needed.

**Bad Example:**
```csharp
// int x = 10; // Unused variable
```

### Use comments as explanations of intent.
Sometimes, the reason behind a piece of code might not be obvious. Use comments to explain the intent, especially when it’s not straightforward.

**Good Example:**
```csharp
// Using binary search beacause it proved to give better performance in most cases
int index = BinarySearch(array, value);
```

### Use comments as clarification of code.
If the logic is complex or not immediately clear, briefly explain what’s happening.

**Good Example:**
```csharp
// Adjusting for zero-based index
int position = userInput - 1;
```

### Use comments as a warning of consequences.
Warn about potential pitfalls or important considerations.

**Good Example:**
```cs
// Be careful: setting the limit higher than 1000 makes the client throw an exception
var inboxMails = superMailClient.FetchLatestInboxMails(userId, limit: 1_000);
```

### Summary
The main rule with comments is to be intentional. Avoid clutter, and use comments where they add value by explaining intent, clarifying complex logic, or warning about consequences.

## 5. Design Rules

### General Principle
Good design practices make code more modular, maintainable, and scalable. Following these principles leads to clearer, more robust software.

###  Keep configurable data at high levels
Configuration should be managed at a higher level in the system, keeping it separate from the core logic.

**Bad Example:**
```csharp
public void Connect()
{
    string connectionString = "Server=myServer;Database=myDB;";
    Database.Connect(connectionString);
}
```
**Good Example:**
```csharp
public void Connect(string connectionString)
{
    Database.Connect(connectionString);
}
```
- The good example makes the connection string configurable from outside, promoting flexibility.

#### Prefer polymorphism to if/else or switch/case
Instead of using conditionals to decide behavior, use polymorphism to let objects decide how to behave.

**Bad Example:**
```csharp
if (shape == "circle") DrawCircle();
else if (shape == "square") DrawSquare();
```
**Good Example:**
```csharp
Shape shape = new Circle();
shape.Draw();
```
- Polymorphism makes it easy to add new shapes without modifying existing code.

### Separate multi-threading code
Multi-threading concerns should be isolated to reduce complexity and improve maintainability.

**Bad Example:**
```csharp
public void Process()
{
    Task.Run(() => DoWork());
    Log("Task started");
}
```
**Good Example:**
```csharp
public void StartTask()
{
    Task.Run(DoWork);
}

private void DoWork()
{
    Log("Task started");
}
```
- This separation makes multi-threaded code more readable and manageable.

### Prevent over-configurability
Too many configuration options can overwhelm and complicate the system.

**Bad Example:**
```csharp
if (config.Mode == "A") DoA();
else if (config.Mode == "B") DoB();
else if (config.Mode == "C") DoC();
```
**Good Example:**
```csharp
Action action = GetAction(config.Mode);
action();
```
- This approach centralizes configuration handling.

### Use dependency injection
Dependencies should be passed in rather than hard-coded, improving testability and modularity.

**Bad Example:**
```csharp
public class UserService
{
    private Database db = new Database();
}
```
**Good Example:**
```csharp
public class UserService
{
    private readonly IDatabase db;

    public UserService(IDatabase database)
    {
        db = database;
    }
}
```
- This promotes loose coupling and makes the class more testable.

### Follow Law of Demeter
A class should only interact with its immediate dependencies, not chain calls through multiple objects.

**Bad Example:**
```csharp
invoice.GetLastLine().ApplyDiscount(Discount.MinusPercent(10));
```
**Good Example:**
```csharp
invoice.ApplyDiscountToLastLine(Discount.MinusPercent(10));
```
- This reduces the risk of "skipping" business rules. Here for example, applying a discount to an invoice line might trigger the recomputing of the total value. The computing would be skipped if the invoice line was modified directly.

## 6. Objects and Data Structures

### Hide Internal Structure
One of the core principles of OOP is encapsulation, which means hiding internal data and exposing only what is necessary. This helps to protect the internal state from unintended modifications.

**Example:**
```csharp
class BankAccount
{
    private decimal balance;

    public void Deposit(decimal amount)
    {
        if (amount > 0)
        {
            balance += amount;
        }
    }

    public decimal GetBalance() => balance;
}
```
**Explanation:** The `balance` field is private, and the `Deposit` method ensures that the amount is valid before updating the balance, protecting the integrity of the object.

### Keep Objects Small
Smaller objects are easier to test and understand. They should encapsulate only a few related fields and methods.

**Example:**
```csharp
class Address
{
    public string Street { get; set; }
    public string City { get; set; }
    public string ZipCode { get; set; }
}
```
**Explanation:** The `Address` class only contains fields related to the address, keeping it focused and manageable.

### Do One Thing
An object should represent a single concept or responsibility. This adheres to the Single Responsibility Principle (SRP).

**Example:**
```csharp
class Logger
{
    public void LogError(string message) => Console.WriteLine($"Error: {message}");
    public void LogInfo(string message) => Console.WriteLine($"Info: {message}");
}
```
**Explanation:** The `Logger` class is responsible only for logging messages, not processing or formatting them.

### Base Class Ignorance
Base classes should not know about their derived classes. This follows the principle of polymorphism.

**Example:**
```csharp
abstract class Shape
{
    public abstract double CalculateArea();
}

class Circle : Shape
{
    public double Radius { get; set; }
    public override double CalculateArea() => Math.PI * Radius * Radius;
}
```
**Explanation:** The `Shape` base class doesn't know about `Circle`, allowing extensibility.

### Prefer Multiple Functions
Avoid using conditional logic within a single method to handle different behaviors. Separate methods increase clarity.

**Example:**
```csharp
class ReportGenerator
{
    public void GeneratePdf() => Console.WriteLine("Generating PDF report...");

    public void GenerateCsv() => Console.WriteLine("Generating CSV report...");
}
```
**Explanation:** Splitting functionality makes each method purpose clear, rather than using a single method with conditionals.

## About Architecture
### N-tier architecture (and why it's bad)
![Three Tier Architecture](https://lananas.cloud/s/Y2GF3AdMSSJmgyz/download "Three Tier Architecture")

- Rigid
- Highly coupled
- Untestable

Do. Not. Do. This. (Except if you have good reasons to)

### Onion Architecture (and why it's better)
![Onion Architecture](https://lananas.cloud/s/PJG3MEtFCkWqqLs/download "Onion Architecture")

This schema is an example of an Onion Architecture. However, note that the following three layers are almost always present in such an architecture :

- **Domain Layer**: Contains the core logic, entities and rules of the domain that your code is modeling.
- **Application Layer**: Implements use cases of the product.
- **Infrastructure Layer**: Handles external concerns such as databases, file systems, external services and presentation.

### Hexagonal architecture (and why it is usually the best)
![Hexagonal Architecture](https://lananas.cloud/s/rcNj9G4LpRcy5Bm/download "Hexagonal Architecture")

#### Ports 

A port is a consumer agnostic entry and exit point to/from the application. In many languages, it will be an interface. For example, it can be an interface used to perform searches in a search engine. In our application, we will use this interface as an entry and/or exit point with no knowledge of the concrete implementation that will actually be injected where the interface is defined as a type hint.  

#### Adapters
An adapter is a piece of code (let's say a Class) that transforms (adapts) an interface into another.

#

There are 2 kinds of adapters :
- **Inbound adapters** : Adapters that call the application. Also called "Primary" or "Driving" adapters. They are usually represented on the left side of the Hexagon.
- **Outbound adapters** : Adapters that are called by the application. Also called "Secondary" or "Driven" adapters. They are usually represented on the right side of the Hexagon.

Hexagonal architecture is also called "Ports and Adapters" architecture.

Hexagonal Architecture makes the code :
- Highly flexible
- Modular
- Testable

![Hexagonal Architecture Inbound vs Outbound](https://lananas.cloud/s/z2ZTXAoFPWXFo5H/download "Hexagonal Architecture Inbound vs Outbound")

Note that no matter the direction of the **Flow of Control**, the Source code **dependency always points inwards**. 

![Hexagonal vs Onion](https://lananas.cloud/s/CxyMW7oPX9oxkkN/download "Hexagonal vs Onion")

Some good reads about hexagonal Architecture :

https://sd.blackball.lv/articles/read/19658-hexagonal-architecture-what-is-it-why-should-you-use-it

https://herbertograca.com/2017/09/14/ports-adapters-architecture

## Tests

### What is a unit ?

Ask 100 developers what is their definition of a unit in "Unit testing" and you will get 100 different answers.

"From the system requirements perspective only the perimeter of the system is relevant, thus only entry points to externally visible system behaviours define units."

-- Wikipedia (Beck, Kent (2002). Test-Driven Development by Example. Addison-Wesley)

So in short, a unit is whatever you decide it to be as long as it is a cohesive part of the sytem.

A test that depends upon factors external to the system are not unit tests but integration tests, which this course does not intend to cover.

### What makes a test a good test ?

A good unit test is a test that :

- asserts only one thing
- is readable.
- runs fast.
- is independant of the other tests.
- is repeatable.
- test behaviours, not implementations.

### Test Driven Development (TDD)


![TDD](https://lananas.cloud/s/S7MF4Kx4sokMbf2/download "TDD")

**Red**: Start by writing a unit test for one core element of the proposed functionality. **You expect the test to fail**.

**Green**: Write **just enough code to pass the test**. 

**Blue**: Refactor newly implemented code to ensure it is as clean as possible.