# Keep line numbers for crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembernames interface * {
    @retrofit2.http.* <methods>;
}

# Gson / JSON serialization (API request/response models)
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# OBD library
-keep class com.pnuema.android.obd.** { *; }

# App models (keep for Gson deserialization)
-keep class com.squillaci.autodiag.data.model.** { *; }
-keep class com.squillaci.autodiag.domain.model.** { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# EvalEx (expression evaluator used by OBD library) - uses Lombok annotations at compile time only
-dontwarn lombok.Generated