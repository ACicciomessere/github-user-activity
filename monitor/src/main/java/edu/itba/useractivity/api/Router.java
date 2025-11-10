package edu.itba.useractivity.api;

public class Router {
    private static final String BACKEND_URL = "http://localhost:8080/api/";

    public static String USER_EVENTS(String user){return BACKEND_URL + "events/user/" + user;}

    public static String USER_REPOSITORY(String user, String repo){ return BACKEND_URL + "repos/" + user + "/" + repo; }

    public static String REPOSITORY_COMMITS(String user, String repo){ return USER_REPOSITORY(user, repo) + "/pulls"; }

    public static String REPOSITORY_MERGED(String user, String repo){ return USER_REPOSITORY(user, repo) + "/merged"; }
}
