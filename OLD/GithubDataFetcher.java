package OLD;

import org.json.JSONArray;

public interface GithubDataFetcher {
    public JSONArray fetchAllEvent(String url) throws Exception;
    public JSONArray fetchPREvents(String owner, String repo) throws Exception;
    public JSONArray fetchHistoricalPRs(String owner, String repo) throws Exception;
}
