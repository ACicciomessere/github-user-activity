package OLD;

import org.json.JSONArray;

public class App {

    public static void main(String[] args) throws Exception {
        // Start web server when no args or when explicitly requested
        if (args.length == 0 || (args.length >= 1 && "server".equalsIgnoreCase(args[0]))) {
            int port = 8080;
            if (args.length >= 2) {
                try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignore) {}
            }
            CommitMonitorServer server = new CommitMonitorServer();
            server.start(port);
            // Keep main thread alive
            Thread.currentThread().join();
            return;
        }

        if (args.length == 1) {
            String apiEndpoint = "https://api.github.com/users/" + args[0] + "/events";

            try {
                GithubDataFetcher gh = new GithubHttpClientDataFetcher();
                JSONArray events = gh.fetchAllEvent(apiEndpoint);

                if (args.length == 2 && !args[1].isEmpty())
                    DataFormater.specificEventDisplayer(events, args[1]);
                else
                    DataFormater.eventsDisplayer(events);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            return;
        }

        if (args.length == 2) {
            try {
                GithubDataFetcher gh = new GithubHttpClientDataFetcher();
                JSONArray events = gh.fetchHistoricalPRs(args[0], args[1]);

                DataFormater.historicalPRsDisplayer(events);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            return;
        }


        System.err.println("Usage: java -cp \"bin:lib/*\" OLD.App <username> [EventType] | server [port]");
    }
}
