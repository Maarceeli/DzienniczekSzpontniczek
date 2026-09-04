package io.github.szpontium.api.hebe

data class HebeHttpIdentity(
    val appName: String,
    val appVersion: String,
    val appVersionApple: String,
    val appOS: String,
    val appOSApple: String,
    val appUserAgent: String,
    val appUserAgentApple: String,
    val appVersionCode: String,
    val appVersionCodeApple: String
)

val hebeIdentity = HebeHttpIdentity(
    appName = "DzienniczekPlus 2.0",
    appVersion = "26.07.00 (G)",
    appVersionApple = "26.07.00",
    appOS = "Android",
    appOSApple = "iOS",
    appUserAgent = "Dart/3.11 (dart:io)",
    appUserAgentApple = "Dart/3.11 (dart:io)",
    appVersionCode = "988",
    appVersionCodeApple = "988"
)

val hebeCeIdentity = HebeHttpIdentity(
    appName = "DzienniczekPlus 3.0",
    appVersion = "26.06.02 (G)",
    appVersionApple = "26.06.02",
    appOS = "Android",
    appOSApple = "iOS",
    appUserAgent = "Dart/3.11 (dart:io)",
    appUserAgentApple = "Dart/3.11 (dart:io)",
    appVersionCode = "998",
    appVersionCodeApple = "998"
)

fun HebeHttpIdentity.appVersionCode(deviceModel: String) =
    if (isIphone(deviceModel)) appVersionCodeApple else appVersionCode

fun HebeHttpIdentity.appVersion(deviceModel: String) =
    if (isIphone(deviceModel)) appVersionApple else appVersion

fun HebeHttpIdentity.appOS(deviceModel: String) =
    if (isIphone(deviceModel)) appOSApple else appOS

fun HebeHttpIdentity.appUserAgent(deviceModel: String) =
    if (isIphone(deviceModel)) appUserAgentApple else appUserAgent

fun isIphone(deviceModel: String) =
    deviceModel.lowercase().contains("iphone") || deviceModel.lowercase().contains("ios")
