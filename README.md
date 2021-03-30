# Food-Coop-Warehouse

Food-coop warehouse management software.
## Installing, starting and stopping the application

Food-coop warehouse comes prepackaged with its maven wrapper. Go to the project directory.<br>
Building the app:
```
mvnw clean install
```
Starting the app:
```
mvnw -pl plugins spring-boot:run
```
Stopping the app: 
```
ctrl c
```
___

##Domain Driven Design

Domain Driven Design (DDD) describes an approach to model complex software. The term was coined by Eric Evans in 2004 
[[1]](#1). A domain model is collaboratively extracted from the business domain by software engineers and domain 
experts. The model maps core concepts and relationships in the domain language.<br>
This guide uses the terminology as defined in Eric Evans' "Domain-driven design reference" [[2]](#2).

### Ubiquitous language

A food-coop warehouse contains defined amounts (**Menge**) of goods (**Produkt**), largely foodstuff. Each *Produkt* is
of a specific **Kategorie** (literally category), e.g. meat or vegetable. The current stock (**istLagerbestand**) of
each *Produkt* and the current target stock (**sollLagerbestand**) can be set by the buyer (**Einkäufer**). The *
Einkäufer* can also define new amounts (**Menge**, e.g. kg, liters), new kinds of *Produkt* and newkinds of  *Kategorie*
of goods.
---

### Domain model terms

**Produkt**<br>
Product: Individual products of wares in the warehouse. A *Produkt* belongs to a
*Kategory*, has an *istLagerbestand* and a *sollLagerbestand*.

**Menge**<br>
Amount: The specific count, weight or volume (depending on the Produkt in question), that the *Produkt* is measured in,
and its *Einheit* (unit of measurement).

**Einheit**<br>
The unit of measurement of a *Menge*.

**Kategorie**<br>
Specific category of *Produkt*. For example, the warehouse might be sorted into meat, vegetables, noodles, grains, etc.

**Lagerbestand**<br>
The **istLagerbestand** amount of a *Produkt* in the warehouse.<br>
The **sollLagerbestand** amount of a *Produkt* in the warehouse.
---

#### Roles

**Rollen**<br>
Roles of food coop members.

**Einkäufer**<br>
Buyer: keeps the warehouse stocked by buying from farmers and wholesaler.

---

#### Use cases

**Ansicht Lagerbestand**<br>
A complete view of the all stock for the convenice of the buyer (**Einkäufer**). This REST-API provides the current
stock (**istLagerbestand**) and target stock
(**sollLagerbestand**) of each product (**Produkt**) sorted by category
(**Kategorie**) and product as a JSON. It is on the client to create a suitable table to view the information,
preferably including the option to collapse categories.

**Externe Bestellungsliste**<br>
The **Einkäufer** gets a list of all products(**Produkte**) with current stock levels(**istLagerbestand**) below target
stock levels (**sollLagerbestand**), and the amount(**Menge**) that is missing. The list infomration is encoded in JSON
and is supposed to be turned into a PDF document by the client.

---

### Bounded context

---

### Terminology of large-scale structure

---

### Patterns

---

## API

---

### Kategorie

This API allows a consumer to get information on all *Kategorien* with all their
*Produkte* and their respective *Lagerbestand* in one JSON (in production the information will depend on the
role via authentication). Try it yourself:

```
curl <address>/kategorien
``` 

---

## References
<a id="1">[1]</a>
Evans, Eric. Domain-driven design: tackling complexity in the heart of software. 
Addison-Wesley Professional, 2004.<br>
<a id="2">[2]</a>
Evans, Eric. "Domain-driven design reference." Definitions and Pattern Summaries. März (2015).