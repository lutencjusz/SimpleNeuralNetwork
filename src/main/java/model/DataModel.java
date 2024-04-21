package model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class DataModel {
    String role;
    String content;

    public DataModel(String role, double[] content) {
        this.role = role;
        this.content = Arrays.toString(content);
    }

    public DataModel(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
