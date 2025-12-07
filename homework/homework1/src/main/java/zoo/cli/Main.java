package zoo.cli;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zoo.config.AppConfig;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            ConsoleRunner runner = context.getBean(ConsoleRunner.class);
            runner.run();
        }
    }
}
