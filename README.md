# floaty-backend
Java Spring Boot backend for the floaty project.

## Installation
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
Simply run the floaty Application in your IDE.

#### Possibility 2 - run via docker-compose
Run the services clicking arrows in docker-compose.yml file.

**How to connect to MySQL database which runs in the db container from within IDEA?**
- Note that 3306 is exposed s.t. it is accessible on localhost
- Go to Database tab on upper right
- Add connection
- Settings -> Host: localhost, Port: 3306 (default), User & Password as configured, Database: floaty-db

### Raspberry PI Deployment
#### Deployment
* On the Raspberry PI there is a systemd service under `/etc/systemd/system/floaty-backend.service` which looks like:
    ```
    [Unit]
    Description=Floaty Backend Service using Docker Compose
    After=docker.service
    Requires=docker.service
    
    [Service]
    WorkingDirectory=/home/floaty/floaty/floaty-backend
    Restart=always
    ExecStart=/usr/bin/docker compose up
    ExecStop=/usr/bin/docker compose down
    
    [Install]
    WantedBy=multi-user.target
    ```
    which means that as soon as the Raspberry PI is booted, the floaty-backend service will be started.
    It is based on running docker compose from the floaty-backend directory.

#### Networking
* CORS is allowed to be accessed from the frontend by the cors.allowed.origins property in the application.properties file.
This allows traffic from https://app.floatyfly.com/.
* On the Raspberry PI runs a cloudflared (Cloudflare deamon) which tunnels traffic into the machine.
  * The service is called `cloudflared.service` and is located in `/etc/systemd/system/cloudflared.service`.
  * The service's config file is located under `/etc/cloudflared/config.yml` and points to the credentials file / cloudflared config location on the machine.

### DNS
#### Frontend & Backend Services DNS
* The domain floatyfly.com is bought via GoDaddy. The nameservers are set to the ones of Cloudflare.
* On Cloudflare a domain for floatyfly is registered, where:
  * CNAME on "app" is set to the frontend hosting domain on firebase such at app.floatyfly.com resolves to the frontend.
  * CNAME on "test" is set to the test floaty backend on the Raspberry PI such that test.floatyfly.com resolves to the backend.
  * CNAME on "prod" is set to the prod floaty backend on the Raspberry PI such that prod.floatyfly.com resolves to the backend.

#### EMail DNS
* CNAME & MX & TXT: For mailserver and spam protection check the Godaddy Settings under "My Products" -> "FloatyFly" -> "Email" -> "Deine EMail verwalten" (three dots) -> "Email Ziel einrichten" to see all necessary DNS records for EMail.

