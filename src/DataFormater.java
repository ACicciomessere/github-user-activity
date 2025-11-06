import org.json.JSONArray;
import org.json.JSONObject;

public class DataFormater {
    public static void eventsDisplayer(JSONArray jsonEventsArray){
        if (jsonEventsArray==null || jsonEventsArray.length() == 0) {
            System.out.println("the array is empty");
            return;
        }
        //System.out.println(jsonEventsArray);
        for(int i = 0; i < jsonEventsArray.length(); i++){
            JSONObject event = jsonEventsArray.getJSONObject(i);

            eventPrinter(event);
        }
    }

    public static void specificEventDisplayer(JSONArray jsonEventsArray, String eventType){
        if (jsonEventsArray==null || jsonEventsArray.length() == 0) {
            System.out.println("the array is empty");
            return;
        }
        
        //System.out.println(jsonEventsArray);
        for(int i = 0; i < jsonEventsArray.length(); i++){
            JSONObject event = jsonEventsArray.getJSONObject(i);

            if(event.getString("type").equals(eventType))
                eventPrinter(event);
        
        }
    }

    private static void eventPrinter(JSONObject event){
        JSONObject payload = event.optJSONObject("payload");
        JSONObject repo = event.optJSONObject("repo");

        String repoName = repo != null ? repo.optString("name", "unknown repo") : "unknown repo";
        String createdAt = event.optString("created_at", "");
        String type = event.optString("type", "");

        switch (type) {
            case "PushEvent" -> {
                int commits = 0;
                if (payload != null) {
                    commits = payload.optInt("distinct_size", 0);
                    if (commits == 0) {
                        JSONArray commitsArray = payload.optJSONArray("commits");
                        commits = commitsArray != null ? commitsArray.length() : 0;
                    }
                }
                System.out.println(createdAt+":: Pushed "+commits+" commit(s) to "+repoName);
            }
            case "CreateEvent" -> {
                String refType = payload != null ? payload.optString("ref_type", "resource") : "resource";
                System.out.println(createdAt+":: created a "+refType+" on "+repoName);
            }
            case "CommitCommentEvent" -> {
                System.out.println(createdAt+":: Commented on a commit in "+repoName);
            }
            case "DeleteEvent" -> {
                String refType = payload != null ? payload.optString("ref_type", "resource") : "resource";
                System.out.println(createdAt+":: deleted a "+refType+" on "+repoName);
            }
            case "ForkEvent" -> {
                System.out.println(createdAt+":: Forked "+repoName);
            }
            case "WatchEvent" -> {
                System.out.println(createdAt+":: Starred "+repoName);
            }
            case "GollumEvent" -> {
                if (payload != null) {
                    JSONArray pages = payload.optJSONArray("pages");
                    if (pages != null && pages.length() > 0) {
                        JSONObject page = pages.optJSONObject(0);
                        if (page != null) {
                            String action = page.optString("action", "updated");
                            String pageName = page.optString("page_name", "wiki page");
                            System.out.println(createdAt+":: "+action+" --"+pageName+"-- wiki page on "+repoName);
                            return;
                        }
                    }
                }
                System.out.println(createdAt+":: updated wiki content on "+repoName);
            }
            case "IssueCommentEvent" -> {
                String action = payload != null ? payload.optString("action", "performed") : "performed";
                String title = payload != null && payload.optJSONObject("issue") != null ? payload.optJSONObject("issue").optString("title", "issue") : "issue";
                System.out.println(createdAt+":: "+action+" a comment on --"+title+"-- in "+repoName);
            }
            case "IssuesEvent" -> {
                String action = payload != null ? payload.optString("action", "acted on") : "acted on";
                String title = payload != null && payload.optJSONObject("issue") != null ? payload.optJSONObject("issue").optString("title", "issue") : "issue";
                System.out.println(createdAt+":: "+action+" the issue --"+title+"-- in "+repoName);
            }
            case "MemberEvent" -> {
                String action = payload != null ? payload.optString("action", "updated") : "updated";
                System.out.println(createdAt+":: "+action+" a member in "+repoName);
            }
            case "PublicEvent" -> {
                System.out.println(createdAt+":: Made --"+repoName+"-- public 🥳🥳🥳");
            }
            case "PullRequestReviewEvent" -> {
                String action = payload != null ? payload.optString("action", "acted on") : "acted on";
                System.out.println(createdAt+":: "+action+" a pull request review on "+repoName);
            }
            case "PullRequestReviewCommentEvent" -> {
                System.out.println(createdAt+":: Commented on a pull request review in "+repoName);
            }
            case "PullRequestReviewThreadEvent" -> {
                String action = payload != null ? payload.optString("action", "updated") : "updated";
                System.out.println(createdAt+":: Marked a thread as "+action+" on "+repoName);
            }
            case "PullRequestEvent" -> {
                String action = payload != null ? payload.optString("action", "acted on") : "acted on";
                System.out.println(createdAt+":: "+action+" a pull request in "+repoName);
            }
            case "ReleaseEvent" -> {
                String action = payload != null ? payload.optString("action", "updated") : "updated";
                System.out.println(createdAt+":: "+action+" a release in "+repoName);
            }
            case "SponsorshipEvent" -> {
                String action = payload != null ? payload.optString("action", "performed") : "performed";
                System.out.println(createdAt+":: "+action+" a sponsorship action in "+repoName);
            }
            default -> {
                System.out.println(createdAt+":: " + type + " event on " + repoName);
            }
        }
    }

    public static void prDisplayer(JSONArray jsonEventsArray){
        if (jsonEventsArray==null || jsonEventsArray.length() == 0) {
            System.out.println("the array is empty");
            return;
        }
        
        for(int i = 0; i < jsonEventsArray.length(); i++){
            JSONObject event = jsonEventsArray.getJSONObject(i);
            eventPrinter(event);
        }
    }

    public static void historicalPRsDisplayer(JSONArray jsonPRsArray){
        if (jsonPRsArray==null || jsonPRsArray.length() == 0) {
            System.out.println("the array is empty");
            return;
        }
        
        for(int i = 0; i < jsonPRsArray.length(); i++){
            JSONObject pr = jsonPRsArray.getJSONObject(i);
            historicalPRPrinter(pr);
        }
    }

    private static void historicalPRPrinter(JSONObject pr){
        // Extract required fields
        String title = pr.optString("title", "No title");
        String createdAt = pr.optString("created_at", "");
        String mergedAt = pr.optString("merged_at", "");
        String state = pr.optString("state", "");
        int number = pr.optInt("number", 0);
        
        // Extract user name
        String userName = "Unknown user";
        JSONObject user = pr.optJSONObject("user");
        if (user != null) {
            userName = user.optString("login", "Unknown user");
        }
        
        // Format merged_at (can be null)
        String mergedAtDisplay = mergedAt.isEmpty() || mergedAt.equals("null") ? "Not merged" : mergedAt;
        
        // Print formatted information
        System.out.println("PR #" + number + " | " + state.toUpperCase() + 
                          " | Title: " + title + 
                          " | User: " + userName + 
                          " | Created: " + createdAt + 
                          " | Merged: " + mergedAtDisplay);
    }
}
