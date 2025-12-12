package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error");
            System.exit(1);
        }

        String username = args[0];
        String password = args[1];

        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try (Scanner myScanner = new Scanner(System.in)) {
            System.out.println("Enter actor name:");
            String userInputLastName = myScanner.nextLine().trim();

            displayActorsWithLastName(dataSource, userInputLastName);

            System.out.println("\nEnter the first name of the actor:");
            String userInputMovieActorFirstName = myScanner.nextLine().trim();

            System.out.println("\nEnter the last name of the actor:");
            String userInputMovieActorLastName = myScanner.nextLine().trim();

            displayMoviesWithActorFirstAndLastName(dataSource, userInputMovieActorFirstName, userInputMovieActorLastName);
        } catch (Exception ex) {
            System.out.println("Error on inputs");
            System.out.println(ex.getMessage());
        }
    }
    public static void displayMoviesWithActorFirstAndLastName(BasicDataSource dataSource,
                String movieActorFirstName,
                String movieActorLastName) {
        try {
            String query = """
                    SELECT Title
                    FROM film_actor as fa
                    INNER JOIN film as f ON (fa.Film_ID = f.Film_ID)
                    INNER JOIN actor as a ON (fa.Actor_ID = a.Actor_ID)
                    WHERE First_Name = ? AND Last_Name = ?
                    """;
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)
            ) {
                preparedStatement.setString(1, movieActorFirstName);
                preparedStatement.setString(2, movieActorLastName);

                try (ResultSet results = preparedStatement.executeQuery()) {
                    if (results.next()) {
                        do {
                            String movieName = results.getString(1);
                            System.out.println("Movie Name: " + movieName);
                        } while (results.next());
                    } else {
                        System.out.println("No Matches!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error");
            System.out.println(ex.getMessage());
        }
    }
    public static void displayActorsWithLastName(BasicDataSource dataSource, String userInputLastName) {
        try {
            String query = """
                    SELECT First_Name, Last_Name
                    FROM actor
                    WHERE Last_Name = ?
                    """;
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)
                ) {
                preparedStatement.setString(1, userInputLastName);
                try (ResultSet results = preparedStatement.executeQuery()) {
                    if (results.next()) {
                        do {
                            String firstName = results.getString(1);
                            String lastName = results.getString(2);

                            System.out.println("First Name: " + firstName);
                            System.out.println("Last Name: " + lastName);
                            System.out.println("----------------------------");
                        } while (results.next());
                    } else {
                        System.out.println("No Matches found :(");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error");
            System.out.println(ex.getMessage());
        }
    }
}

