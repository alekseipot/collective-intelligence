package demo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class EuclideanDistanceScore {
    /**
     * @return values from 1 to 0. Where 1 means that people have identical preferences
     */
    public static double getSimilarityDistance(Map<String, Map<String, Double>> recommendations, String person1, String person2) {
        Map<String, Double> scoresPerson1 = recommendations.get(person1);
        Map<String, Double> scoresPerson2 = recommendations.get(person2);
        Set<String> sameMoveScores = scoresPerson1.keySet().stream().filter(scoresPerson2::containsKey).collect(Collectors.toSet());
        //return 0 if they have no rating in common
        if (sameMoveScores.isEmpty()) {
            return 0;
        }
        return 1 / (1 + getDistance(recommendations, sameMoveScores, person1, person2));
    }

    private static double getDistance(Map<String, Map<String, Double>> recommendations, Set<String> sameMoveScored, String person1, String person2) {
        Double sumOfSquares =
                sameMoveScored.stream().map(movie ->
                        pow(recommendations.get(person1).get(movie) - recommendations.get(person2).get(movie), 2)).reduce(0.0, Double::sum);
        return sqrt(sumOfSquares);
    }

    public static void main(String[] args) {
        System.out.println("=========================EuclideanDistanceScore=========================");
        System.out.println("\"Gene Seymour\" and \"Aleksei\" Distance: " + getSimilarityDistance(demo.Recommendations.personToMovie, "Aleksei", "Gene Seymour"));
        System.out.println("\"Lisa Rose\" and \"Aleksei\" Distance: " + getSimilarityDistance(demo.Recommendations.personToMovie, "Lisa Rose", "Aleksei"));
        System.out.println("\"Mick LaSalle\" and \"Aleksei\" Distance: " + getSimilarityDistance(Recommendations.personToMovie, "Mick LaSalle", "Aleksei"));
        System.out.println("=========================EuclideanDistanceScore=========================");
    }
}
