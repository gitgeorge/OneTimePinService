package com.otp.resources;

import com.otp.props.Properties;
import com.otp.request.Request;
import com.otp.request.ValidateRequest;
import com.otp.response.Response;
import com.otp.services.ManagerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/otpManager")
public class Controller {


    @Autowired
    private ManagerInterface manager;

    @Autowired
    private Properties properties;

    Logger log = LoggerFactory.getLogger(Controller.class);

    @PostMapping("/generateOtp")
    public Response save(@Valid @RequestBody Request request)
            throws MethodArgumentNotValidException {

        log.info("Otp request received " + request.toString());

        return manager.generateOtp(request);

    }

    @GetMapping("/vaidateOtp")
    public Response fetch(@Valid @RequestBody ValidateRequest request)
            throws MethodArgumentNotValidException {

        log.info("Validate otp request received " + request.toString());

        return manager.validateOtp(request);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        errors.put("status", properties.getFailedStatus());
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;

    }
}
