package tn.esprit.spring.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SwaggerRedirectController {

    // Changer le mapping pour éviter tout conflit avec springdoc
    @GetMapping("/home")
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView("/api/swagger-ui.html");
    }
}
