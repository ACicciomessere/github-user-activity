package edu.itba.useractivity.api;

public class Router {
    private static final String BACKEND_URL = "http://localhost:8080/";

    public static String USER_EVENTS(String user){return BACKEND_URL + "events/user/" + user;}

    public static String REPOSITORY_PULL_REQUESTS(String owner, String repo){ 
        return BACKEND_URL + "repository/" + owner + "/" + repo + "/pull-requests"; 
    }

    public static String REPOSITORY_MERGED_PULL_REQUESTS(String owner, String repo){ 
        return BACKEND_URL + "repository/" + owner + "/" + repo + "/pull-requests/merged"; 
    }

    public static String REPOSITORY_COMMITS(String owner, String repo){ 
        return BACKEND_URL + "repository/" + owner + "/" + repo + "/commits"; 
    }

    public static String REPOSITORY_PULL_REQUESTS_LIFE_AVG(String owner, String repo){ 
        return BACKEND_URL + "repository/" + owner + "/" + repo + "/pull-requests/life-avg"; 
    }
}
