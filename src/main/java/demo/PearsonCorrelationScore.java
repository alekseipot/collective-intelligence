package demo;

import demo.Recommendations;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class PearsonCorrelationScore {
    /**
     * @return values from 1 to 0. Where 1 means that people have identical preferences
     */
    public static double getSimilarityDistance(Map<String, Map<String, Double>> recommendations, String person1, String person2) {
        Map<String, Double> scoresPerson1 = recommendations.get(person1);
        Map<String, Double> scoresPerson2 = recommendations.get(person2);
        Set<String> sameMoveScored = scoresPerson1.keySet().stream().filter(scoresPerson2::containsKey).collect(Collectors.toSet());
        //return 0 if they have no rating in common
        if (sameMoveScored.isEmpty()) {
            return 0;
        }

        //Add up all the preferences
        double sum1 = sameMoveScored.stream().map(scoresPerson1::get).reduce(0.0, Double::sum);
        double sum2 = sameMoveScored.stream().map(scoresPerson2::get).reduce(0.0, Double::sum);

        //Sum up the squares
        double sum1Sq = sameMoveScored.stream().map(movie -> pow(scoresPerson1.get(movie), 2)).reduce(0.0, Double::sum);
        double sum2Sq = sameMoveScored.stream().map(movie -> pow(scoresPerson2.get(movie), 2)).reduce(0.0, Double::sum);

        //Sum up the products
        double sumP = sameMoveScored.stream().map(movie -> scoresPerson1.get(movie) * scoresPerson2.get(movie)).reduce(0.0, Double::sum);

        //Calculate Pearson score
        int n = sameMoveScored.size();
        double num = sumP - (sum1 * sum2 / n);
        double den = sqrt((sum1Sq - pow(sum1, 2) / n) * (sum2Sq - pow(sum2, 2) / n));
        if (den == 0) return 0;
        return num / den;
    }


    public static void main(String[] args) {
        System.out.println("=========================PearsonCorrelationScore=========================");
        System.out.println("\"Gene Seymour\" and \"Aleksei\" Distance: " + getSimilarityDistance(demo.Recommendations.personToMovie, "Aleksei", "Gene Seymour"));
        System.out.println("\"Lisa Rose\" and \"Aleksei\" Distance: " + getSimilarityDistance(demo.Recommendations.personToMovie, "Lisa Rose", "Aleksei"));
        System.out.println("\"Mick LaSalle\" and \"Aleksei\" Distance: " + getSimilarityDistance(Recommendations.personToMovie, "Mick LaSalle", "Aleksei"));
        System.out.println("=========================PearsonCorrelationScore=========================");
    }

}
