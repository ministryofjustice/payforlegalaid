package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.BiPredicate;

public record NamedValidator(String name, BiPredicate<Cell, Workbook> predicate) {}