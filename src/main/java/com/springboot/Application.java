package com.springboot;

import database.tables.AccessTokenTable;
import database.tables.UserTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {
	public static void main(String[] args){

		try (UserTable userDAO = new UserTable()){
			try (AccessTokenTable accessTokenDAO = new AccessTokenTable(userDAO)) {
				accessTokenDAO.
			}
		}


		SpringApplication.run(Application.class, args);
	}
}
