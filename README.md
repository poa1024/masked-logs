# Masked logs

```xml
<dependency>
    <groupId>io.github.poa1024</groupId>
    <artifactId>masked-logs</artifactId>
    <version>1.0.3</version>
</dependency>
```

Logback pattern layout for masking sensitive fields in 
the logs. 

It's possible to mask only a part of the field's value (by using `maskPercentage` parameter),
but by default the full value is masked. 

Fields should be configured ONLY in the low underscore case.
Other cases will be calculated automatically.

Field name allowed pattern: `[a-z0-9_]*` 

Field value allowed pattern: `[A-Za-z0-9_+-]*`

Fields are searched in:

* URL request parameters
* URL path parameters (disabled by default)
* JSON fields
* Map keys (if map is printed in logs, like `map.toString()`)

## Example
    
#### Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="FileAppender" class="ch.qos.logback.core.FileAppender">
        <file>logback-logs.log</file>
        <layout class="org.poa1024.maskedlogs.logback.AsteriskFieldMaskingPatternLayout">
            <maskPercentage>60</maskPercentage>
            <field>person_id</field>
            <field>order_id</field>
            <Pattern>
                %msg%n
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
  POST https://poa1024.com/orders?order_id=146562356
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
  POST https://poa1024.com/orders?order_id=14*****56
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
        <layout class="org.poa1024.maskedlogs.logback.AsteriskFieldMaskingPatternLayout">
            <maskPercentage>${maskPercentage}</maskPercentage>
            <fields>${fieldsToMask}</fields>
            <Pattern>
                %msg%n
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

## Properties

| Name | Description | Default |
|:-----|:------------|:--------|
| `<field/>` | Field that should be masked | none |       
| `<fields/>` | Fields (comma separated) that should be masked | none |        
| `<maskPercentage/>` | Value from 0 to 100, defining the mask size (`card_number=3454*****123` or `card_number=34*********3` or `card_number=************`) | 100% |        
| `<jsonPatternsEnabled/>` | Enables json fields masking | true |        
| `<equalSignPatternsEnabled/>` | Enables map keys and url parameters masking | true |        
| `<urlPathPatternsEnabled/>` | Enables path parameters masking (`http://some.org/person_id/1243`, `person_id` - field's name, `1243` - value to mask) | false |        

## Manually configured regex patterns

As an alternative you can define regex patterns by yourself. You should use regex group `value` to mark what you want to replace with mask.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="FileAppender" class="ch.qos.logback.core.FileAppender">
        <file>logback-logs.log</file>
        <layout class="org.poa1024.maskedlogs.logback.AsteriskMaskingPatternLayout">
            <maskPercentage>60</maskPercentage>
            <regexPattern>.*order_id=(?&lt;value&gt;[0-9a-z]*),</regexPattern>
            <regexPattern>.*personId=(?&lt;value&gt;[0-9a-z]*),</regexPattern>
            <Pattern>
                %msg%n
            </Pattern>
        </layout>
    </appender>

    <root level="info">
        <appender-ref ref="FileAppender"/>
    </root>

</configuration>
```