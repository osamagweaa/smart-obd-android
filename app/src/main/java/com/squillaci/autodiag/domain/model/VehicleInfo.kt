package com.squillaci.autodiag.domain.model

data class VehicleInfo(
    val vin: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val fuelType: FuelType = FuelType.UNKNOWN,
    val mileageKm: Int? = null,
    val manualName: String? = null
) {
    val displayName: String
        get() = listOfNotNull(make, model, year?.toString()).joinToString(" ")
            .ifBlank { manualName ?: "Mi vehículo" }
}

enum class FuelType {
    GASOLINE,
    DIESEL,
    HYBRID,
    ELECTRIC,
    UNKNOWN
}
