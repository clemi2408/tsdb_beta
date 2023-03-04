package de.cleem.tub.tsdbb.commons.spring.banner;


import com.github.dtmo.jfiglet.FigFont;
import com.github.dtmo.jfiglet.FigletRenderer;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Optional;

import static com.github.dtmo.jfiglet.FigFontResources.loadFigFontResource;

// copied and adopted from: https://github.com/springbootcamp/bannerama/blob/master/src/main/java/org/springbootcamp/bannerama/Bannerama.java

@Value
@Builder
public class DynamicBanner extends BaseClass implements Banner {

    public static final String FIGLET_FONT_ID="standard.flf";

    @Default
    int newLinesAfter = 1;

    @Override
    @SneakyThrows
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        new Worker(environment, sourceClass, out)
                .before()
                .banner()
                .copyright()
                .after()
                .close();
    }

    @RequiredArgsConstructor
    private class Worker {

        private final Environment environment;
        private final Class<?> sourceClass;
        private final PrintStream out;

        Worker before() {
            return newLines(1);
        }

        @SneakyThrows
        Worker banner() {

            final FigFont font = loadFigFontResource(FIGLET_FONT_ID);
            final FigletRenderer figletRenderer = new FigletRenderer(font);
            final String applicationName = resolvePropertyValue(PlaceholderEnum.APPLICATION_NAME, sourceClass.getSimpleName().toLowerCase());
            final String renderedApplicationName = figletRenderer.renderText(applicationName);

            final String applicationTitle = resolvePropertyValue(PlaceholderEnum.APPLICATION_TITLE, "N/A");

            return println(renderedApplicationName)
                    .println(Optional.ofNullable(applicationTitle).map(it -> "  :: " + it + "  ::").orElse(null))
                    .newLines(1);
        }

        Worker copyright() {

            final String copyrightString = resolvePropertyValue(PlaceholderEnum.APPLICATION_COPYRIGHT, "N/A");
            final String yearString = resolvePropertyValue(PlaceholderEnum.APPLICATION_YEAR, "N/A");
            final String copyrightLine = String.format("  > %s: %s @ %s","Copyright",copyrightString,yearString);

            println(copyrightLine);

            return this;
        }

        Worker after() {
            return newLines(newLinesAfter);
        }

        void close() {
            println(resolvePropertyValue("${AnsiColor.DEFAULT}", null));
        }

        Worker println(String line) {
            Optional.ofNullable(line).ifPresent(out::println);
            return this;
        }

        private Worker newLines(int num) {
            if (num > 0) {
                for (int i = 0; i < num; i++) {
                    out.println("");
                }
            }
            return this;
        }

        private String resolvePropertyValue(String value, String defaultValue) {
            return (value.startsWith("${") && value.endsWith("}"))
                    ? environment.getProperty(value.substring(2, value.length() - 1), defaultValue)
                    : value;
        }
    }


}