package uk.gov.laa.gpfd.exception;

/**
 * A sealed abstract class representing a hierarchy of exceptions related to template resource operations.
 * This class serves as the base for specific exceptions that can occur during template creation,
 * downloading, or reading. It extends {@link RuntimeException}, making it an unchecked exception.
 */
public sealed abstract class TemplateResourceException extends RuntimeException {

    /**
     * Constructs a new {@code TemplateResourceException} with the specified error message.
     *
     * @param s the detail message describing the exception.
     */
    protected TemplateResourceException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code TemplateResourceException} with the specified error message and a cause.
     *
     * @param s the detail message describing the exception.
     * @param e the cause of the exception.
     */
    protected TemplateResourceException(String s, Exception e) {
        super(s, e);
    }

    /**
     * Represents an exception that occurs during the downloading of a template resource.
     * This is a specific type of {@link TemplateResourceException}.
     */
    public static final class TemplateDownloadException extends TemplateResourceException {
        public TemplateDownloadException(String s) {
            super(s);
        }
    }

    /**
     * Represents an exception that occurs during the reading of a local template resource.
     * This is a specific type of {@link TemplateResourceException}.
     */
    public static final class LocalTemplateReadException extends TemplateResourceException {

        public LocalTemplateReadException(String s) {
            super(s);
        }
    }

    /**
     * Exception thrown when a requested template cannot be found by its identifier.
     * <p>
     * This typically occurs when:
     * <ul>
     *   <li>The provided template ID doesn't match any known template</li>
     *   <li>The template ID exists but has been deprecated or removed</li>
     *   <li>The template ID format is invalid</li>
     * </ul>
     */
    public static final class TemplateNotFoundException extends TemplateResourceException {
        /**
         * Constructs a new {@code TemplateNotFoundException} with the specified error message.
         *
         * @param message the detail message including the template ID that wasn't found
         */
        public TemplateNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a template file cannot be located in the application resources.
     * <p>
     * This typically indicates either:
     * <ul>
     *   <li>The template file is missing from the deployed resources</li>
     *   <li>The template file exists but cannot be accessed due to permissions</li>
     *   <li>The path to the template resource is incorrect</li>
     * </ul>
     * <p>
     * Contrast with {@link TemplateNotFoundException} which indicates the template ID wasn't recognized,
     * while this exception indicates the ID was valid but the corresponding resource file was missing.
     */
    public static final class TemplateResourceNotFoundException extends TemplateResourceException {

        /**
         * Constructs a new {@code TemplateResourceNotFoundException} with the specified error message.
         *
         * @param message the detail message including the filename that couldn't be found
         */
        public TemplateResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Represents an exception that occurs during the creation of an Excel template.
     * This is a specific type of {@link TemplateResourceException}.
     */
    public static final class ExcelTemplateCreationException extends TemplateResourceException {

        public ExcelTemplateCreationException(String s, Exception e) {
            super(s, e);
        }

        /**
         * Constructs a new {@code ExcelTemplateCreationException} with the specified error message.
         *
         * @param e the cause of the exception.
         * @param  format
         *         A <a href="../util/Formatter.html#syntax">format string</a>
         *
         * @param  args
         *         Arguments referenced by the format specifiers in the format
         *         string.  If there are more arguments than format specifiers, the
         *         extra arguments are ignored.  The number of arguments is
         *         variable and may be zero.
         */
        public ExcelTemplateCreationException(Exception e, String format, Object... args) {
            super(String.format(format, args), e);
        }
    }

}
