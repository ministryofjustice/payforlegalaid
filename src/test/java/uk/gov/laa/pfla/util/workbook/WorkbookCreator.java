package uk.gov.laa.pfla.util.workbook;

import lombok.SneakyThrows;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;

import static org.apache.poi.openxml4j.util.ZipSecureFile.setMinInflateRatio;


/**
 * Provides functionality to create Excel workbooks with built-in protection against ZIP bomb attacks.
 *
 * <p>
 * The implementation uses Apache POI's {@link ZipSecureFile} mechanism to prevent decompression
 * bombs by setting a safe minimum inflation ratio before workbook creation and restoring the
 * original ratio afterwards.
 * </p>
 *
 * @since 1.0
 */
public interface WorkbookCreator {

    /**
     * The safe minimum inflation ratio used for ZIP bomb protection (0.001).
     * <p>
     * This value represents 0.1% compression ratio, meaning files that would inflate
     * to more than 1000 times their compressed size will be rejected.
     * </p>
     */
    double SAFE_MIN_INFLATE_RATIO = 1.0E-03;
    double DEFAULT_RATIO = ZipSecureFile.getMinInflateRatio();

    /**
     * Creates a new XSSFWorkbook from the provided byte array with ZIP bomb protection.
     */
    @SneakyThrows
    default Workbook construct(byte[] bytes) {
        setMinInflateRatio(SAFE_MIN_INFLATE_RATIO);

        try {
            return new XSSFWorkbook(new ByteArrayInputStream(bytes));
        } finally {
            setMinInflateRatio(DEFAULT_RATIO);
        }
    }
}