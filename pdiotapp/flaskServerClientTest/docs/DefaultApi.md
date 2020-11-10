# DefaultApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getFullData**](DefaultApi.md#getFullData) | **GET** /data | 
[**getRespeckData**](DefaultApi.md#getRespeckData) | **GET** /respeck/{respeck_mac} | 
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

> getRespeckData(respeckMac)



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String respeckMac = null; // String | 
try {
    apiInstance.getRespeckData(respeckMac);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getRespeckData");
    e.printStackTrace();
}
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respeckMac** | **String**|  | [default to null]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


## postRespeckData

> postRespeckData(respeckMac, payload)



### Example

```java
// Import classes:
//import org.openapitools.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String respeckMac = null; // String | 
RespeckData payload = new RespeckData(); // RespeckData | 
try {
    apiInstance.postRespeckData(respeckMac, payload);
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

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

