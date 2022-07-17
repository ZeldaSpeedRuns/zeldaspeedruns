# ZeldaSpeedRuns

`ZeldaSpeedRuns` is an in-development web application aimed at providing a community hub for the greater Zelda
speedrunning community. The application aims to provide a modern, clean, and efficient set of features that can aid in
the management of hosting tournaments, scheduling events, managing volunteers, and more.

The application is written in Java 17 using the Spring framework, using Thymeleaf for HTML client views and Sass (SCSS) 
for styling. A React/NextJS based frontend is planned for the future.

## Getting Started

Building the ZSR application is easy. To get started, making sure you have any Java 17 or higher compatible JDK 
installed. We recommend using OpenJDK. This application uses Maven as its build system and comes with the Maven 
wrapper `mnvw` included, which can be used to build the application in lieu of a Maven system-wide installation.

```shell
mvnw clean package # or mvnw.bat on Windows 
```

The ZSR application relies on Node for compiling SCSS files. Maven will automatically pull Node v18 and install
the required packages. **You do not need to run `npm install` manually.**

After the command has finished, you can start the Spring Boot application like so:

```shell
java -jar target/zeldaspeedruns-{VERSION}.jar
```

## Configuration

### Database

This application is designed to run against a PostgreSQL Database only. You can find a schema file in the resources
directory at `src/main/resources/schema.sql` which can be used to initialize a database for the ZeldaSpeedRuns 
application. During development, we recommend using Docker to spin up a PostgreSQL image.

To help the application find the database, 
[you need to set some configuration parameters.](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
The linked article contains instructions on how to do so, but the easiest way by far is through OS environment
variables:

```shell
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:49153/postgres
export SPRING_DATASOURCE_USERNAME=postgres 
export SPRING_DATASOURCE_PASSWORD=postgrespw
```

Or by using command line arguments:

```shell
java -jar zeldaspeedruns-<version>.jar \ 
    --spring.datasource.url=jdbc:postgresql://localhost:49153/postgres \
    --spring.datasource.username=postgres \
    --spring.datasource.password=postgrespw
```

### Twitch and Discord Authentication

The application provides Twitch and Discord integration. To make these features work you must create a Discord and 
Twitch API key. See the developer API documentation for these services on how to do that. Once you have done so, obtain
your client ID and secret. You can pass these to the applications as follows:

```shell
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_DISCORD_CLIENT_ID=discordclientid
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_DISCORD_CLIENT_SECRET=discord-secret
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_DISCORD_TWITCH_ID=twitchlientid
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_DISCORD_TWITCH_SECRET=twitch-secret
```

Currently, the application will not work without these. For development reasons we'll be looking for a more graceful 
way to disable these features.

### SMTP

Much like above, ZeldaSpeedRuns requires the presence of an SMTP server. You can use your own personal GMail account for
this but a much better idea is to use Docker and spin up a small SMTP server for development such as Mailhog. We are 
planning to use GreenMail (which is an embedded development SMTP server) in the future for this goal.

You can point the application to the correct place with the following environment variables:

```shell
export SPRING_MAIL_HOST=localhost
export SPRING_MAIL_PORT=55025
```

For more information see the Spring Mail documentation.

## Development

This application comes with Spring Boot DevTools enabled by default, allowing you the benefits they provide during
development. Refer to the documentation of Spring Boot DevTools for more information on how to use it and how to 
disable it for production.

### Recompile SCSS Stylesheets

The project makes use of Sass for stylesheets, but these will not be automatically recompiled by the DevTools, as it is
not aware of the presence of Maven and Node. As a result, you may experience missing stylesheets when checking the 
site during development.

To enable to automatic recompiling of Sass stylesheets, run the following command:

```shell
npm run watch
```

You can now edit the stylesheets under `src/main/scss` while Sass automatically compiles your changes. To see your
changes you need only refresh the web page. 

## Contributing

We welcome any contributions. If you wish to get involved with the project, please reach out to *Spell* or *TreZc0_* 
on the [ZSR Discord](https://discord.gg/zsr).