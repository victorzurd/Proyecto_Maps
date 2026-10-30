package es.uji.wise.maps.model

class DuplicateUserException(message: String = "El usuario con este correo ya existe") : RuntimeException(message)
class InvalidCredentialsException(message: String = "Credenciales inválidas") : RuntimeException(message)
class NoActiveSessionException(message: String = "No hay sesión activa para cerrar") : RuntimeException(message)
class UnauthorizedActionException(message: String = "Acción no autorizada") : RuntimeException(message)
class InvalidCoordinatesException(message: String = "Coordenadas fuera de rango válido") : RuntimeException(message)
class DuplicatePlaceException(message: String = "Ya existe un lugar con ese nombre") : RuntimeException(message)

