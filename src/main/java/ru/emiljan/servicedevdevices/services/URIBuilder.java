package ru.emiljan.servicedevdevices.services;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class URIBuilder {
    static public URI buildURI(HttpServletRequest request, String path) {
        try {
            URI url = URI.create(request.getRequestURL().toString());
            return new URI(url.getScheme(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    path,
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
