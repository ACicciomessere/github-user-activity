## Github-user-activity

Hi. This is a Java CLi app to fetch github user activity.

## Features

- Fetch user activity
- Filter user activity of different type like **push event, commit comment event, watch event, and more**
- Caching fetched datas to improve performance

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

3. run

```bash
# Display activities of a user
java -cp "bin:lib/*" App <username>
# ex: java -cp "../bin:../lib/*" App ruxlsr
# or
# Display activity with filter
java -cp "bin:lib/*" App <username> [EventType]
# ex: java -cp "../bin:../lib/*" App ruxlsr PushEvent
```

> En Windows reemplaza `:` por `;` dentro de las rutas de classpath.

Si ya tienes un servidor Redis en ejecución, exporta la variable `REDIS_PORT` con el puerto correspondiente antes de ejecutar la aplicación (por defecto se usa `55555`).

**Nb:** _all event type are supported_. Refers to [github Event Type](https://docs.github.com/en/rest/using-the-rest-api/github-event-types?apiVersion=2022-11-28)

# Others

That was provided by **[roadmap.sh](https://roadmap.sh/projects/github-user-activity)**
