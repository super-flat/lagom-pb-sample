# lagom-pb-sample

![BUILD](https://github.com/super-flat/lagom-pb-sample/workflows/BUILD/badge.svg)

The goal of this project is to have a sample project fully functional built on top of [lagom-pb](https://github.com/super-flat/lagom-pb). 
The project is a mini banking microservice. It offers the following features via REST endpoint and gRPC. The project is bundled with all the
necessary setup required to run it locally.

## Features

* Open an Account 

* Transfer money from an Account

* Fetch the details of an Account

## Implementation 

The project is composed of three sbt modules:

* `account-api` defines the REST API service definition

* `account-common` defines the apis requests and responses, the commands, the events, the state and the gRPC
service definition

* `account` is the implementation

## Usage

* Install docker on your machine if not yet

* Clone this repo locally

* From the root of the cloned repo just run `sbt clean dockerComposeUp` and Enjoy :)
