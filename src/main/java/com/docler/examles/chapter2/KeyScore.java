package com.docler.examles.chapter2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyScore {
    private String key;
    private Double score;

    @Override
    public String toString() {
        return "('" +
                key + "'" +
                ", " + score +
                ')';
    }
}
