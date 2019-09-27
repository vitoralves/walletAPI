package com.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.response.Response;

@RestController
@RequestMapping("hello-world")
public class HelloWorldController {
	
	public ResponseEntity<Response<String>> get() {
		Response<String> response = new Response<String>();
		response.setData("Hello World!!!!!!  ");
		return ResponseEntity.ok().body(response);
	}

}
