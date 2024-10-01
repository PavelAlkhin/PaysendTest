package com.paysend.controller;

import com.paysend.dto.PayResponseDto;
import com.paysend.dto.RequestAmountDto;
import com.paysend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class MainController {

    private final PaymentService paymentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectError e;

    public MainController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/")
    private RedirectView home(Model model) {
        return new RedirectView("/payments");
    }

    @GetMapping("/payments")
    private String start(@RequestParam(required = false) List<String> errorMessages, @RequestParam(required = false) List<String> errorMessage, Model model) {
        logger.info("Start. We are here!");
        model.addAttribute("errorMessages", errorMessages);
        model.addAttribute("errorMessage", errorMessage);
        return "payments";
    }

    @RequestMapping(value = "/payments/action", method = RequestMethod.POST, params = "action=postpayment")
    private RedirectView postPayment(Model model,
                                     @Valid RequestAmountDto dto,
                                     BindingResult bindingResult,
                                     RedirectAttributes attributes
    ) {
        logger.info("Post Payment. {}", dto);
        List<String> errorMessageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                errorMessageList.add("\"" + fieldName + "\"" + " - " + error.getDefaultMessage() + "!  ");
            });
            attributes.addAttribute("errorMessages", errorMessageList);
            return new RedirectView("/payments");
        }
        try {
            PayResponseDto resp = paymentService.sendGet(dto);
            Objects.requireNonNull(resp);

            if (resp.errorText() != null && !resp.errorText().isEmpty()) {
                attributes.addAttribute("errorMessage", resp.errorText());
                return new RedirectView("/payments");
            }
            Objects.requireNonNull(resp.result().redirectUrl());
            return new RedirectView(resp.result().redirectUrl());
        } catch (Exception e) {
            logger.error(e.getMessage());
            attributes.addAttribute("errorMessage", e.getMessage());
        }
        return new RedirectView("/payments");
    }
}
