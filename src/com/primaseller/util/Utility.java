package com.primaseller.util;

import com.primaseller.model.Book;
import com.primaseller.model.Sale;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author rkyadav
 */
public class Utility {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String COLUMN_SPLIT_DELIMITER = ";";

    /**
     * Creates Book Object from CSV line
     *
     * @param column
     * @return
     */
    public static Book getBookObject(String[] column) {
        Book book = new Book(column[0], column[1], column[2], Float.parseFloat(column[3]));
        return book;
    }

    /**
     * Creates Sale Object from CSV line
     *
     * @param column
     * @param book_map
     * @return
     * @throws java.text.ParseException
     */
    public static Sale getSaleObject(String[] column, Map<String, Book> book_map) throws ParseException {
        Sale sale = new Sale(DATE_FORMAT.parse(column[0]), column[1], column[2]);

        int count = Integer.parseInt(column[3]);
        //start with column 4 and increase column number untill all columns processed
        int itemColumn = 4;
        Map<Book, Integer> items = new HashMap<>();
        while (count > 0) {
            String[] item = column[itemColumn].split(COLUMN_SPLIT_DELIMITER);
            items.put(book_map.get(item[0]), Integer.parseInt(item[1]));
            itemColumn++;
            count--;
        }
        sale.setItems(items);
        return sale;
    }

    /**
     * returns map sorted by value in descending order
     *
     * @param map
     * @return
     */
    public static Map<String, Float> sortMapInDescendingOrder(Map<String, Float> map) {
        LinkedHashMap<String, Float> reverseSortedMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

}
