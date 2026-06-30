# Framework copy : Spring MVC

Nous allons faire notre Framework similaire a spring MVC

## Fonctionnement

- fonctionnalites par Sprint - une fois par semaine / jour nours auront une fonctionnalites
  a implementer
- separer le code source du framework a separer du code source des fichiers de test
- utiliser git tout les jours

## Setup necessaire

Pour pouvoir bien utiliser dans l'application client il faut mettre en place un web.xml .
C'est le format du fichier

```xml

<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
    version="3.1">

    <display-name> Servlet for framework setup </display-name>

    <!--
        C'est le serveur     
    -->
    <servlet>
        <servlet-name>FrontControllerServlet</servlet-name>
        <servlet-class>mg.conquerant.sprintmvc.core.web.FrontControllerServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontControllerServlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

    <!-- 
        Declaration de la classe du ServletContextListener de notre application
    -->
    <listener>
        <listener-class>mg.conquerant.sprintmvc.core.web.InitializerContextListener</listener-class>
    </listener>

    <!-- 
        On met les parametres a passer aux contextListener 
        dans context param pour etre accessible sans servlet
        c'est donc dans le scope global
    -->
    <context-param>
        <param-name>package_list</param-name>
        <param-value>mg.conquerant.sprinttest.controller;mg.conquerant.sprinttest.test</param-value>
    </context-param>

    <context-param>
        <param-name>list_separator</param-name>
        <param-value>;</param-value>
    </context-param>

</web-app>

```

## Livrable

- un .jar contenant le projet

## Technologies

- java
