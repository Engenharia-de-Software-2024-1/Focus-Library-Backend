package com.focuslibrary.focus_library.exeptions;

public class InvalidRefreshToken extends FocusLibraryExeption {
    public InvalidRefreshToken() {
        super("Refresh token is invalid");
    }
}
