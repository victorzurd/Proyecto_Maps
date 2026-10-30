package es.uji.wise.maps.service

import es.uji.wise.maps.model.Session

class SessionManager(initialSession: Session? = null) {
    var sesionActiva: Session? = initialSession
}

