# floaty

## Getting Started
This is a simple Spring Boot web application exposing endpoints which can be called by the floaty frontend.

### TL;DR
1) Start the spring boot app (or build the docker image and run the container)
2) Run `curl localhost:8080` or 'curl localhost:8080/users' for local testing 

### Docker
#### Build and run the docker image
```docker build -t matthaeusheer/floaty-backend:latest .```  
```docker run -p 8080:8080 matthaeusheer/floaty-backend:latest```  
```curl loaclhost:8080/users```

#### Build and run via docker-compose
This will enable the possibility to directly run a MySQL database alongside the backend locally.

Build the necessary images using  
```docker-compose build```

Run the services using  
```docker-compose up```

### Using IDEA
#### Possibility 1 - run spring boot locally
Simply run the floaty Application in your IDE

#### Possibility 2 - run via docker-compose
Run the services clicking arrows in docker-compose.yml file.

**How to connect to MySQL database which runs in the db container from within IDEA?**
- Note that 3306 is exposed s.t. it is accessible on localhost
- Go to Database tab on upper right
- Add connection
- Settings -> Host: localhost, Port: 3306 (default), User & Password as configured, Database: floaty-db
 