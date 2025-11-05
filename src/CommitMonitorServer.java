// filepath: /Users/augusto/Documents/github-user-activity/src/CommitMonitorServer.java

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import model.GithubDataFetcher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommitMonitorServer {
    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/health", exchange -> {
            byte[] body = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Commit monitor running on http://localhost:" + port + "/");
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendHtml(exchange, 405, layout("Method Not Allowed", "<p>Only GET is supported.</p>"));
                    return;
                }

                URI uri = exchange.getRequestURI();
                Map<String, String> query = parseQuery(uri.getRawQuery());
                String username = query.getOrDefault("user", "").trim();

                if (username.isEmpty()) {
                    sendHtml(exchange, 200, layout("GitHub Commit Monitor", form("")));
                    return;
                }

                String endpoint = "https://api.github.com/users/" + url(username) + "/events";
                JSONArray events;
                try {
                    GithubDataFetcher gh = new GithubHttpClientDataFetcher();
                    events = gh.fetchAllEvent(endpoint);
                } catch (Exception e) {
                    sendHtml(exchange, 500, layout("Error", errorBox("Failed to fetch data: " + html(e.getMessage())) + form(username)));
                    return;
                }

                if (events == null) {
                    sendHtml(exchange, 200, layout("No Data", infoBox("No events found for user '" + html(username) + "'.") + form(username)));
                    return;
                }

                // Filter PushEvent and build rows
                List<Map<String, String>> rows = new ArrayList<>();
                int totalPushes = 0;
                int totalCommits = 0;

                for (int i = 0; i < events.length(); i++) {
                    JSONObject ev = events.optJSONObject(i);
                    if (ev == null) continue;
                    String type = ev.optString("type", "");
                    if (!"PushEvent".equals(type)) continue;

                    totalPushes++;
                    JSONObject payload = ev.optJSONObject("payload");
                    JSONObject repo = ev.optJSONObject("repo");
                    String repoName = repo != null ? repo.optString("name", "unknown repo") : "unknown repo";

                    int commits = 0;
                    if (payload != null) {
                        commits = payload.optInt("distinct_size", 0);
                        if (commits == 0) {
                            JSONArray commitsArray = payload.optJSONArray("commits");
                            commits = commitsArray != null ? commitsArray.length() : 0;
                        }
                    }
                    totalCommits += commits;

                    String createdAt = ev.optString("created_at", "");
                    String createdAtFmt = createdAt;
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse(createdAt);
                        createdAtFmt = odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss O", Locale.US));
                    } catch (Exception ignore) {}

                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("when", createdAtFmt);
                    row.put("repo", repoName);
                    row.put("commits", String.valueOf(commits));
                    rows.add(row);
                }

                StringBuilder body = new StringBuilder();
                body.append("<div class=\"summary\">");
                body.append("<div class=\"card\"><div class=\"label\">User</div><div class=\"value\">").append(html(username)).append("</div></div>");
                body.append("<div class=\"card\"><div class=\"label\">Push events</div><div class=\"value\">").append(totalPushes).append("</div></div>");
                body.append("<div class=\"card\"><div class=\"label\">Total commits</div><div class=\"value\">").append(totalCommits).append("</div></div>");
                body.append("</div>");

                body.append(table(rows));
                body.append("<div class=\"foot\">Data source: GitHub public events API. Cached via Redis if available.</div>");

                String page = layout("Commits for " + html(username), form(username) + body);
                sendHtml(exchange, 200, page);
            } catch (Exception e) {
                sendHtml(exchange, 500, layout("Unexpected Error", errorBox(html(e.toString()))));
            }
        }

        private static Map<String, String> parseQuery(String raw) {
            Map<String, String> map = new LinkedHashMap<>();
            if (raw == null || raw.isEmpty()) return map;
            for (String pair : raw.split("&")) {
                int idx = pair.indexOf('=');
                String k = idx >= 0 ? pair.substring(0, idx) : pair;
                String v = idx >= 0 ? pair.substring(idx + 1) : "";
                map.put(urlDecode(k), urlDecode(v));
            }
            return map;
        }

        private static String layout(String title, String content) {
            return "<!doctype html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "<meta charset=\"utf-8\">" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                    "<title>" + title + "</title>" +
                    "<style>" + BASE_CSS + "</style>" +
                    "</head>" +
                    "<body>" +
                    "<header><h1>GitHub Commit Monitor</h1></header>" +
                    content +
                    "</body></html>";
        }

        private static String form(String userPrefill) {
            String u = userPrefill == null ? "" : html(userPrefill);
            return "<form method=\"GET\" action=\"/\" class=\"search\">" +
                    "<label>GitHub username</label>" +
                    "<input type=\"text\" name=\"user\" placeholder=\"octocat\" value=\"" + u + "\" required>" +
                    "<button type=\"submit\">View commits</button>" +
                    "</form>";
        }

        private static String table(List<Map<String, String>> rows) {
            if (rows.isEmpty()) {
                return infoBox("No PushEvent activity found in the latest events.");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<div class=\"table-wrap\">");
            sb.append("<table><thead><tr><th>When</th><th>Repository</th><th>Commits</th></tr></thead><tbody>");
            for (Map<String, String> r : rows) {
                sb.append("<tr>")
                  .append("<td>").append(html(r.getOrDefault("when", ""))).append("</td>")
                  .append("<td>").append(html(r.getOrDefault("repo", ""))).append("</td>")
                  .append("<td class=\"num\">").append(html(r.getOrDefault("commits", "0"))).append("</td>")
                  .append("</tr>");
            }
            sb.append("</tbody></table></div>");
            return sb.toString();
        }

        private static String infoBox(String msg) {
            return "<div class=\"info\">" + html(msg) + "</div>";
        }

        private static String errorBox(String msg) {
            return "<div class=\"error\">" + html(msg) + "</div>";
        }

        private static String html(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }

        private static String url(String s) {
            return s == null ? "" : s.replace(" ", "%20");
        }

        private static String urlDecode(String s) {
            return s == null ? "" : URLDecoder.decode(s, StandardCharsets.UTF_8);
        }

        private static void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
            byte[] body = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(status, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        }

        private static final String BASE_CSS = """
                :root{--bg:#0b1020;--panel:#111832;--border:#22315a;--text:#e6e9f5;--muted:#9aa3c7;--accent:#4f8cff;--ok:#2ecc71;--err:#ff5c5c}
                *{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--text);font:14px/1.5 -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Ubuntu,Cantarell,'Helvetica Neue',Arial}
                header{padding:16px 20px;border-bottom:1px solid var(--border);background:linear-gradient(180deg,#131b38,#0e1530)}
                h1{margin:0;font-size:18px}
                .search{display:flex;gap:10px;align-items:end;padding:16px 20px;border-bottom:1px solid var(--border)}
                .search label{display:block;color:var(--muted);font-size:12px}
                .search input{flex:1;min-width:220px;padding:10px 12px;border:1px solid var(--border);border-radius:8px;background:#0d142c;color:var(--text)}
                .search button{padding:10px 14px;border:1px solid var(--border);border-radius:8px;background:var(--accent);color:white;cursor:pointer}
                .summary{display:flex;gap:12px;flex-wrap:wrap;padding:16px 20px}
                .card{background:var(--panel);padding:12px 14px;border:1px solid var(--border);border-radius:10px}
                .card .label{font-size:12px;color:var(--muted)}
                .card .value{font-size:20px}
                .table-wrap{padding:0 20px 24px}
                table{width:100%;border-collapse:collapse;background:var(--panel);border:1px solid var(--border);border-radius:10px;overflow:hidden}
                thead{background:#0f1731}
                th,td{padding:10px 12px;border-bottom:1px solid var(--border)}
                td.num{text-align:right}
                .info,.error{margin:16px 20px;padding:12px 14px;border-radius:10px;border:1px solid var(--border)}
                .info{background:#0e1b36;color:var(--text)}
                .error{background:#2b1321;color:#ffd6d6;border-color:#5b2237}
                .foot{padding:0 20px 18px;color:var(--muted);font-size:12px}
                @media (max-width:600px){.search{flex-direction:column;align-items:stretch}.card .value{font-size:18px}}
                """;
    }
}
