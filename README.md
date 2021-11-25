# PROPERSTOCK

## 구동 환경
### Selenium Chrome
```
docker run -d --restart=always -p 4444:4444 -p 7900:7900 -e JAVA_OPTS="-Dwebdriver.chrome.whitelistedIps=" --name selenium-standalone-chrome selenium/standalone-chrome
```

### Docker Registry
```
docker run -d --restart=always -p 5000:5000 --name registry -v registry:2
```

### Mongo DB
```
docker run -d --restart=always -p 27017:27017 --name mongodb-dev -v /data/dv/mongodb-dev/data/db:/data/db -v /etc/localtime:/etc/localtime:ro -e MONGO_INITDB_ROOT_USERNAME= -e MONGO_INITDB_ROOT_PASSWORD= mongo
```
