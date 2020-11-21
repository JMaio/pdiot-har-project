# DefaultApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getFullData**](DefaultApi.md#getFullData) | **GET** /data | 
[**getRespeckData**](DefaultApi.md#getRespeckData) | **GET** /respeck/{respeck_mac} | 
[**getRespeckStreamedData**](DefaultApi.md#getRespeckStreamedData) | **GET** /respeck/stream/{respeck_mac} | 
[**postRespeckData**](DefaultApi.md#postRespeckData) | **POST** /respeck/{respeck_mac} | 


<a name="getFullData"></a>
# **getFullData**
> getFullData()



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = DefaultApi()
try {
    apiInstance.getFullData()
} catch (e: ClientException) {
    println("4xx response calling DefaultApi#getFullData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling DefaultApi#getFullData")
    e.printStackTrace()
}
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

<a name="getRespeckData"></a>
# **getRespeckData**
> RespeckData getRespeckData(respeckMac, xFields)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = DefaultApi()
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 
val xFields : kotlin.String = xFields_example // kotlin.String | An optional fields mask
try {
    val result : RespeckData = apiInstance.getRespeckData(respeckMac, xFields)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling DefaultApi#getRespeckData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling DefaultApi#getRespeckData")
    e.printStackTrace()
}
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

<a name="getRespeckStreamedData"></a>
# **getRespeckStreamedData**
> kotlin.collections.List&lt;java.math.BigDecimal&gt; getRespeckStreamedData(respeckMac)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = DefaultApi()
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 
try {
    val result : kotlin.collections.List<java.math.BigDecimal> = apiInstance.getRespeckStreamedData(respeckMac)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling DefaultApi#getRespeckStreamedData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling DefaultApi#getRespeckStreamedData")
    e.printStackTrace()
}
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

<a name="postRespeckData"></a>
# **postRespeckData**
> RespeckPrediction postRespeckData(respeckMac, payload, xFields)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = DefaultApi()
val respeckMac : kotlin.String = respeckMac_example // kotlin.String | 
val payload : RespeckData =  // RespeckData | 
val xFields : kotlin.String = xFields_example // kotlin.String | An optional fields mask
try {
    val result : RespeckPrediction = apiInstance.postRespeckData(respeckMac, payload, xFields)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling DefaultApi#postRespeckData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling DefaultApi#postRespeckData")
    e.printStackTrace()
}
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

