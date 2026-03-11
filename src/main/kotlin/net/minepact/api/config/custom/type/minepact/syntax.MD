# MinePact Custom Config Syntax

---
## Paths & Nested Values
### Methods
1. Using dots: `database.port: 3306`
2. Using tabs: 
```yaml
database:
     name: MinePact
     port: 3306
```
### Code Access
**Note**: *The data type of the `file.reader.get<DataType>(PATH)` will be default as a String. If it is changed, then an attempt to cast the value or parse the value will be made.*
```kotlin
val file: MinePactFile = ConfigManager.file<MinePactConfigType>("config.mpc")
val port: Int = file.reader.get<Int>("database.port")
```

---

## Lists & Maps
### Lists
**Note**: *Every value in a list **MUST** be of the same data type for the list to function!*
```yaml
servers:
  - Server1
  - Server2
```

```yaml
servers: [Server1, Server2]
```

### Maps
```yaml
servers: [{"Server1", 25565}, {"Server2", 25566}]
```
### Code Access
**Note**: *The DataType must be parameterized for the parsing to work. The data in the config file must also match the given data types, or a (`ConfigListTypeMismatchException` | `ConfigMapTypeMismatchException`) will occur.*
```kotlin
val file: MinePactFile = ConfigManager.file<MinePactConfigType>("config.mpc")
val servers: Map<String, Int> = file.reader.get<Map<String, Int>>("servers")
```
---
## Data Type Declaration
```yaml
name1: 1234 # Integer
name2: "1234" # String
name3: 12.34 # Double
name4: true # Boolean
name5: 13.0f # Float

name6: [1, 2, 3] # List<Int>
name7: # List<Int>
  - 1
  - 2
  - 3

name8: [{"key1", "value1"}, {"key2", "value2"}] # Map<String, String>
```