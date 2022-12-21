package com.modeln.webserver;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {

    /**
     * Capture all path patterns except /api/** and serve index.html to allow client-side routing work on page reload
     * ref: https://stackoverflow.com/questions/47689971/how-to-work-with-react-routers-and-spring-boot-controller
     */
    @RequestMapping(value = { "/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}" })
    public String getIndex(HttpServletRequest request) {
        return "/index.html";
    }

}
