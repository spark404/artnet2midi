package net.strocamp.webui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RootController {

    @RequestMapping("/lalala.htm")
    public ModelAndView rootPage() {
        return new ModelAndView("index", "msg", "test");
    }

}
