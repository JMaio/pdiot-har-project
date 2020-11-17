# DefaultApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getFullData**](DefaultApi.md#getFullData) | **GET** /data | 
[**getRespeckData**](DefaultApi.md#getRespeckData) | **GET** /respeck/{respeck_mac} | 
[**getRespeckStreamedData**](DefaultApi.md#getRespeckStreamedData) | **GET** /respeck/stream/{respeck_mac} | 
[**postRespeckData**](DefaultApi.md#postRespeckData) | **POST** /respeck/{respeck_mac} | 



## getFullData

> getFullData()



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
try {
    apiInstance.getFullData();
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getFullData");
    e.printStackTrace();
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


## getRespeckData

> RespeckData getRespeckData(respeckMac, xFields)



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String respeckMac = null; // String | 
String xFields = null; // String | An optional fields mask
try {
    RespeckData result = apiInstance.getRespeckData(respeckMac, xFields);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getRespeckData");
    e.printStackTrace();
}
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **String**|  | [default to null]
 **xFields** | **String**| An optional fields mask | [optional] [default to null]

### Return type

[**RespeckData**](RespeckData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getRespeckStreamedData

> List&lt;BigDecimal&gt; getRespeckStreamedData(respeckMac)



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String respeckMac = null; // String | 
try {
    List<BigDecimal> result = apiInstance.getRespeckStreamedData(respeckMac);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getRespeckStreamedData");
    e.printStackTrace();
}
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **String**|  | [default to null]

### Return type

[**List&lt;BigDecimal&gt;**](BigDecimal.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## postRespeckData

> RespeckPrediction postRespeckData(respeckMac, payload, xFields)



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String respeckMac = null; // String | 
RespeckData payload = new RespeckData(); // RespeckData | 
String xFields = null; // String | An optional fields mask
try {
    RespeckPrediction result = apiInstance.postRespeckData(respeckMac, payload, xFields);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#postRespeckData");
    e.printStackTrace();
}
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **String**|  | [default to null]
 **payload** | [**RespeckData**](RespeckData.md)|  |
 **xFields** | **String**| An optional fields mask | [optional] [default to null]

### Return type

[**RespeckPrediction**](RespeckPrediction.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

