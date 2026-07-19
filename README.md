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
		Ces 2 parametres suivant sont optionnels
  		Sauf si l'application client veut utiliser spring 
  	
  		Indication a spring de la classe de configuration principale si on 
  		utilise spring dans la partie client
  	-->
	<context-param>
    	<param-name>contextConfigLocation</param-name>
    	<param-value>mg.conquerant.sprinttest.config.SpringConfig</param-value>
	</context-param>
  	<!-- 
  		Indication de l'utilisation du Context listener de spring 
  	-->
  	<listener>
    	<listener-class>
        	org.springframework.web.context.ContextLoaderListener
    	</listener-class>
	</listener>
  
    <!--
        Definition de la servlet principale , base du framework
        Centre de controlle de l'application
    -->
    <servlet>
        <servlet-name>FrontControllerServlet</servlet-name>
        <servlet-class>mg.conquerant.sprintmvc.core.web.FrontControllerServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontControllerServlet</servlet-name>
        <url-pattern>/</url-pattern>
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

    <!--
        Exemple de definition des packages a scan par l'application
     -->
    <context-param>
        <param-name>package_list</param-name>
        <param-value>mg.conquerant.sprinttest.controller;mg.conquerant.sprinttest.test</param-value>
    </context-param>

    <!--
     Separateur de liste modifiable
    -->
    <context-param>
        <param-name>list_separator</param-name>
        <param-value>;</param-value>
    </context-param>

    <!--
  	    Configuration du prefixe du chemin
    -->
    <context-param>
        <param-name>prefix</param-name>
        <param-value>WEB-INF/jsp/</param-value>
    </context-param>

    <!--
  	    Configuration de la terminaison du fichier
    -->
    <context-param>
        <param-name>suffix</param-name>
        <param-value>.jsp</param-value>
    </context-param>

</web-app>
```

    

## Livrable

- un .jar contenant le projet

## Technologies

- java
