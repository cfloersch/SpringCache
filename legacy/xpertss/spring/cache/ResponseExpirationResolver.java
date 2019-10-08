package xpertss.spring.cache;

import org.springframework.http.client.ClientHttpResponse;

import java.util.Date;

public interface ResponseExpirationResolver {

   Date resolveInitialDate(ClientHttpResponse response, Date requestDate, Date responseDate);

   Date resolveExpirationDate(ClientHttpResponse response, Date initialDate);
}
