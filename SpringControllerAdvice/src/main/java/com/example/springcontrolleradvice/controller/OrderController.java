package com.example.springcontrolleradvice.controller;


import com.example.springcontrolleradvice.controller.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

//    @GetMapping("/{orderId}")
//    public ResponseEntity<CommonResponse> getOrder(@PathVariable String orderId) {
//        return ResponseEntity.ok(new CommonResponse("success", "order found", orderId));
//    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse> getOrder(@PathVariable String orderId) {
        if (!orderId.equals("100")) {
//            throw new Exception("order not found");
            throw new CustomException("I Can't Find Order", "ERROR400", "please check the order id");
        }
        return ResponseEntity.ok(new CommonResponse("success", "order found", orderId));
    }

}
