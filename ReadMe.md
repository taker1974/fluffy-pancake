привет! вопрос по liquibase.

changelog-master.yml:
databaseChangeLog:

- include:
  file: liquibase/scripts/student.sql
- include:
  file: liquibase/scripts/faculty.sql

student.sql:
-- liquibase formatted sql
-- changeset kostusonline:1
CREATE INDEX student_name_index ON student (name);

faculty.sql:
-- liquibase formatted sql
-- changeset kostusonline:1
CREATE INDEX faculty_name_color_index ON faculty (name, color);

при запуске на "свежей" БД, в которой сущности создаются и обновляются Hibernate, всё ок - приложение запускается,
работает.

гашу приложение из IDEA (Ctrl+F2 или кнопкой в панели), приложение гасится.
после этого снова запускаю приложение, и получаю ошибки liquibase:

------------------------------------------------------------------------------------------------------
C:\Users\kostu\.jdks\openjdk-23.0.1\bin\java.exe -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:59133,suspend=y,server=n -javaagent:C:\Users\kostu\AppData\Local\GIGAIDE\GIGAIDE-CE-2024.2\captureAgent\debugger-agent.jar -Dkotlinx.coroutines.debug.enable.creation.stack.trace=false -Ddebugger.agent.enable.coroutines=true -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath "C:\Projects\Java\IdeaProjects\SkySchool\target\classes;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-web\3.4.2\spring-boot-starter-web-3.4.2.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter\3.4.2\spring-boot-starter-3.4.2.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot\3.4.2\spring-boot-3.4.2.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.4.2\spring-boot-autoconfigure-3.4.2.jar;C:\Users\kostu\.m2\repository\jakarta\annotation\jakarta.annotation-api\2.1.1\jakarta.annotation-api-2.1.1.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-json\3.4.2\spring-boot-starter-json-3.4.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.18.2\jackson-databind-2.18.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.18.2\jackson-annotations-2.18.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.18.2\jackson-core-2.18.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jdk8\2.18.2\jackson-datatype-jdk8-2.18.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.18.2\jackson-datatype-jsr310-2.18.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\module\jackson-module-parameter-names\2.18.2\jackson-module-parameter-names-2.18.2.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-tomcat\3.4.2\spring-boot-starter-tomcat-3.4.2.jar;C:\Users\kostu\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\10.1.34\tomcat-embed-core-10.1.34.jar;C:\Users\kostu\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\10.1.34\tomcat-embed-el-10.1.34.jar;C:\Users\kostu\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\10.1.34\tomcat-embed-websocket-10.1.34.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-web\6.2.2\spring-web-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-beans\6.2.2\spring-beans-6.2.2.jar;C:\Users\kostu\.m2\repository\io\micrometer\micrometer-observation\1.14.3\micrometer-observation-1.14.3.jar;C:\Users\kostu\.m2\repository\io\micrometer\micrometer-commons\1.14.3\micrometer-commons-1.14.3.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-webmvc\6.2.2\spring-webmvc-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-aop\6.2.2\spring-aop-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-context\6.2.2\spring-context-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-expression\6.2.2\spring-expression-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\3.4.1\spring-boot-starter-data-jpa-3.4.1.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-jdbc\3.4.2\spring-boot-starter-jdbc-3.4.2.jar;C:\Users\kostu\.m2\repository\com\zaxxer\HikariCP\5.1.0\HikariCP-5.1.0.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-jdbc\6.2.2\spring-jdbc-6.2.2.jar;C:\Users\kostu\.m2\repository\org\hibernate\orm\hibernate-core\6.6.5.Final\hibernate-core-6.6.5.Final.jar;C:\Users\kostu\.m2\repository\jakarta\persistence\jakarta.persistence-api\3.1.0\jakarta.persistence-api-3.1.0.jar;C:\Users\kostu\.m2\repository\jakarta\transaction\jakarta.transaction-api\2.0.1\jakarta.transaction-api-2.0.1.jar;C:\Users\kostu\.m2\repository\org\jboss\logging\jboss-logging\3.6.1.Final\jboss-logging-3.6.1.Final.jar;C:\Users\kostu\.m2\repository\org\hibernate\common\hibernate-commons-annotations\7.0.3.Final\hibernate-commons-annotations-7.0.3.Final.jar;C:\Users\kostu\.m2\repository\io\smallrye\jandex\3.2.0\jandex-3.2.0.jar;C:\Users\kostu\.m2\repository\com\fasterxml\classmate\1.7.0\classmate-1.7.0.jar;C:\Users\kostu\.m2\repository\net\bytebuddy\byte-buddy\1.15.11\byte-buddy-1.15.11.jar;C:\Users\kostu\.m2\repository\org\glassfish\jaxb\jaxb-runtime\4.0.5\jaxb-runtime-4.0.5.jar;C:\Users\kostu\.m2\repository\org\glassfish\jaxb\jaxb-core\4.0.5\jaxb-core-4.0.5.jar;C:\Users\kostu\.m2\repository\org\eclipse\angus\angus-activation\2.0.2\angus-activation-2.0.2.jar;C:\Users\kostu\.m2\repository\org\glassfish\jaxb\txw2\4.0.5\txw2-4.0.5.jar;C:\Users\kostu\.m2\repository\com\sun\istack\istack-commons-runtime\4.1.2\istack-commons-runtime-4.1.2.jar;C:\Users\kostu\.m2\repository\jakarta\inject\jakarta.inject-api\2.0.1\jakarta.inject-api-2.0.1.jar;C:\Users\kostu\.m2\repository\org\antlr\antlr4-runtime\4.13.0\antlr4-runtime-4.13.0.jar;C:\Users\kostu\.m2\repository\org\springframework\data\spring-data-jpa\3.4.2\spring-data-jpa-3.4.2.jar;C:\Users\kostu\.m2\repository\org\springframework\data\spring-data-commons\3.4.2\spring-data-commons-3.4.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-orm\6.2.2\spring-orm-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-tx\6.2.2\spring-tx-6.2.2.jar;C:\Users\kostu\.m2\repository\org\slf4j\slf4j-api\2.0.16\slf4j-api-2.0.16.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-aspects\6.2.2\spring-aspects-6.2.2.jar;C:\Users\kostu\.m2\repository\org\aspectj\aspectjweaver\1.9.22.1\aspectjweaver-1.9.22.1.jar;C:\Users\kostu\.m2\repository\org\postgresql\postgresql\42.7.5\postgresql-42.7.5.jar;C:\Users\kostu\.m2\repository\org\checkerframework\checker-qual\3.48.3\checker-qual-3.48.3.jar;C:\Users\kostu\.m2\repository\org\liquibase\liquibase-core\4.29.2\liquibase-core-4.29.2.jar;C:\Users\kostu\.m2\repository\com\opencsv\opencsv\5.9\opencsv-5.9.jar;C:\Users\kostu\.m2\repository\org\yaml\snakeyaml\2.3\snakeyaml-2.3.jar;C:\Users\kostu\.m2\repository\javax\xml\bind\jaxb-api\2.3.1\jaxb-api-2.3.1.jar;C:\Users\kostu\.m2\repository\org\apache\commons\commons-collections4\4.4\commons-collections4-4.4.jar;C:\Users\kostu\.m2\repository\org\apache\commons\commons-text\1.12.0\commons-text-1.12.0.jar;C:\Users\kostu\.m2\repository\org\apache\commons\commons-lang3\3.17.0\commons-lang3-3.17.0.jar;C:\Users\kostu\.m2\repository\commons-io\commons-io\2.18.0\commons-io-2.18.0.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-logging\3.4.2\spring-boot-starter-logging-3.4.2.jar;C:\Users\kostu\.m2\repository\ch\qos\logback\logback-classic\1.5.16\logback-classic-1.5.16.jar;C:\Users\kostu\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.24.3\log4j-to-slf4j-2.24.3.jar;C:\Users\kostu\.m2\repository\org\apache\logging\log4j\log4j-api\2.24.3\log4j-api-2.24.3.jar;C:\Users\kostu\.m2\repository\org\slf4j\jul-to-slf4j\2.0.16\jul-to-slf4j-2.0.16.jar;C:\Users\kostu\.m2\repository\ch\qos\logback\db\logback-classic-db\1.2.11.1\logback-classic-db-1.2.11.1.jar;C:\Users\kostu\.m2\repository\ch\qos\logback\logback-core\1.2.11\logback-core-1.2.11-tests.jar;C:\Users\kostu\.m2\repository\ch\qos\logback\logback-core\1.5.16\logback-core-1.5.16.jar;C:\Users\kostu\.m2\repository\ch\qos\logback\db\logback-core-db\1.2.11.1\logback-core-db-1.2.11.1.jar;C:\Users\kostu\.m2\repository\org\mapstruct\mapstruct\1.6.3\mapstruct-1.6.3.jar;C:\Users\kostu\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\4.0.2\jakarta.xml.bind-api-4.0.2.jar;C:\Users\kostu\.m2\repository\jakarta\activation\jakarta.activation-api\2.1.3\jakarta.activation-api-2.1.3.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-core\6.2.2\spring-core-6.2.2.jar;C:\Users\kostu\.m2\repository\org\springframework\spring-jcl\6.2.2\spring-jcl-6.2.2.jar;C:\Users\kostu\.m2\repository\com\h2database\h2\2.3.232\h2-2.3.232.jar;C:\Users\kostu\.m2\repository\org\springdoc\springdoc-openapi-starter-webmvc-ui\2.8.3\springdoc-openapi-starter-webmvc-ui-2.8.3.jar;C:\Users\kostu\.m2\repository\org\springdoc\springdoc-openapi-starter-webmvc-api\2.8.3\springdoc-openapi-starter-webmvc-api-2.8.3.jar;C:\Users\kostu\.m2\repository\org\springdoc\springdoc-openapi-starter-common\2.8.3\springdoc-openapi-starter-common-2.8.3.jar;C:\Users\kostu\.m2\repository\io\swagger\core\v3\swagger-core-jakarta\2.2.27\swagger-core-jakarta-2.2.27.jar;C:\Users\kostu\.m2\repository\io\swagger\core\v3\swagger-annotations-jakarta\2.2.27\swagger-annotations-jakarta-2.2.27.jar;C:\Users\kostu\.m2\repository\io\swagger\core\v3\swagger-models-jakarta\2.2.27\swagger-models-jakarta-2.2.27.jar;C:\Users\kostu\.m2\repository\jakarta\validation\jakarta.validation-api\3.0.2\jakarta.validation-api-3.0.2.jar;C:\Users\kostu\.m2\repository\com\fasterxml\jackson\dataformat\jackson-dataformat-yaml\2.18.2\jackson-dataformat-yaml-2.18.2.jar;C:\Users\kostu\.m2\repository\org\webjars\swagger-ui\5.18.2\swagger-ui-5.18.2.jar;C:\Users\kostu\.m2\repository\org\webjars\webjars-locator-lite\1.0.1\webjars-locator-lite-1.0.1.jar;C:\Users\kostu\.m2\repository\org\jspecify\jspecify\1.0.0\jspecify-1.0.0.jar;C:\Users\kostu\.m2\repository\io\swagger\core\v3\swagger-annotations\2.2.28\swagger-annotations-2.2.28.jar;C:\Users\kostu\.m2\repository\org\springframework\boot\spring-boot-starter-thymeleaf\3.4.2\spring-boot-starter-thymeleaf-3.4.2.jar;C:\Users\kostu\.m2\repository\org\thymeleaf\thymeleaf-spring6\3.1.3.RELEASE\thymeleaf-spring6-3.1.3.RELEASE.jar;C:\Users\kostu\.m2\repository\org\thymeleaf\thymeleaf\3.1.3.RELEASE\thymeleaf-3.1.3.RELEASE.jar;C:\Users\kostu\.m2\repository\org\attoparser\attoparser\2.0.7.RELEASE\attoparser-2.0.7.RELEASE.jar;C:\Users\kostu\.m2\repository\org\unbescape\unbescape\1.1.6.RELEASE\unbescape-1.1.6.RELEASE.jar;C:\Program Files\GIGA IDE\GIGA IDE Community Edition 2024.2\lib\idea_rt.jar" ru.hogwarts.school.SchoolApplication
Connected to the target VM, address: '127.0.0.1:59133', transport: 'socket'
19:08:02,937 |-INFO in ch.qos.logback.classic.LoggerContext[default] - This is logback-classic version 1.5.16
19:08:02,939 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Here is a list of configurators discovered as a service, by rank:
19:08:02,939 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 -   org.springframework.boot.logging.logback.RootLogLevelConfigurator
19:08:02,939 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - They will be invoked in order until ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY is returned.
19:08:02,939 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Constructed configurator of type class org.springframework.boot.logging.logback.RootLogLevelConfigurator
19:08:02,947 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - org.springframework.boot.logging.logback.RootLogLevelConfigurator.configure() call lasted 0 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY
19:08:02,947 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Trying to configure with ch.qos.logback.classic.joran.SerializedModelConfigurator
19:08:02,948 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Constructed configurator of type class ch.qos.logback.classic.joran.SerializedModelConfigurator
19:08:02,950 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.scmo]
19:08:02,950 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.scmo]
19:08:02,950 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - ch.qos.logback.classic.joran.SerializedModelConfigurator.configure() call lasted 2 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY
19:08:02,950 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Trying to configure with ch.qos.logback.classic.util.DefaultJoranConfigurator
19:08:02,950 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Constructed configurator of type class ch.qos.logback.classic.util.DefaultJoranConfigurator
19:08:02,951 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
19:08:02,951 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.xml]
19:08:02,951 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - ch.qos.logback.classic.util.DefaultJoranConfigurator.configure() call lasted 1 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY
19:08:02,951 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Trying to configure with ch.qos.logback.classic.BasicConfigurator
19:08:02,952 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - Constructed configurator of type class ch.qos.logback.classic.BasicConfigurator
19:08:02,952 |-INFO in ch.qos.logback.classic.BasicConfigurator@63a5d002 - Setting up default configuration.
19:08:02,969 |-INFO in ch.qos.logback.core.ConsoleAppender[console] - BEWARE: Writing to the console can be very slow. Avoid logging to the
19:08:02,969 |-INFO in ch.qos.logback.core.ConsoleAppender[console] - console in production environments, especially in high volume systems.
19:08:02,969 |-INFO in ch.qos.logback.core.ConsoleAppender[console] - See also https://logback.qos.ch/codes.html#slowConsole
19:08:02,969 |-INFO in ch.qos.logback.classic.util.ContextInitializer@11ce2e22 - ch.qos.logback.classic.BasicConfigurator.configure() call lasted 17 milliseconds. ExecutionStatus=NEUTRAL
19:08:03,320 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [DB]
19:08:03,320 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.classic.db.DBAppender]
19:08:03,329 |-INFO in ch.qos.logback.core.model.processor.ModelInterpretationContext@13cda7c9 - value "jdbc:postgresql://localhost:5432/SCHOOL" substituted for "${SPRING_DATASOURCE_URL}"
19:08:03,329 |-INFO in ch.qos.logback.core.model.processor.ModelInterpretationContext@13cda7c9 - value "schooladmin" substituted for "${SPRING_DATASOURCE_USERNAME}"
19:08:03,329 |-INFO in ch.qos.logback.core.model.processor.ModelInterpretationContext@13cda7c9 - value "12345678" substituted for "${SPRING_DATASOURCE_PASSWORD}"
19:08:03,489 |-INFO in ch.qos.logback.core.db.DriverManagerConnectionSource@5aa6202e - Driver name=PostgreSQL JDBC Driver
19:08:03,489 |-INFO in ch.qos.logback.core.db.DriverManagerConnectionSource@5aa6202e - Driver version=42.7.5
19:08:03,489 |-INFO in ch.qos.logback.core.db.DriverManagerConnectionSource@5aa6202e - supportsGetGeneratedKeys=true
19:08:03,491 |-WARN in ch.qos.logback.core.model.processor.ImplicitModelHandler - Ignoring unknown property [sqlDialect] in [ch.qos.logback.classic.db.DBAppender]
19:08:03,492 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [STDOUT]
19:08:03,492 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
19:08:03,493 |-INFO in ch.qos.logback.core.model.processor.ImplicitModelHandler - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
19:08:03,509 |-INFO in ch.qos.logback.core.ConsoleAppender[STDOUT] - BEWARE: Writing to the console can be very slow. Avoid logging to the
19:08:03,509 |-INFO in ch.qos.logback.core.ConsoleAppender[STDOUT] - console in production environments, especially in high volume systems.
19:08:03,509 |-INFO in ch.qos.logback.core.ConsoleAppender[STDOUT] - See also https://logback.qos.ch/codes.html#slowConsole
19:08:03,509 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [FILE]
19:08:03,509 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.core.FileAppender]
19:08:03,510 |-INFO in ch.qos.logback.core.model.processor.ModelInterpretationContext@13cda7c9 - value "c:/temp/logs/school.log" substituted for "${LOG_ROOT}/${LOG_APP_NAME}.log"
19:08:03,510 |-WARN in ch.qos.logback.core.model.processor.ImplicitModelHandler - Ignoring unknown property [rollingPolicy] in [ch.qos.logback.core.FileAppender]
19:08:03,510 |-INFO in ch.qos.logback.core.model.processor.ImplicitModelHandler - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
19:08:03,510 |-INFO in ch.qos.logback.core.FileAppender[FILE] - File property is set to [c:/temp/logs/school.log]
19:08:03,511 |-INFO in ch.qos.logback.classic.model.processor.LoggerModelHandler - Setting level of logger [ru.hogwarts.school.controller.advice] to TRACE
19:08:03,511 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@3af9aa66 - Propagating TRACE level on Logger[ru.hogwarts.school.controller.advice] onto the JUL framework
19:08:03,512 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [DB] to Logger[ru.hogwarts.school.controller.advice]
19:08:03,512 |-INFO in ch.qos.logback.classic.model.processor.LoggerModelHandler - Setting level of logger [ru.hogwarts.school.service] to TRACE
19:08:03,512 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@3af9aa66 - Propagating TRACE level on Logger[ru.hogwarts.school.service] onto the JUL framework
19:08:03,512 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [DB] to Logger[ru.hogwarts.school.service]
19:08:03,512 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [ASYNC_FILE]
19:08:03,512 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.classic.AsyncAppender]
19:08:03,513 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [FILE] to ch.qos.logback.classic.AsyncAppender[ASYNC_FILE]
19:08:03,513 |-INFO in ch.qos.logback.classic.AsyncAppender[ASYNC_FILE] - Attaching appender named [FILE] to AsyncAppender.
19:08:03,514 |-INFO in ch.qos.logback.classic.AsyncAppender[ASYNC_FILE] - Setting discardingThreshold to 51
19:08:03,514 |-INFO in ch.qos.logback.classic.model.processor.RootLoggerModelHandler - Setting level of ROOT logger to WARN
19:08:03,514 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@3af9aa66 - Propagating WARN level on Logger[ROOT] onto the JUL framework
19:08:03,514 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [STDOUT] to Logger[ROOT]
19:08:03,514 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [ASYNC_FILE] to Logger[ROOT]
19:08:03,515 |-INFO in ch.qos.logback.core.model.processor.DefaultProcessor@771158fb - End of configuration.
19:08:03,515 |-INFO in org.springframework.boot.logging.logback.SpringBootJoranConfigurator@91c4a3f - Registering current configuration as safe fallback point


