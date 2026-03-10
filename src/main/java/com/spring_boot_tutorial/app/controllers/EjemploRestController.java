package com.spring_boot_tutorial.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_boot_tutorial.app.dto.ClaseDTO;
import com.spring_boot_tutorial.app.models.Empleados;


@RestController
@RequestMapping("/api")
public class EjemploRestController {
    
    @GetMapping("/detalles_info2")
    public Map<String, Object> data(){
        Empleados empleado1 = new Empleados(1, "Alexi", "Duran", "Calle 107", "Full Stack", 20, 345678);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("Empleado", empleado1);
        return respuesta;
    }

    @GetMapping("/detalles_info3")
    public ClaseDTO data2(){
        ClaseDTO user1 = new ClaseDTO();
        user1.setTitle("Administrador");
        user1.setUser("Dg Software");
        return user1;
    }
    
}
