package com.ubb.minisgbdserver.dto;

public class TableDTO {

    private String databaseName;
    private String tableName;
    private String fields;

    public TableDTO(String databaseName, String tableName, String fields) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.fields = fields;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "TableDTO{" +
                "databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", fields='" + fields + '\'' +
                '}';
    }
}
