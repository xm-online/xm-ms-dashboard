[![Build Status](https://travis-ci.org/xm-online/xm-ms-dashboard.svg?branch=master)](https://travis-ci.org/xm-online/xm-ms-dashboard)

# XM MS Dashboard

This application was generated using JHipster 4.6.1, you can find documentation and help at 
[https://jhipster.github.io/documentation-archive/v4.6.1](https://jhipster.github.io/documentation-archive/v4.6.1).

This is a "microservice" application intended to be part of a microservice architecture, please refer to the 
[Doing microservices with JHipster] page of the documentation for more information.

This application is configured for Service Discovery and Configuration with Consul. On launch, it will refuse to start 
if it is not able to connect to Consul at [http://localhost:8500](http://localhost:8500). For more information, read 
documentation on [Service Discovery and Configuration with Consul].

## Development

To start your application in the dev profile, simply run:

    ./gradlew


For further instructions on how to develop with JHipster, have a look at [Using JHipster in development].

## Building for production

To optimize the dashboard application for production, run:

    ./gradlew -Pprod clean bootRepackage

To ensure everything worked, run:

    java -jar build/libs/*.war


Refer to [Using JHipster in production] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test
