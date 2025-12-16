package main;

import controller.UserController;
import model.User;

public class Main {
	
	public Main() {
		UserController uc = new UserController();
		User user = uc.login("admin@gmail.com", "admin123");

		if (user != null) {
		    System.out.println("Login success as " + user.getRole());
		} else {
		    System.out.println("Login failed");
		}

	}

	public static void main(String[] args) {
		new Main();
	}

}
