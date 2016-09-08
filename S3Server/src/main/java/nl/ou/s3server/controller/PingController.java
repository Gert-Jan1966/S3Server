package nl.ou.s3server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RESTful controller voor afhandeling van requests naar /S3Server/ping.
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    /**
     * Levert ping- cq. heartbeatfunctionaliteit. 
     */
    @RequestMapping(method = GET)
    public String ping() {
        return "I'm still alive!";
    }

}
