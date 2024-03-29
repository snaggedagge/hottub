/**
 * Hottub API
 * API for hottub
 *
 * The version of the OpenAPI document: 1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface Statistics { 
    /**
     * Total amount of hours on heater
     */
    heaterHours: number;
    /**
     * Total amount of hours on heater since hot tub started.
     */
    heaterHoursSinceStart?: number;
    /**
     * Total amount of hours on circulation pump
     */
    circulationPumpHours: number;
    /**
     * Total amount of hours where temperature has been good for bathing
     */
    effectiveBathTimeHours: number;
}

