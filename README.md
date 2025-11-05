## Github-user-activity

Hi. This is a Java CLi app to fetch github user activity.

Now it also includes a tiny Web UI (embedded server) to monitor commit activity by username.

## Features

- Fetch user activity
- Filter user activity of different type like **push event, commit comment event, watch event, and more**
- Caching fetched datas to improve performance
- Simple Web UI to input a GitHub username and view commit (PushEvent) activity

## Installation

1. clone the project:

```bash
    git clone https://github.com/ruxlsr/github-user-activity.git
    cd github-user-activity
```

2. Compile the source code:

```bash
    javac -cp "lib/*" src/*.java src/model/*.java -d bin
```

## Run: CLI mode

```bash
# Display activities of a user
java -cp "bin:lib/*" App <username>
# ex: java -cp "bin:lib/*" App ruxlsr

# Display activity with filter
java -cp "bin:lib/*" App <username> [EventType]
# ex: java -cp "bin:lib/*" App ruxlsr PushEvent
```

> En Windows reemplaza `:` por `;` dentro de las rutas de classpath.

Si ya tienes un servidor Redis en ejecución, exporta la variable `REDIS_PORT` con el puerto correspondiente antes de ejecutar la aplicación (por defecto se usa `55555`).

**Nb:** _all event type are supported_. Refers to [github Event Type](https://docs.github.com/en/rest/using-the-rest-api/github-event-types?apiVersion=2022-11-28)

## Run: Web UI (Commit Monitor)

Start the embedded HTTP server and open the monitor in your browser. It exposes a form where you can enter a GitHub username and see recent PushEvent activity (commits per repo, timestamp, totals).

```bash
# Start on default port 8080
java -cp "bin:lib/*" App server

# Or specify a port, e.g. 3000
java -cp "bin:lib/*" App server 3000
```

Open http://localhost:8080/ (or your chosen port) and type a username (e.g. `octocat`).

Notes:
- Responses are cached briefly in Redis if available. If no Redis is present, it runs without caching.
- You can set `REDIS_PORT` to point at an external Redis. If not set, an embedded Redis is attempted first; if it can't start, the app continues without cache.

# Others

That was provided by **[roadmap.sh](https://roadmap.sh/projects/github-user-activity)**
