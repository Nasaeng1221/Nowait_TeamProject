package com.myboot.config;

import com.myboot.reservation.MyReservationsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

  @Bean
  public ServletRegistrationBean<MyReservationsServlet> myReservationsServlet() {
    ServletRegistrationBean<MyReservationsServlet> bean =
        new ServletRegistrationBean<>(new MyReservationsServlet(), "/api/my/reservations");
    bean.setLoadOnStartup(1);
    return bean;
  }
}
