package com.docler.examles;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
A dictionary of movie critics and their ratings of a small set of movies
 */
public class Recommendations {
    public static final Map<String, Map<String, Double>> recommendations = Map.of(
            "Lisa Rose",
            Map.of("Lady in the Water", 2.5,
                    "Snakes on a Plane", 3.5,
                    "Just My Luck", 3.0,
                    "Superman Returns", 3.5,
                    "You, Me and Dupree", 2.5,
                    "The Night Listener", 3.0),
            "Gene Seymour",
            Map.of("Lady in the Water", 3.0,
                    "Snakes on a Plane", 3.5,
                    "Just My Luck", 1.5,
                    "Superman Returns", 5.0,
                    "You, Me and Dupree", 3.5,
                    "The Night Listener", 3.0),
            "Michael Phillips",
            Map.of("Lady in the Water", 2.5,
                    "Snakes on a Plane", 3.0,
                    "Superman Returns", 3.5,
                    "The Night Listener", 4.0),
            "Claudia Puig",
            Map.of("Snakes on a Plane", 3.5,
                    "Just My Luck", 3.0,
                    "Superman Returns", 5.0,
                    "You, Me and Dupree", 2.5,
                    "The Night Listener", 4.5),
            "Mick LaSalle",
            Map.of("Lady in the Water", 3.0,
                    "Snakes on a Plane", 4.0,
                    "Just My Luck", 2.0,
                    "Superman Returns", 3.0,
                    "You, Me and Dupree", 2.0,
                    "The Night Listener", 3.0),
            "Jack Matthews",
            Map.of("Lady in the Water", 3.0,
                    "Snakes on a Plane", 4.0,
                    "Just My Luck", 2.0,
                    "Superman Returns", 3.0,
                    "You, Me and Dupree", 2.0,
                    "The Night Listener", 3.0),
            "Toby",
            Map.of("Snakes on a Plane", 4.5,
                    "Superman Returns", 4.0,
                    "You, Me and Dupree", 1.0)
    );


    public static List<PersonScore> topMarchers(String person, int topN, String simularity) {
        List<PersonScore> scores =
                recommendations.keySet().stream().filter(personFromDataSet -> !personFromDataSet.equals(person))
                        .map(personFromDataSet -> {
                            if ("Euclidean".equals(simularity)) {
                                return new PersonScore(personFromDataSet, EuclideanDistanceScore.getSimilarityDistance(recommendations, person, personFromDataSet));

                            } else {
                                return new PersonScore(personFromDataSet, PearsonCorrelationScore.getSimilarityDistance(recommendations, person, personFromDataSet));
                            }
                        }).sorted((e1, e2) -> e2.getScore().compareTo(e1.getScore())).collect(Collectors.toList());

        return scores.subList(0, topN);
    }

    public static void main(String[] args) {
        System.out.println("=========================PearsonCorrelationScore=========================");
        System.out.println("\"Toby\" top 3 recomenders: " + topMarchers("Toby", 3, "Pearson"));
        System.out.println("=========================PearsonCorrelationScore=========================");

    }

    @Data
    @AllArgsConstructor
    static
    class PersonScore {
        String person;
        Double score;

        @Override
        public String toString() {
            return "(" +
                    person + '\'' +
                    ", " + score +
                    ')';
        }
    }
}
