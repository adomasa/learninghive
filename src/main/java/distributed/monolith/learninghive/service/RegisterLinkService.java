package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class RegisterLinkService {
    @Value("${server:port}")
    private String port;
    @Value("${server:domain}")
    private String domain;

    @PostConstruct
    public String createRegisterLink(User user){
        StringBuffer str = new StringBuffer ("http://");
        str.append(domain);
        str.append("/");
        str.append(port);
        str.append("signupemail/");
        str.append(user.getId());
        return str.toString();
    }
}