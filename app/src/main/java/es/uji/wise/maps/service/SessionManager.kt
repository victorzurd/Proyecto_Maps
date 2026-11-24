package es.uji.wise.maps.service

import es.uji.wise.maps.model.Session

interface SessionStore {
    fun load(): Session?
    fun save(session: Session?)
}

class SessionManager(
    initialSession: Session? = null,
    private val sessionStore: SessionStore? = null
) {
    var sesionActiva: Session? = initialSession ?: sessionStore?.load()
        set(value) {
            field = value
            sessionStore?.save(value)
        }
}

