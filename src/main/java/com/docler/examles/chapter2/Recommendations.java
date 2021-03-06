package com.docler.examles.chapter2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*
A dictionary of movie critics and their ratings of a small set of movies
 */
public class Recommendations {
    public static final Map<String, Map<String, Double>> personToMovie = Map.of(
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

    public static void main(String[] args) {
        System.out.println("=========================Recommendations for Toby=========================");
        System.out.println("\"Toby\" top 3 recomenders: " + topMarchers(personToMovie, "Toby", 3, "Pearson"));
        System.out.println("\"Toby\" Recommendations with Pearson algorithm: " + getUserBasedRecommendations(personToMovie, "Toby", "Pearson"));
        System.out.println("\"Toby\" Recommendations with Euclidean algorithm: " + getUserBasedRecommendations(personToMovie, "Toby", "Euclidean"));
        System.out.println("=========================Recommendations for Toby=========================");

        System.out.println("=========================Movies like Superman Returns=========================");
        System.out.println("\"Superman Returns\" top 3 like: " + topMarchers(movieToPerson(personToMovie), "Superman Returns", 5, "Pearson"));
        System.out.println("\"Superman Returns\" Recommendations with Pearson algorithm: " + getUserBasedRecommendations(movieToPerson(personToMovie), "Superman Returns", "Pearson"));
        System.out.println("\"Superman Returns\" Recommendations with Euclidean algorithm: " + getUserBasedRecommendations(movieToPerson(personToMovie), "Superman Returns", "Euclidean"));
        System.out.println("=========================Movies like Superman Returns=========================");
    }

    public static List<KeyScore> getUserBasedRecommendations(Map<String, Map<String, Double>> recommendations, String person, String similarityAlg) {
        Map<String, Double> totals = new HashMap<>();
        Map<String, Double> simSum = new HashMap<>();
        for (String personFromDataSet : recommendations.keySet()) {
            //don't compare me to myself
            if (personFromDataSet.equals(person)) {
                continue;
            }

            double sim;
            if ("Euclidean".equals(similarityAlg)) {
                sim = EuclideanDistanceScore.getSimilarityDistance(recommendations, person, personFromDataSet);
            } else {
                sim = PearsonCorrelationScore.getSimilarityDistance(recommendations, person, personFromDataSet);
            }
            //ignore scores of zero or lower
            if (sim <= 0) {
                continue;
            }

            Map<String, Double> otherPersonScore = recommendations.get(personFromDataSet);
            for (String movie : otherPersonScore.keySet()) {
                //only score movies person hasn't seen yet
                if (recommendations.get(person).containsKey(movie)) {
                    continue;
                }
                if (totals.containsKey(movie)) {
                    totals.put(movie, recommendations.get(personFromDataSet).get(movie) * sim + totals.get(movie));
                    simSum.put(movie, sim + simSum.get(movie));
                } else {
                    totals.put(movie, recommendations.get(personFromDataSet).get(movie) * sim);
                    simSum.put(movie, sim);
                }
            }
        }
        //Create normalized list
        List<KeyScore> rankings = new ArrayList<>();
        for (String movie : totals.keySet()) {
            rankings.add(new KeyScore(movie, totals.get(movie) / simSum.get(movie)));
        }
        rankings.sort((o1, o2) -> o2.getScore().compareTo(o1.getScore()));
        return rankings;
    }

    public static List<KeyScore> getItemBasedRecommendations(Map<String, Map<String, Double>> recommendations, Map<String, List<KeyScore>> itemsSimilarity, String person, String similarityAlg) {
        Map<String, Double> personScores = recommendations.get(person);
        Map<String, Double> totalScore = new HashMap<>();
        Map<String, Double> totalSim = new HashMap<>();
        //Loop over items rated by this user
        for (String movie : personScores.keySet()) {
            List<KeyScore> similarItems = itemsSimilarity.get(movie);
            if(similarItems == null || similarItems.isEmpty()){continue;}
            //Loop over items similar to this one
            for (KeyScore movieWithSimilarity : similarItems) {
                //Ignore if user already scored this one
                if (personScores.containsKey(movieWithSimilarity.getKey())) {
                    continue;
                }
                Double score = totalScore.getOrDefault(movieWithSimilarity.getKey(), 0.0);
                totalScore.put(movieWithSimilarity.getKey(), score + movieWithSimilarity.getScore() * personScores.get(movie));

                Double total = totalSim.getOrDefault(movieWithSimilarity.getKey(), 0.0);
                totalSim.put(movieWithSimilarity.getKey(), total + movieWithSimilarity.getScore());
            }
        }

        //Dividing each total score by total weight to get an average
        List<KeyScore> rankings = new ArrayList<>();
        for (String movie : totalSim.keySet()) {
            if (totalScore.get(movie) > 0) {
                rankings.add(new KeyScore(movie, totalScore.get(movie) / totalSim.get(movie)));
            }
        }
        rankings.sort((o1, o2) -> o2.getScore().compareTo(o1.getScore()));
        return rankings;
    }

    public static List<KeyScore> topMarchers(Map<String, Map<String, Double>> recommendations, String person, int topN, String similarity) {
        List<KeyScore> scores =
                recommendations.keySet().stream().filter(personFromDataSet -> !personFromDataSet.equals(person))
                        .map(personFromDataSet -> {
                            if ("Euclidean".equals(similarity)) {
                                return new KeyScore(personFromDataSet, EuclideanDistanceScore.getSimilarityDistance(recommendations, person, personFromDataSet));
                            } else {
                                return new KeyScore(personFromDataSet, PearsonCorrelationScore.getSimilarityDistance(recommendations, person, personFromDataSet));
                            }
                        }).sorted((e1, e2) -> e2.getScore().compareTo(e1.getScore())).collect(Collectors.toList());

        return scores.subList(0, topN);
    }

    public static Map<String, List<KeyScore>> calculateSimilarItems(Map<String, Map<String, Double>> recommendations, Integer topN) {
        //Create a dictionary of items showing which other items they are similar to
        Map<String, List<KeyScore>> result = new HashMap<>();
        //Invert the preference matrix to be item-centric
        Map<String, Map<String, Double>> movieToPerson = movieToPerson(recommendations);
        AtomicInteger progress = new AtomicInteger();
        movieToPerson.keySet().parallelStream().forEach(movie -> {
            //Find the most similar items to this one
            List<KeyScore> scores = topMarchers(movieToPerson, movie, topN, "Pearson");
            result.put(movie, scores);
            progress.getAndIncrement();
            if (progress.get() % 1000 == 0) {
                System.out.println("Calculated similarity for items: " + progress);
            }
        });
        return result;
    }

    public static Map<String, Map<String, Double>> movieToPerson(Map<String, Map<String, Double>> recommendations) {
        Map<String, Map<String, Double>> movieToPerson = new HashMap<>();
        for (String person : recommendations.keySet()) {
            Map<String, Double> scores = recommendations.get(person);
            for (String movie : scores.keySet()) {
                Map<String, Double> personMovieScore;
                if (movieToPerson.containsKey(movie)) {
                    personMovieScore = movieToPerson.get(movie);
                } else {
                    personMovieScore = new HashMap<>();
                }
                personMovieScore.put(person, scores.get(movie));
                movieToPerson.put(movie, personMovieScore);
            }
        }
        return movieToPerson;
    }
}
