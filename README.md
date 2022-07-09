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

## Development

This application comes with Spring Boot DevTools enabled by default, allowing you the benefits they provide during
development. Refer to the documentation of Spring Boot DevTools for more information on how to use it and how to 
disable it for production.

### Recompile SCSS stylesheets

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