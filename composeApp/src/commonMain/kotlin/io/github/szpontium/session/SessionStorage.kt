package io.github.szpontium.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.szpontium.api.hebe.SzpontHebeCeApi
import io.github.szpontium.api.hebe.SzpontHebeApi
import io.github.szpontium.api.hebe.credentials.RsaCredential
import io.github.szpontium.api.hebe.models.Account
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

class SessionStorage(
    private val dataStore: DataStore<Preferences>,
    private val httpClient: HttpClient
) {
    private val credentialKey = stringPreferencesKey("session_credential")
    private val accountsKey = stringPreferencesKey("session_accounts")

    suspend fun save(
        apiType: String,
        credential: RsaCredential,
        accounts: List<Account>,
        pLogin: String? = null,
        pPassword: String? = null,
        pTenant: String? = null
    ) {

        val stored = StoredCredential(
            apiType = apiType,
            type = credential.type,
            restUrl = credential.restUrl,
            certificate = credential.certificate,
            privateKey = credential.privateKey,
            fingerprint = credential.fingerprint,
            notificationToken = credential.notificationToken,
            deviceId = credential.deviceId,
            deviceOs = credential.deviceOs,
            deviceModel = credential.deviceModel,
            prometheusLogin = pLogin,
            prometheusPassword = pPassword,
            prometheusTenant = pTenant
        )
        dataStore.edit { prefs ->
            prefs[credentialKey] = json.encodeToString(stored)
            prefs[accountsKey] = json.encodeToString(ListSerializer(Account.serializer()), accounts)
        }
    }

    suspend fun saveLibrus(
        email: String,
        password: String,
        portalToken: String,
        apiToken: String,
        accounts: List<Account>,
        synergiaAccounts: List<io.github.szpontium.api.librus.models.LibrusSynergiaAccount>
    ) {
        val stored = StoredCredential(
            apiType = "librus",
            type = "librus",
            restUrl = "librus",
            certificate = "",
            privateKey = "",
            fingerprint = "",
            notificationToken = null,
            deviceId = "librus",
            deviceOs = "Android",
            deviceModel = "LibrusClient",
            librusEmail = email,
            librusPassword = password,
            librusPortalToken = portalToken,
            librusApiToken = apiToken,
            librusAccountsJson = json.encodeToString(synergiaAccounts)
        )
        dataStore.edit { prefs ->
            prefs[credentialKey] = json.encodeToString(stored)
            prefs[accountsKey] = json.encodeToString(ListSerializer(Account.serializer()), accounts)
        }
    }

    suspend fun restore(session: ApiSession): Boolean {
        val prefs = dataStore.data.first()
        val credentialJson = prefs[credentialKey] ?: return false
        val accountsJson = prefs[accountsKey] ?: return false
        return try {
            val stored = json.decodeFromString<StoredCredential>(credentialJson)
            val accounts = json.decodeFromString(ListSerializer(Account.serializer()), accountsJson)

            if (stored.apiType == "librus") {
                val portalToken = stored.librusPortalToken ?: return false
                val apiToken = stored.librusApiToken ?: return false
                val synergiaAccounts: List<io.github.szpontium.api.librus.models.LibrusSynergiaAccount> = 
                    stored.librusAccountsJson?.let { json.decodeFromString(it) } ?: emptyList()
                val firstAccount = synergiaAccounts.firstOrNull() ?: return false
                
                val librusApi = io.github.szpontium.api.librus.SzpontLibrusApi(
                    httpClient = httpClient,
                    portalAccessToken = portalToken,
                    apiAccessToken = apiToken
                )
                val adapter = io.github.szpontium.api.librus.SzpontLibrusAdapterApi(
                    librusApi = librusApi,
                    currentSynergiaAccount = firstAccount,
                    httpClient = httpClient
                )
                session.setup(adapter, accounts)
                session.librusApi = librusApi
                session.librusAccounts = synergiaAccounts
                session.librusPortalToken = portalToken
                return true
            }

            val credential = RsaCredential(
                type = stored.type,
                restUrl = stored.restUrl,
                certificate = stored.certificate,
                privateKey = stored.privateKey,
                fingerprint = stored.fingerprint,
                notificationToken = stored.notificationToken,
                deviceId = stored.deviceId,
                deviceOs = stored.deviceOs,
                deviceModel = stored.deviceModel
            )
            val api = when (stored.apiType) {
                "hebe_ce" -> SzpontHebeCeApi(credential, httpClient)
                else -> SzpontHebeApi(credential, httpClient)
            }
            session.setup(api, accounts)
            
            if (stored.prometheusLogin != null && stored.prometheusPassword != null && stored.prometheusTenant != null) {
                session.prometheusMessagesApi = io.github.szpontium.api.prometheus.PrometheusMessagesApi(
                    tenant = stored.prometheusTenant,
                    login = stored.prometheusLogin,
                    password = stored.prometheusPassword,
                    initialCookies = null
                )
            } else if (stored.apiType == "hebe_ce") {
                // Stara sesja bez zapisanych danych - wymuś ponowne logowanie
                return false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
