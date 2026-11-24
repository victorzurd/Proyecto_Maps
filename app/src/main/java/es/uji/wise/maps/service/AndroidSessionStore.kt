package es.uji.wise.maps.service

import android.content.Context
import es.uji.wise.maps.model.Session

/** Persiste únicamente el estado de sesión del prototipo en preferencias privadas. */
class AndroidSessionStore(context: Context) : SessionStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun load(): Session? {
        val email = preferences.getString(KEY_EMAIL, null) ?: return null
        return Session(email = email, autenticado = true)
    }

    override fun save(session: Session?) {
        preferences.edit().apply {
            if (session?.autenticado == true) {
                putString(KEY_EMAIL, session.email)
            } else {
                remove(KEY_EMAIL)
            }
        }.apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "wise_maps_session"
        const val KEY_EMAIL = "authenticated_email"
    }
}
