![EEXCESS](http://eexcess.eu/wp-content/uploads/2013/04/eexcess_Logo_neu1.jpg "EEXCESS")

## Purpose
The purpose of the privacy proxy is to ensure users privacy in the EEXCESS system. Each query issued by the clients (e.g., Google Chrome extension, Wordpress plug-in) is sent to the privacy proxy before it is forwarded to the federated recommender. It allows obfuscating or anonymising it when it is required by the user. For instance, if a user does not want to share her location, then the privacy proxy will ensure that this information does not get to the federated recommender. 

## Installation
The privacy proxy is developed in Java. Dependencies are handled with Maven (no additional dependencies need to be installed manually). 

## Getting started
At this stage of the project, the privacy proxy: 
* does 1
* does 2

```java
public class PrivacyProxyService {
  public Response responseJSON(String origin,	String input, HttpServletRequest req) { ... }
}

public class ProxyLogProcessor {
  public void process(InteractionType type, String origin, String ip, String request) { ... }
  public void process(InteractionType type, String origin, String ip, String request, String answer) { ... }
```

## Settings
