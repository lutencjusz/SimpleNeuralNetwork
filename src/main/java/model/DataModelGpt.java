package model;

import lombok.Getter;

@Getter
public class DataModelGpt {
    DataModel[] messages;

    public DataModelGpt(DataModel[] messages) {
        this.messages = messages;
    }
}
