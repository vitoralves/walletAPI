package com.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.response.Response;

@RestController
@RequestMapping("hello-world")
public class HelloWorldController {
	
	@GetMapping
	public ResponseEntity<Response<String>> get() {
		Response<String> response = new Response<String>();
		response.setData("Hello World!!!!!!  ");
		return ResponseEntity.ok().body(response);
	}

}
