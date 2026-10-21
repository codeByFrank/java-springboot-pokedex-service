# Pokédex Service — Java & Spring Boot

Ein Lernprojekt aus dem Java-Bootcamp bei neue fische.

Die Anwendung lädt Pokémon-Daten aus der PokéAPI und verwaltet
eine Favoriten-Sammlung in MongoDB.

## Technologien

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC und RestClient
- Spring Data MongoDB
- Spring Validation
- Maven
- JUnit, Mockito und MockMvc

## Funktionen

- Pokémon anhand ihres Namens abrufen
- Favoriten mit eigener UUID und Nickname speichern
- Alle Favoriten oder einen einzelnen Favoriten abrufen
- Nicknames ändern
- Favoriten löschen
- Eingaben validieren
- Fehler zentral mit Meldung und Zeitstempel zurückgeben

Beim Abrufen, Bearbeiten und Löschen gespeicherter Favoriten
werden ausschließlich Daten aus MongoDB verwendet.

## Voraussetzungen

- JDK 25
- Ein laufender MongoDB-Server
- Internetverbindung für den Zugriff auf die PokéAPI

Maven wird über den enthaltenen Maven Wrapper ausgeführt.

## Konfiguration

Die Datei `src/main/resources/application.properties` enthält
für eine lokale MongoDB ohne Anmeldung:

```properties
spring.application.name=java-springboot-pokedex-service
spring.mongodb.uri=mongodb://localhost:27017/pokedex
```

Die Favoriten werden in der Datenbank `pokedex` in der
Collection `favorites` gespeichert.

## Anwendung starten

Unter Windows im Projektordner:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternativ kann die Hauptklasse
`JavaSpringbootPokedexServiceApplication` in IntelliJ gestartet werden.

Die Anwendung ist unter `http://localhost:8080` erreichbar.

## Build und Tests

Alle Tests ausführen:

```powershell
.\mvnw.cmd test
```

Projekt vollständig bauen und prüfen:

```powershell
.\mvnw.cmd clean verify
```

Unter macOS/Linux wird statt `.\mvnw.cmd` der Befehl `./mvnw` verwendet.

Die Service-Tests verwenden Mocks beziehungsweise nachgestellte
API-Antworten. Die Controller-Tests verwenden MockMvc.

## Endpunkte

| Methode | Pfad | Funktion | Erfolg |
| --- | --- | --- | --- |
| GET | `/api/pokemon/{name}` | Pokémon aus der PokéAPI abrufen | 200 |
| POST | `/api/collection` | Favoriten speichern | 201 |
| GET | `/api/collection` | Alle Favoriten abrufen | 200 |
| GET | `/api/collection/{id}` | Einzelnen Favoriten abrufen | 200 |
| GET | `/api/pokemon/random` | Zufälliges Pokémon aus den ersten 151 abrufen | 200 |
| PUT | `/api/collection/{id}` | Nickname ändern | 200 |
| DELETE | `/api/collection/{id}` | Favoriten löschen | 204 |

`{id}` ist die UUID des gespeicherten Favoriten.
`pokemonId` bezeichnet dagegen die ursprüngliche ID aus der PokéAPI.

Beim reinen Pokémon-Abruf sind `id` und `nickname` noch `null`.

## Beispiele

### Pokémon abrufen

```http
GET http://localhost:8080/api/pokemon/pikachu
```

### Favoriten anlegen

```http
POST http://localhost:8080/api/collection
Content-Type: application/json
```

```json
{
  "pokemonName": "pikachu",
  "nickname": "Mein Starter"
}
```

### Nickname ändern

Ersetze `{id}` durch die UUID aus der POST-Antwort.

```http
PUT http://localhost:8080/api/collection/{id}
Content-Type: application/json
```

```json
{
  "nickname": "Arena-Champion"
}
```

## Validation

- Der Pokémon-Name darf nicht fehlen, leer sein oder nur Leerzeichen enthalten.
- Der Nickname darf nicht fehlen, leer sein oder nur Leerzeichen enthalten.
- Der Nickname muss mindestens zwei Zeichen enthalten.

Ungültige Eingaben werden mit `400 Bad Request` abgewiesen.

## Fehlerbehandlung

Ein globaler Exception Handler behandelt:

- `PokemonNotFoundException`: Pokémon nicht gefunden → 404
- `CollectionEntryNotFoundException`: Favorit nicht gefunden → 404
- `MethodArgumentNotValidException`: Eingabe ungültig → 400

Beispiel:

```json
{
  "message": "Favorite with id 'unknown-id' not found",
  "timestamp": "2026-10-21T08:00:00Z"
}
```

## Bonusaufgaben

- Zufälliges Pokémon: `GET /api/pokemon/random` liefert ein
  zufällig ausgewähltes Pokémon aus den ersten 151 Pokémon.
  Der Abruf speichert keinen Favoriten.

- Favoriten durchsuchen: Suche nach Teilen des Pokémon-Namens
  oder Nicknames, unabhängig von Groß- und Kleinschreibung.
  Endpoint: `GET /api/collection/search?name=pika`

## Datenquelle

Die Pokémon-Daten und Bild-URLs stammen aus der
[PokéAPI](https://pokeapi.co/).