# Masked logs

Logback pattern layout for masking sensitive fields in 
the logs.

Fields should be configured ONLY in the low underscore case.
Other cases will be calculated automatically.

Field name allowed pattern: `[a-z0-9_]*` 

Field value allowed pattern: `[A-Za-z0-9_+-]*`

Fields are searched in:
* URL request parameters
* URL path parameters
* JSON fields
* Map keys (if map is printed in logs, like `map.toString()`)

## Example
    
#### Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="FileAppender" class="ch.qos.logback.core.FileAppender">
        <file>logback-logs.log</file>
        <layout class="org.poa1024.maskedlogs.logback.AsteriskMaskingPatternLayout">
            <maskPercentage>60</maskPercentage>
            <field>person_id</field>
            <field>order_id</field>
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <root level="info">
        <appender-ref ref="FileAppender"/>
    </root>

</configuration>
```
 
#### Log
```log
  2022-07-25 12:46:15,633 INFO ort.poa1024.app.ClientLoggingInterceptor [http-nio-8080-exec-3] Request
  POST https://poa1024.com/person_id/poa1024?order_id=146562356
  Accept: application/json, application/*+json
  Content-Type: application/json
  Content-Length: 433
    {
      "personId" : "poa1024",
      "orderId" : 146562356
    }
```

#### Masked log
```log
  2022-07-25 12:46:15,633 INFO ort.poa1024.app.ClientLoggingInterceptor [http-nio-8080-exec-3] Request
  POST https://poa1024.com/person_id/p****24?order_id=14*****56
  Accept: application/json, application/*+json
  Content-Type: application/json
  Content-Length: 433
    {
      "personId" : "p****24",
      "orderId" : 14*****56
    }
```

## Spring logback configuration example

spring-logback.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <springProperty scope="context" name="maskPercentage" source="log.maskPercentage"/>
    <springProperty scope="context" name="fieldsToMask" source="log.fieldsToMask"/>
    
    <appender name="FileAppender" class="ch.qos.logback.core.FileAppender">
        <file>spring-logback-logs.log</file>
        <layout class="org.poa1024.maskedlogs.logback.AsteriskMaskingPatternLayout">
            <maskPercentage>${maskPercentage}</maskPercentage>
            <fields>${fieldsToMask}</fields>
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <root level="info">
        <appender-ref ref="FileAppender"/>
    </root>

</configuration>
```

application.properties
```properties
log.maskPercentage=60.0
log.fieldsToMask=person_id, order_id
```