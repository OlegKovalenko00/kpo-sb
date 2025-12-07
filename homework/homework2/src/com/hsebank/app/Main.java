package com.hsebank.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            ConsoleApplication app = ctx.getBean(ConsoleApplication.class);
            app.run();
        }
    }
}
