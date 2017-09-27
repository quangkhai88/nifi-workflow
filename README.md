nifi-workflow-tool

To start Nifi workflow control tool:

+Place jar file and config file in the same folder and launch:

java -jar nifi-workflow-0.0.1-SNAPSHOT.jar --spring.config.location=file:./application.yml

An other way, if they are not in the same folder:
java -jar nifi-workflow-0.0.1-SNAPSHOT.jar --spring.config.location="pathToConfigFile" --spring.config.name=application.yml