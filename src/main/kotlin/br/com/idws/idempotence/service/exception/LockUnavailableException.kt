package br.com.idws.idempotence.service.exception

class LockUnavailableException(key: String) : RuntimeException("Lock to key: $key is unavailable")