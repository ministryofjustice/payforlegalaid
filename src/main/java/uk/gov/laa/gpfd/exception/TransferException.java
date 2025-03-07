package uk.gov.laa.gpfd.exception;

/**
 * A sealed abstract class representing a hierarchy of exceptions related to data transfer operations.
 * This class serves as the base for specific exceptions that can occur during data transfer,
 * such as streaming errors. It extends {@link RuntimeException}, making it an unchecked exception.
 */
public abstract sealed class TransferException extends RuntimeException {

    /**
     * Constructs a new {@code TransferException} with the specified error message and cause.
     *
     * @param s the detail message describing the exception.
     * @param e the cause of the exception.
     */
    protected TransferException(String s, Exception e) {
        super(s, e);
    }

    /**
     * Represents an exception that occurs during a streaming operation.
     * This is a specific type of {@link TransferException}.
     */
    public static sealed class StreamException extends TransferException {

        /**
         * Constructs a new {@code StreamException} with the specified error message and cause.
         *
         * @param s the detail message describing the exception.
         * @param e the cause of the exception.
         */
        protected StreamException(String s, Exception e) {
            super(s, e);
        }

        /**
         * Represents an exception that occurs while writing an Excel file as a stream.
         * This is a specific type of {@link StreamException}.
         */
        @SuppressWarnings("java:S110")
        public static non-sealed class ExcelStreamWriteException extends StreamException {

            /**
             * Constructs a new {@code ExcelStreamWriteException} with the specified error message and cause.
             *
             * @param s the detail message describing the exception.
             * @param e the cause of the exception.
             */
            public ExcelStreamWriteException(String s, Exception e) {
                super(s, e);
            }
        }
    }
}
