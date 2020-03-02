# Spothero Code Challenge - Aman
## Project Overview

A web server in scala using http4s.

The *core business logic* of the service can be found in 
`src/main/scala/aman/spothero/core`. The `api` package
defines the http service and `persistence` package manages 
file & memory based persistence.

I have also included an undocumented `/metrics` endpoint that tracks the server metrics
and additionally two custom metrics -
 - `pr_success` - tracks successful calculation of rates 
 - `pr_failure` - tracks unsuccessful calculation of rates

The project is written using tagless final programming concepts that is
used by the underlying http4s library.
I recommend the use of the IntelliJ IDE for source browsing.

Please do not hesitate to reach out and ask any questions

## Api

The openapi spec defines the api. It can be found in `openapi.yaml`

- Assumptions
  - The `calculate` `GET` endpoint accepts data in the request body 
  instead of the query params to solve the issue of having the reserved 
  character(`+`) in the acceptable time values instead of some hacky
  solution that handles it in the url query parameter

## Build Instructions

### Compile the project -
`sbt compile`

### Run the tests -
`sbt test`

### Launch the server via sbt at port 8080 -
`sbt service/run`

### Launch the server via docker -

The project uses a sbt plugin to create a docker image for the service. 
Creating an explicit dockerfile is unnecessary and a simple matter of
copying the generated dockerfile if ever required

*Requirements to create a docker image for the server* -
 - Have a docker daemon running
 - Initialize a git repository and make a commit including everything
   in the extracted project

Create a docker image locally with -
`sbt docker:publishLocal`

Once the image is published, run the web service from within docker with -
`docker run -p 8080:8080 <image-id>`

## Testing the api

Query the parking rate calculator endpoint 
```
curl -X GET 'http://localhost:8080/calculate' --data '{"start":"2015-07-01T07:00:00-05:00","end":"2015-07-01T12:00:00-05:00"}'
curl -X GET 'http://localhost:8080/calculate' --data '{"start":"2015-07-04T15:00:00+00:00","end":"2015-07-04T20:00:00+00:00"}'
curl -X GET 'http://localhost:8080/calculate' --data '{"start":"2015-07-04T07:00:00+05:00","end":"2015-07-04T20:00:00+05:00"}'
```

Update the rates 
```
curl -X POST 'http://localhost:8080/rates' --data @rates.json
```