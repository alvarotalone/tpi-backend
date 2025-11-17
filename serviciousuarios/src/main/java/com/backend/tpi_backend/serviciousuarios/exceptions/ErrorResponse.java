package com.backend.tpi_backend.serviciousuarios.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String message) {}
