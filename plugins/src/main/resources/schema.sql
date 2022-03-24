CREATE TABLE einheit(
                        id VARCHAR(50) PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
);

CREATE TABLE lagerkategorie(
                               id VARCHAR(50) PRIMARY KEY,
                               name VARCHAR(50) NOT NULL,
                               icon VARCHAR(10000) NOT NULL
);

CREATE TABLE lagerprodukt(
                             id VARCHAR(50) PRIMARY KEY,
                             name VARCHAR(50) NOT NULL,
                             lagerkategorie_id VARCHAR(50) references lagerkategorie(id),
                             einheit_id VARCHAR(50) references einheit(id),
                             ist_lagerbestand VARCHAR(50) NOT NULL,
                             soll_lagerbestand VARCHAR(50) NOT NULL
);

CREATE TABLE person(
                       id VARCHAR(50) PRIMARY KEY,
                       vorname VARCHAR(50) NOT NULL,
                       nachname VARCHAR(50) NOT NULL
);

CREATE TABLE brot(
                     id VARCHAR(50) PRIMARY KEY,
                     name VARCHAR(50) NOT NULL,
                     gewicht INT NOT NULL,
                     preis FLOAT NOT NULL
);

CREATE TABLE brotbestellung(
                               id VARCHAR(50) NOT NULL,
                               brot_id VARCHAR(50) references brot(id),
                               person_id VARCHAR(50) references person(id),
                               datum DATE NOT NULL,
                               PRIMARY KEY(brot_id, person_id, datum)
);

CREATE TABLE frischbestand(
                              id VARCHAR(50) PRIMARY KEY,
                              name VARCHAR(50) NOT NULL,
                              verfuegbarkeit BOOLEAN NOT NULL,
                              herkunftsland VARCHAR(50) NOT NULL,
                              gebindegroesse INT NOT NULL,
                              einheit_id VARCHAR(50) references einheit(id),
                              preis FLOAT NOT NULL
);

CREATE TABLE frischbestellung(
                                 id VARCHAR(50) NOT NULL,
                                 frischbestand_id VARCHAR(50) references frischbestand(id),
                                 person_id VARCHAR(50) references person(id),
                                 datum DATE NOT NULL,
                                 PRIMARY KEY(frischbestand_id, person_id, datum)
);