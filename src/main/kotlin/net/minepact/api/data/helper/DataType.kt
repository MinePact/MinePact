package net.minepact.api.data.helper

enum class DataType(val rep: String) {
    STRING("VARCHAR(255)"),
    TEXT("TEXT"),

    INT("INT"),
    LONG("BIGINT"),
    DOUBLE("DOUBLE"),
    FLOAT("FLOAT"),

    BOOLEAN("BOOLEAN"),

    UUID("CHAR(36)"),

    TIMESTAMP("TIMESTAMP"),
    DATETIME("DATETIME"),

    JSON("JSON");
}
