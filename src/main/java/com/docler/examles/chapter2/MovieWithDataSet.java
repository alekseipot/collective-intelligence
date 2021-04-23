package com.docler.examles.chapter2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieWithDataSet {

    public static void main(String[] args) {
        Map<Long, String> movieIdToTitle = loadMovieIdToTitle();
        Map<String, Map<String, Double>> personToMovie = loadDataSet(movieIdToTitle);
        System.out.println("====================Get recommendations for user====================");
        System.out.println("User based recommendation for user 177: " + Recommendations.getUserBasedRecommendations(personToMovie, "177", "Euclidean"));
        System.out.println("Item based recommendation for user 177: " + Recommendations.getItemBasedRecommendations(personToMovie, "177", "Euclidean"));

    }

    private static Map<Long, String> loadMovieIdToTitle() {
        Map<Long, String> movieIdToTitle = new HashMap<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("movie-dataset/movies.csv").toURI()));
            allLines.remove(0);//skip titles
            for (String line : allLines) {
                String[] split = line.split(",");
                movieIdToTitle.put(Long.valueOf(split[0]), split[1]);
            }
            return movieIdToTitle;
        } catch (Exception e) {
            return null;
        }
    }

    private static Map<String, Map<String, Double>> loadDataSet(Map<Long, String> movieIdToTitle) {
        Map<String, Map<String, Double>> personToMovie = new HashMap<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("movie-dataset/ratings.csv").toURI()));
            allLines.remove(0);//skip titles
            for (String line : allLines) {
                String[] split = line.split(",");
                String userId = split[0];
                String movie = movieIdToTitle.get(Long.valueOf(split[1]));
                Double score = Double.valueOf(split[2]);
                Map<String, Double> scores;
                if (personToMovie.containsKey(userId)) {
                    scores = personToMovie.get(userId);
                } else {
                    scores = new HashMap<>();
                }
                scores.put(movie, score);
                personToMovie.put(userId, scores);
            }
            return personToMovie;
        } catch (Exception e) {
            return null;
        }
    }
}
