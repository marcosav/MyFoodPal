package com.gmail.marcosav2010.myfoodpal.tasks;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfoodpal.R;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "from")
public class SessionRequestResult {

    @Getter
    private Type type;
    @Getter
    private MFPSession result;

    @NoArgsConstructor
    @AllArgsConstructor
    public enum Type {
        LOGIN_ERROR(R.string.session_request_error_login),
        IO_ERROR(R.string.result_io_error),
        NO_INTERNET_ERROR(R.string.result_no_internet_error),
        SUCCESS;

        @Getter
        int msg;
    }
}