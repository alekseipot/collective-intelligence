package com.docler.examles.chapter2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class KeyScore {
    String key;
    Double score;

    @Override
    public String toString() {
        return "('" +
                key + "'" +
                ", " + score +
                ')';
    }
}
