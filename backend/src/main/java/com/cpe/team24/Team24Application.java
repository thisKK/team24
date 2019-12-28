package com.cpe.team24;

import com.cpe.team24.entity.*;
import com.cpe.team24.entity.auth.ERole;
import com.cpe.team24.entity.auth.Role;
import com.cpe.team24.repository.*;
import com.cpe.team24.repository.auth.RoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Team24Application {

	public static void main(String[] args) {
		SpringApplication.run(Team24Application.class, args);
	}

	@Bean
	ApplicationRunner init(FlightBookingRepository flightBookingRepository,
						   BookingStatusRepository bookingStatusRepository, FlightRepository flightRepository,
						   FlightBookingLinkRepository flightBookingLinkRepository, UserRepository userRepository,
						   FightCityRepository fightCityRepository,
						   PasswordEncoder encoder,
						   RoleRepository roleRepository) {
		return args -> {
			Object[][] data;
			// ------------Member-----------------
			data = new Object[][] { { "Alice", "0882223331", "Alick@mail.com", "1234" ,"Alice","admin"},
					{ "Bob", "0881112223", "Bob@mail.com", "1234" ,"Bob","member"} };
			for (int i = 0; i < data.length; i++) {
//				User user = new User();
//				user.setUsername(data[i][4].toString());
//				user.setName(data[i][0].toString());
//				user.setPhone(data[i][1].toString());
//				user.setEmail(data[i][2].toString());
//				user.setPassword(data[i][3].toString());
//				user.setRegDate(new Date());
//				user = userRepository.save(user);
				User user = new User(data[i][4].toString(),
						data[i][2].toString(),
						encoder.encode(data[i][3].toString()));

				Set<Role> roles = new HashSet<>();
//				roles.add(roleRepository.findByName(ERole.ROLE_MEMBER).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
				user.setRoles(null);
				user = userRepository.save(user);

				System.out.printf("\n------------Add Member%d--------------\n", i + 1);
				System.out.println(user);
				System.out.println("-------------------------------------------");
			}
			// ------------Flight-----------------
			data = new Object[][] {
					// price , depart(days), flight duration(minute)
					// 0 is today , 1 tomorrow , 2 next 2 day
					{ 1900, 0, 30 }, { 1800, 0, 40 }, { 2000, 1, 75 }, { 2400, 1, 55 }, };
			for (int i = 0; i < data.length; i++) {
				Flight flight = new Flight();
				flight.setPrice(Double.parseDouble(data[i][0].toString()));

				// date create
				Date depart = new Date(
						new Date().getTime() + ((1000 * 60 * 60 * 24) * Integer.parseInt(data[i][1].toString())));
				Date arrive = new Date(depart.getTime() + (1000 * 60) * Integer.parseInt(data[i][2].toString()));
				//
				flight.setDepart(depart);
				flight.setArrive(arrive);
				flight = flightRepository.save(flight);
				System.out.printf("\n------------Add Flight%d--------------\n", i + 1);
				System.out.println(flight);
				System.out.println("-------------------------------------------");
			}
			// ------------Booking Status-----------------
			data = new Object[][] { { "ยังไม่ชำระ" }, { "ชำระแล้ว" }, { "เช็คอินแล้ว" } };
			for (int i = 0; i < data.length; i++) {
				BookingStatus bookingStatus = new BookingStatus();
				bookingStatus.setName(data[i][0].toString());
				bookingStatus = bookingStatusRepository.save(bookingStatus);
				System.out.printf("\n------------Add BookingStatus%d--------------\n", i + 1);
				System.out.println(bookingStatus);
				System.out.println("-------------------------------------------");
			}
			// --------------Flight Booking-----------------
			data = new Object[][] {
					// departFlightId,returnFlightId,departSeatId,returnSeatId,MemberId
					{ 2, 1, 1, 1, 1 }, { 1, 2, 2, 2, 1 } };
			for (int i = 0; i < data.length; i++) {
				FlightBooking flightBooking = new FlightBooking();
				flightBooking.book((Integer) data[i][2], (Integer) data[i][3]);
				BookingStatus bs = bookingStatusRepository.findById(1).orElse(null);
				flightBooking.setBookingStatus(bs);
				flightBooking.setUser(userRepository.findById(Long.parseLong(data[i][4].toString())).orElse(null));
				flightBooking = flightBookingRepository.save(flightBooking);

				// Add Depart's Flight and Return's Flight to TableLink
				Flight departFlight = flightRepository.findById(Long.parseLong(data[i][0].toString())).orElse(null);
				Flight returnFlight = flightRepository.findById(Long.parseLong(data[i][1].toString())).orElse(null);

				FlightBookingLink flightBookingLink = new FlightBookingLink();
				flightBookingLink.setFlight(departFlight);
				flightBookingLink.setFlightBooking(flightBooking);
				flightBookingLink.setDepartFlight(true);
				flightBookingLinkRepository.save(flightBookingLink);

				flightBookingLink = new FlightBookingLink();
				flightBookingLink.setFlight(returnFlight);
				flightBookingLink.setFlightBooking(flightBooking);
				flightBookingLink.setDepartFlight(false);
				flightBookingLinkRepository.save(flightBookingLink);

				System.out.printf("\n------------Add FlightBooking%d--------------\n", i + 1);
				System.out.println(flightBooking);
				System.out.println("-------------------------------------------");

			}
			;
			// ------------Flight City-----------------
			data = new Object[][] { { "กรุงเทพมหานคร" }, { "เชียงใหม่" }, { "เชียงราย" }, { "ภูเก็ต" }, { "ส่งขลา" } };
			for (int i = 0; i < data.length; i++) {
				FightCity fightCity = new FightCity();
				fightCity.setName(data[i][0].toString());
				fightCity = fightCityRepository.save(fightCity);
				System.out.printf("\n------------Add Flight City%d--------------\n", i + 1);
				System.out.println(fightCity);
				System.out.println("-------------------------------------------");
			}
		};
	}

	// Fix the CORS errors
	@Bean
	public FilterRegistrationBean simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		// *** URL below needs to match the Vue client URL and port ***
		config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}
