//package uk.gov.laa.pfla.auth.service.beans;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Component
//public class BeanLister implements CommandLineRunner {
//
//    private final ApplicationContext ctx;
//
//    public BeanLister(ApplicationContext ctx) {
//        this.ctx = ctx;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        System.out.println("beanNames here: !!! ");
//
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }
//        System.out.println("beanNames END !!! ");
//
//    }
//}
