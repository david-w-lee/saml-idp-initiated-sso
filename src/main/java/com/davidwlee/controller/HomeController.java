package com.davidwlee.controller;

import com.davidwlee.model.SamlConfig;
import com.davidwlee.model.SamlOutputs;
import com.davidwlee.service.SamlService;
import com.davidwlee.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Base64;

@Controller
public class HomeController {

    @Autowired
    private SamlService samlService;

    @Autowired
    private SignatureService signService;

    @RequestMapping("/")
    public String home(Model model) {
        SamlConfig config = samlService.getDefaultSamlConfig();
        SamlOutputs outputs = new SamlOutputs();
        model.addAttribute("config", config);
        model.addAttribute("outputs", outputs);
        return "index";
    }

    @RequestMapping(value = "/processForm", method= RequestMethod.POST)
    public String process(@ModelAttribute(value="config") SamlConfig config, @ModelAttribute(value="outputs") SamlOutputs outputs) {
        outputs.setSamlResponse(samlService.getSamlResponse(config));
        try {
            outputs.setSamlResponseSigned(signService.sign(outputs.getSamlResponse()));
            outputs.setSamlResponseSingedAndBase64Encoded(new String(Base64.getEncoder().encode(outputs.getSamlResponseSigned().getBytes())));
        }
        catch(Exception ex){
        }

        return "index";
    }
}
