package com.focuslibrary.focus_library.exceptions;

public class InvalidRefreshToken extends FocusLibraryException {
    public InvalidRefreshToken() {
        super("Refresh token is invalid");
    }
}
