package com.taskservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskservice.config.CustomUserServiceImpl;
import com.taskservice.config.JwtProvider;
import com.taskservice.entity.User;
import com.taskservice.repository.UserRepository;
import com.taskservice.response.AuthResponse;
import com.taskservice.response.LoginResponse;

@RestController
@RequestMapping("/auth")
public class UserController {

	private  UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private CustomUserServiceImpl customUserServiceImpl;
		
	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder,
			CustomUserServiceImpl customUserServiceImpl) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.customUserServiceImpl = customUserServiceImpl;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception{
		
		String email=user.getEmail();
		String password= user.getPassword();
		String fullName= user.getFullName();
		String role= user.getRole();
		
		User isEmailExist = userRepository.findByEmail(email);
		if(isEmailExist!=null) {
			throw new Exception("this email is already exist " + email);
		}
		
		//create new user;
		User createUser = new User();
		createUser.setEmail(email);
		createUser.setPassword(passwordEncoder.encode(password));
		createUser.setFullName(fullName);
		createUser.setRole(role);
		
		User saveUser = userRepository.save(createUser);
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = JwtProvider.genearteToken(authentication);
		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Register Succesfull");
		authResponse.setStatus(true);
		return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);		
	}
	
	@PostMapping("/signIn")
	public ResponseEntity<AuthResponse> signIn (@RequestBody LoginResponse loginResponse){
		String userName = loginResponse.getEmail();
		String password = loginResponse.getPassword();
		
		System.out.println(userName+ "============="+ password);
		
		Authentication authentication= authenticate(userName,password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = JwtProvider.genearteToken(authentication);
		AuthResponse authResponse= new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Success Login");
		authResponse.setStatus(true);
		return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
	}
	
	private Authentication authenticate(String userName, String password) {
		UserDetails userDetails = customUserServiceImpl.loadUserByUsername(userName);
		System.out.println("sign in userdetails"+ userDetails);
		if(userDetails==null) {
			System.out.println("sign in userdetails"+ userDetails);
         throw new BadCredentialsException("Invalid UserName and Password");
		}
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			System.out.println("sign in userdetails"+ userDetails);
	         throw new BadCredentialsException("Invalid UserName and Password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
	}

	
	
}
