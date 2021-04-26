package com.docler.examles.chapter2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.docler.examles.chapter2.Recommendations.movieToPerson;

public class MovieWithDataSet {

    public static void main(String[] args) {
        Map<Long, String> movieIdToTitle = loadMovieIdToTitle();
//        Map<String, Map<String, Double>> personToMovie = loadDataSet(movieIdToTitle);
//        System.out.println("====================Get recommendations for user====================");
//        System.out.println("User based recommendation for user 177: " + Recommendations.getUserBasedRecommendations(personToMovie, "177", "Euclidean"));
//
//        System.out.println("------------------calculateSimilarItems started------------------");
//        Map<String, List<KeyScore>> itemsSimilarity = Recommendations.calculateSimilarItems(personToMovie, 10);
//        System.out.println("------------------calculateSimilarItems complete------------------");
//        System.out.println("Item based recommendation for user 177: " + Recommendations.getItemBasedRecommendations(personToMovie, itemsSimilarity, "177", "Euclidean"));
//        System.out.println("Item based recommendation for user 183: " + Recommendations.getItemBasedRecommendations(personToMovie, itemsSimilarity, "183", "Euclidean"));
//        System.out.println("Item based recommendation for user 198: " + Recommendations.getItemBasedRecommendations(personToMovie, itemsSimilarity, "198", "Euclidean"));
//        System.out.println("Item based recommendation for user 552: " + Recommendations.getItemBasedRecommendations(personToMovie, itemsSimilarity, "552", "Euclidean"));

        System.out.println("------------------calculateSimilarTags started------------------");
        Map<String, Map<String, Double>> movieToTag = loadTagDataSet(movieIdToTitle);
        Map<String, List<KeyScore>> tagsSimilarity = Recommendations.calculateSimilarItems(movieToTag, 10);
        System.out.println("tagsSimilarity: " + tagsSimilarity);
        System.out.println("Item based tags recommendation for Fight Club (1999) ending: " + Recommendations.getItemBasedRecommendations(movieToTag, tagsSimilarity, "Fight Club (1999)", "Pearson"));

        System.out.println("------------------calculateSimilarItems complete------------------");
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

    private static Map<String, Map<String, Double>> loadTagDataSet(Map<Long, String> movieIdToTitle) {
        Map<String, Map<String, Double>> tagToMovie = new HashMap<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("movie-dataset/tags.csv").toURI()));
            allLines.remove(0);//skip titles
            for (String line : allLines) {
                String[] split = line.split(",");
                String userId = split[0];
                String movie = movieIdToTitle.get(Long.valueOf(split[1]));
                String tag = split[2];
                Map<String, Double> scores;
                if (tagToMovie.containsKey(movie)) {
                    scores = tagToMovie.get(movie);
                } else {
                    scores = new HashMap<>();
                }
                scores.put(tag, scores.getOrDefault(tag, 0.0) + 1);
                tagToMovie.put(movie, scores);
            }
            return tagToMovie;
        } catch (Exception e) {
            return null;
        }
    }
}
