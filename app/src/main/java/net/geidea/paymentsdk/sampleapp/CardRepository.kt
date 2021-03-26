package net.geidea.paymentsdk.sampleapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Local storage of tokenized cards (with sensitive data masked).
 */
object CardRepository {

    private val dataStore: DataStore<Preferences> by lazy { SampleApplication.INSTANCE.createDataStore(name = "dataStore") }

    private val _selectedCard = MutableStateFlow<CardItem?>(null)
    val selectedCard: Flow<CardItem?> = _selectedCard

    /**
     * Flow of currently stored tokenized cards.
     */
    val tokenizedCards: Flow<Map<String, CardItem>>
        get() = dataStore.data
                .map { prefs ->
                    prefs.asMap()
                            .mapKeys { (key, _) -> key.name }
                            .mapValues { (_, value) -> CardItem.fromJson(value as String) }
                }

    /**
     * Add a tokenized card. If the card with the same tokenId is already existing then it will be
     * replaced with [cardItem].
     */
    suspend fun saveCard(cardItem: CardItem) {
        dataStore.edit { prefs -> prefs[preferencesKey(cardItem.tokenId)] = Json.encodeToString(cardItem) }
    }

    /**
     * Returns true if no tokenized cards are stored or false otherwise.
     */
    suspend fun isEmpty(): Boolean = tokenizedCards.firstOrNull()?.isEmpty() ?: true

    /**
     * Clears locally stored tokenized cards relevant only for the current merchant and environment.
     */
    suspend fun clear() {
        dataStore.edit(MutablePreferences::clear)
        _selectedCard.value = null
    }

    fun selectCard(cardItem: CardItem?) {
        _selectedCard.value = cardItem
    }
}

typealias TokenCardMap = Map<String, CardItem>