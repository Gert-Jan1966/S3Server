package nl.ou.s3server.controller

import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

/**
 * 
 */
@CompileStatic
class RefererHandlerInterceptor extends HandlerInterceptorAdapter {
    
    /**
     * Kijkt of HTTP request geaccepteerd kan worden. 
     */
    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        true
    }

}
