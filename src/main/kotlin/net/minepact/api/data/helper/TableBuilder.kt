package net.minepact.api.data.helper

class TableBuilder(private val name: String) {
    private val columns = mutableListOf<DatabaseColumn>()

    fun column(
        name: String,
        type: DataType,
        primaryKey: Boolean = false,
        autoIncrement: Boolean = false,
        nullable: Boolean = false,
        defaultValue: Any? = null
    ): TableBuilder {
        columns.add(
            DatabaseColumn(
                name,
                type,
                primaryKey,
                autoIncrement,
                nullable,
                defaultValue
            )
        )
        return this
    }

    fun build(): DatabaseTable {
        return DatabaseTable(name, columns)
    }
}