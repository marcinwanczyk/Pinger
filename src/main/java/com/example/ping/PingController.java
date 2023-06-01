package com.example.ping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/")
public class PingController {
    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping("/ping")
    public String pingingPage(Model model) {
        model.addAttribute("pingStatus", pingService.getPingStatus());
        return "ping";
    }

    @PostMapping("/ping/start")
    public String startPinging(Model model) {
        model.addAttribute("message", pingService.startPinging());
        model.addAttribute("pingStatus", pingService.getPingStatus());
        return "redirect:/ping";
    }

    @PostMapping("/ping/stop")
    public String stopPinging(Model model){
        pingService.stopPinging();
        model.addAttribute("message", pingService.getPingStatus());
        return "redirect:/ping";
    }
}


