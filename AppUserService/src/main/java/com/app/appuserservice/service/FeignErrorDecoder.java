package com.app.appuserservice.service;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch(response.status()) {
            case 404: {
                if (StringUtils.contains(methodKey, "getAlbums")) {
                    return new ResponseStatusException(HttpStatusCode.valueOf(response.status()), "Albums not found for the user");
                }
                break;
            }
            case 500:
                return new InternalServerErrorException();
            default:
                return new Exception(response.reason());
        }

        return null;
    }
}
