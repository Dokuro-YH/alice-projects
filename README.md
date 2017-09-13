Alice Projects
==============
A basic micro service architecture

Config server
=============
Centralized management configuration. [spring-cloud-config](http://cloud.spring.io/spring-cloud-config/)

Eureka server
=============
Eureka server for [spring-cloud-netflix](https://cloud.spring.io/spring-cloud-netflix/)

After running, see this url [http://localhost:8761](http://localhost:8761)

Admin UI
========
Integrated [spring-boot-admin](http://codecentric.github.io/spring-boot-admin/1.5.4)

After running, see this url [http://localhost:9999](http://localhost:9999) username: admin, password: pass

Turbine
=======
Hystrix Turbine port 8989 for [spring-admin-turbine-ui](http://codecentric.github.io/spring-boot-admin/1.5.4/#_turbine_ui_module)

UAA
===
User Account and Authentication(UAA) Server

| Method | Path | Description | Authority |
| --- | --- | --- | --- |
| GET | /uaa/login | Login page | any |
| GET | /uaa/login/github | Github login | any |
| GET | /uaa/oauth/authorize | Authorization page | any |
| GET | /uaa/oauth/confirm_access | Access confirmation | any |
| GET | /uaa/me | User info api | authenticated |
| GET,POST,PUT,DELETE | /uaa/users/** | Users management endpoints | uaa.admin |
| GET,POST,PUT,DELETE| /uaa/oauth/clients/** | Clients management endpoints | uaa.admin |

Hello
=====
A sample resource server

| Method | Path | Description | Authority |
| --- | --- | --- | --- |
| GET | /sayHello | {id:&lt;uuid&gt;, content: hello &lt;username&gt;} | authenticated |


Employee
========
Employee management RESTful API resource server for [spring-boot-data-rest](https://docs.spring.io/spring-data/rest/docs/current/reference/html)

| Method | Path | Description | Authority |
| --- | --- | --- | --- |
| GET | /employee/** | RESTful API | authenticated |

UI
==
react + react-router + Spring cloud netflix zuul proxy

#### Page Route
| Path | Description | Authority |
| --- | --- | --- |
| /login | redirect uaa/oauth/authorize | any |
| / | index page | authenticated |
| /employee | employee management page | authenticated |

#### API Route
| Path | Proxy service | Strip Prefix |
| --- | --- | --- |
| /api/hello/** | hello service | false |
| /api/employee/** | employee service | true |

Quick Start
===========

### Config server[8888]
```
$ cd ./configserver && ./mvnw spring-boot:run
```

### Eureka server[8761]
```
$ cd ./eureka && ./mvnw spring-boot:run
```

### Admin UI[9999] (Optional)
```
$ cd ./admin && ./mvnw spring-boot:run
```

### Turbine[8989] (Optional)
```
$ cd ./turbine && ./mvnw spring-boot:run
```

### UAA server[9000]
```
$ cd ./uaa && ./mvnw spring-boot:run
```

### Hello server[9001]
```
$ cd ./hello && ./mvnw spring-boot:run
```

### Employee server[9002]
```
$ cd ./employee && ./mvnw spring-boot:run
```

### UI server[8080]
```
$ cd ./ui && ./mvnw spring-boot:run
```