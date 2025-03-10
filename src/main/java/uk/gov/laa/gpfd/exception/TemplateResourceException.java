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
     * Represents an exception that occurs during the creation of an Excel template.
     * This is a specific type of {@link TemplateResourceException}.
     */
    public static final class ExcelTemplateCreationException extends TemplateResourceException {

        public ExcelTemplateCreationException(String s, Exception e) {
            super(s, e);
        }
    }

}
