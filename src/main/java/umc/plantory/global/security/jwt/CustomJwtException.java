package umc.plantory.global.security.jwt;

public class CustomJwtException extends RuntimeException {
    public CustomJwtException(String message) {
        super(message);
    }
} 