.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/

:: Spring Boot ::                (v3.4.2)

19:08:05.450 [main] WARN  o.s.b.w.s.c.AnnotationConfigServletWebServerApplicationContext - Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: Failed to initialize dependency 'liquibase' of LoadTimeWeaverAware bean 'entityManagerFactory': Error creating bean with name 'liquibase' defined in class path resource [org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class]: liquibase.exception.CommandExecutionException: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
19:08:05.526 [main] ERROR o.s.boot.SpringApplication - Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: Failed to initialize dependency 'liquibase' of LoadTimeWeaverAware bean 'entityManagerFactory': Error creating bean with name 'liquibase' defined in class path resource [org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class]: liquibase.exception.CommandExecutionException: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:325)
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:204)
at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:970)
at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:627)
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146)
at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:752)
at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:439)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:318)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1361)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1350)
at ru.hogwarts.school.SchoolApplication.main(SchoolApplication.java:11)
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'liquibase' defined in class path resource [org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class]: liquibase.exception.CommandExecutionException: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1812)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:601)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:523)
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:336)
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:307)
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:334)
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:312)
... 10 common frames omitted
Caused by: liquibase.exception.UnexpectedLiquibaseException: liquibase.exception.CommandExecutionException: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at liquibase.integration.spring.SpringLiquibase.afterPropertiesSet(SpringLiquibase.java:267)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1859)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1808)
... 17 common frames omitted
Caused by: liquibase.exception.CommandExecutionException: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at liquibase.command.CommandScope.execute(CommandScope.java:258)
at liquibase.Liquibase.lambda$update$0(Liquibase.java:216)
at liquibase.Scope.lambda$child$0(Scope.java:191)
at liquibase.Scope.child(Scope.java:200)
at liquibase.Scope.child(Scope.java:190)
at liquibase.Scope.child(Scope.java:169)
at liquibase.Liquibase.runInScope(Liquibase.java:1329)
at liquibase.Liquibase.update(Liquibase.java:205)
at liquibase.Liquibase.update(Liquibase.java:188)
at liquibase.integration.spring.SpringLiquibase.performUpdate(SpringLiquibase.java:305)
at liquibase.integration.spring.SpringLiquibase.lambda$afterPropertiesSet$0(SpringLiquibase.java:257)
at liquibase.Scope.lambda$child$0(Scope.java:191)
at liquibase.Scope.child(Scope.java:200)
at liquibase.Scope.child(Scope.java:190)
at liquibase.Scope.child(Scope.java:169)
at liquibase.Scope.child(Scope.java:257)
at liquibase.integration.spring.SpringLiquibase.afterPropertiesSet(SpringLiquibase.java:250)
... 19 common frames omitted
Caused by: liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at liquibase.executor.jvm.ChangelogJdbcMdcListener.execute(ChangelogJdbcMdcListener.java:40)
at liquibase.changelog.StandardChangeLogHistoryService.init(StandardChangeLogHistoryService.java:275)
at liquibase.command.core.helpers.DatabaseChangelogCommandStep.checkLiquibaseTables(DatabaseChangelogCommandStep.java:141)
at liquibase.command.core.helpers.DatabaseChangelogCommandStep.run(DatabaseChangelogCommandStep.java:91)
at liquibase.command.CommandScope.execute(CommandScope.java:220)
... 35 common frames omitted
Caused by: liquibase.exception.DatabaseException: ОШИБКА: отношение "databasechangelog" уже существует [Failed SQL: (0) CREATE TABLE public.databasechangelog (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL, ORDEREXECUTED INTEGER NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))]
at liquibase.executor.jvm.JdbcExecutor$ExecuteStatementCallback.doInStatement(JdbcExecutor.java:497)
at liquibase.executor.jvm.JdbcExecutor.execute(JdbcExecutor.java:83)
at liquibase.executor.jvm.JdbcExecutor.execute(JdbcExecutor.java:185)
at liquibase.executor.jvm.JdbcExecutor.execute(JdbcExecutor.java:153)
at liquibase.changelog.StandardChangeLogHistoryService.lambda$init$1(StandardChangeLogHistoryService.java:275)
at liquibase.executor.jvm.ChangelogJdbcMdcListener.lambda$execute$0(ChangelogJdbcMdcListener.java:33)
at liquibase.Scope.lambda$child$0(Scope.java:191)
at liquibase.Scope.child(Scope.java:200)
at liquibase.Scope.child(Scope.java:190)
at liquibase.Scope.child(Scope.java:169)
at liquibase.executor.jvm.ChangelogJdbcMdcListener.execute(ChangelogJdbcMdcListener.java:32)
... 39 common frames omitted
Caused by: org.postgresql.util.PSQLException: ОШИБКА: отношение "databasechangelog" уже существует
at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2733)
at org.postgresql.core.v3.QueryExecutorImpl.processResults(QueryExecutorImpl.java:2420)
at org.postgresql.core.v3.QueryExecutorImpl.execute(QueryExecutorImpl.java:372)
at org.postgresql.jdbc.PgStatement.executeInternal(PgStatement.java:517)
at org.postgresql.jdbc.PgStatement.execute(PgStatement.java:434)
at org.postgresql.jdbc.PgStatement.executeWithFlags(PgStatement.java:356)
at org.postgresql.jdbc.PgStatement.executeCachedSql(PgStatement.java:341)
at org.postgresql.jdbc.PgStatement.executeWithFlags(PgStatement.java:317)
at org.postgresql.jdbc.PgStatement.execute(PgStatement.java:312)
at com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:94)
at com.zaxxer.hikari.pool.HikariProxyStatement.execute(HikariProxyStatement.java)
at liquibase.executor.jvm.JdbcExecutor$ExecuteStatementCallback.doInStatement(JdbcExecutor.java:491)
... 49 common frames omitted
Disconnected from the target VM, address: '127.0.0.1:59133', transport: 'socket'

Process finished with exit code 1
------------------------------------------------------------------------------------------------------

после этого, чтобы снова всё работало, руками делаю

delete from databasechangelog;
delete from databasechangeloglock;

drop table databasechangelog;
drop table databasechangeloglock;

drop index faculty_name_color_index;
drop index student_name_index;

и история повторяется.
что я делаю не так с liquibase?