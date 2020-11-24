# DefaultApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getFullData**](DefaultApi.md#getFullData) | **GET** data | 
[**getRespeckData**](DefaultApi.md#getRespeckData) | **GET** respeck/{respeck_mac} | 
[**getRespeckStreamedData**](DefaultApi.md#getRespeckStreamedData) | **GET** respeck/stream/{respeck_mac} | 
[**postRespeckData**](DefaultApi.md#postRespeckData) | **POST** respeck/{respeck_mac} | 





### Example
```kotlin
// Import classes:
//import org.openapitools.client.*
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DefaultApi::class.java)

webService.getFullData()
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined




### Example
```kotlin
// Import classes:
//import org.openapitools.client.*
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DefaultApi::class.java)
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 
val xFields : kotlin.String = xFields_example // kotlin.String | An optional fields mask

val result : RespeckData = webService.getRespeckData(respeckMac, xFields)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **kotlin.String**|  |
 **xFields** | **kotlin.String**| An optional fields mask | [optional]

### Return type

[**RespeckData**](RespeckData.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import org.openapitools.client.*
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DefaultApi::class.java)
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 

val result : kotlin.collections.List<java.math.BigDecimal> = webService.getRespeckStreamedData(respeckMac)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **kotlin.String**|  |

### Return type

[**kotlin.collections.List&lt;java.math.BigDecimal&gt;**](java.math.BigDecimal.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import org.openapitools.client.*
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DefaultApi::class.java)
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 
val payload : RespeckData =  // RespeckData | 
val xFields : kotlin.String = xFields_example // kotlin.String | An optional fields mask

val result : RespeckPrediction = webService.postRespeckData(respeckMac, payload, xFields)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **kotlin.String**|  |
 **payload** | [**RespeckData**](RespeckData.md)|  |
 **xFields** | **kotlin.String**| An optional fields mask | [optional]

### Return type

[**RespeckPrediction**](RespeckPrediction.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

