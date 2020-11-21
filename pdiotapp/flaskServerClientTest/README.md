# org.openapitools.client - Kotlin client library for API

## Requires

* Kotlin 1.3.61
* Gradle 4.9

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in OpenAPI definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost/api/v1*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**getFullData**](docs/DefaultApi.md#getfulldata) | **GET** /data | 
*DefaultApi* | [**getRespeckData**](docs/DefaultApi.md#getrespeckdata) | **GET** /respeck/{respeck_mac} | 
*DefaultApi* | [**getRespeckStreamedData**](docs/DefaultApi.md#getrespeckstreameddata) | **GET** /respeck/stream/{respeck_mac} | 
*DefaultApi* | [**postRespeckData**](docs/DefaultApi.md#postrespeckdata) | **POST** /respeck/{respeck_mac} | 


<a name="documentation-for-models"></a>
## Documentation for Models

 - [org.openapitools.client.models.RespeckData](docs/RespeckData.md)
 - [org.openapitools.client.models.RespeckPrediction](docs/RespeckPrediction.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
