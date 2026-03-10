package com.spring_boot_tutorial.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring_boot_tutorial.app.models.Empleados;


@Controller
public class EjemploController {

    @GetMapping("/")
    @ResponseBody
    public String root(@RequestParam (required=false) String param) {
        return "<h1>I got no root's</h1>";
    }

    @GetMapping("/message")
    @ResponseBody
    public String getMethodName(@RequestParam (required=false) String param) {
        return "<h1>Hi there! welcome to My Spring Boot App</h1>";
    }

    @GetMapping("/home")
    public String home(){
        return "home";
    }
    
    @GetMapping("/info")
    public String info(Model model){
        Empleados empleado1 = new Empleados(1, "Alexi", "Duran", "Calle 107", "Full Stack", 20, 345678);
        model.addAttribute("empleado", empleado1);
        model.addAttribute("appName", "Spring App");
        model.addAttribute("description", "First application in spring boot");
        return "details_info";
    }
    
}
