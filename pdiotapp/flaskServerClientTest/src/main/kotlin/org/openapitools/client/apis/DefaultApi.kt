package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody

import org.openapitools.client.models.RespeckData
import org.openapitools.client.models.RespeckPrediction

interface DefaultApi {
    /**
     * 
     * 
     * Responses:
     *  - 200: Success
     * 
     * @return [Call]<[Unit]>
     */
    @GET("data")
    fun getFullData(): Call<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: Respeck data found
     *  - 400: Respeck not found
     * 
     * @param respeckMac  
     * @param xFields An optional fields mask (optional)
     * @return [Call]<[RespeckData]>
     */
    @GET("respeck/{respeck_mac}")
    fun getRespeckData(@Path("respeck_mac") respeckMac: kotlin.String, @Header("X-Fields") xFields: kotlin.String): Call<RespeckData>

    /**
     * 
     * 
     * Responses:
     *  - 200: Streaming Respeck data
     *  - 400: Respeck not found
     * 
     * @param respeckMac  
     * @return [Call]<[kotlin.collections.List<java.math.BigDecimal>]>
     */
    @GET("respeck/stream/{respeck_mac}")
    fun getRespeckStreamedData(@Path("respeck_mac") respeckMac: kotlin.String): Call<kotlin.collections.List<java.math.BigDecimal>>

    /**
     * 
     * 
     * Responses:
     *  - 200: Model prediction
     * 
     * @param respeckMac  
     * @param payload  
     * @param xFields An optional fields mask (optional)
     * @return [Call]<[RespeckPrediction]>
     */
    @POST("respeck/{respeck_mac}")
    fun postRespeckData(@Path("respeck_mac") respeckMac: kotlin.String, @Body payload: RespeckData, @Header("X-Fields") xFields: kotlin.String): Call<RespeckPrediction>

}
