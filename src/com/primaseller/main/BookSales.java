package com.primaseller.main;

import com.primaseller.model.*;
import com.primaseller.util.Utility;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rkyadav
 */
public class BookSales {

    private static final String CSV_SPLIT_DELIMITER = ",";
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");

    public static void main(String[] args) {

        String book_path = null;
        String sales_path = null;
        String sale_date = null;
        int booksCount = 0, customersCount = 0;

        //read passed arguments 
        for (String arg : args) {
            String[] argument = arg.split("=");
            if (null == argument[0]) {
                System.out.println("passed argument is not supported");
            } else {
                switch (argument[0]) {
                    case "--books":
                        book_path = argument[1];
                        break;
                    case "--sales":
                        sales_path = argument[1];
                        break;
                    case "--top_selling_books":
                        booksCount = Integer.parseInt(argument[1]);
                        break;
                    case "--top_customers":
                        customersCount = Integer.parseInt(argument[1]);
                        break;
                    case "--sales_on_date":
                        sale_date = argument[1];
                        break;
                    default:
                        System.out.println("passed argument is not supported");
                        break;
                }
            }
        }

        BookSales bookSales = new BookSales();
        Map<String, Book> book_map = new HashMap<>();
        List<Sale> sales = new ArrayList<>();

        if (book_path != null) {
            book_map = bookSales.readBooksCSV(book_path);
        } else {
            System.out.println("Book path is not passed in argument, exiting system");
            System.exit(1);
        }

        if (sales_path != null) {
            sales = bookSales.readSalesCSV(sales_path, book_map);
        } else {
            System.out.println("Sales path is not passed in argument, exiting system");
            System.exit(1);
        }

        //print top selling books if count is greater than zero
        if (booksCount > 0) {
            bookSales.printTopSellingBooks(sales, booksCount);
            System.out.println("");
        }

        //print top customers if count is greater than zero
        if (customersCount > 0) {
            bookSales.printTopCustomers(sales, customersCount);
            System.out.println("");
        }

        //print sale amount if date is not null
        if (sale_date != null) {
            bookSales.printSaleOnDate(sales, sale_date);
        }
    }

    /**
     * prints to selling books
     *
     * @param sales
     * @param topSellingBooksCount
     */
    private void printTopSellingBooks(List<Sale> sales, int topSellingBooksCount) {
        Map<String, Float> topSellingBooksMap = new HashMap<>();
        sales.forEach((sale) -> {
            Map<Book, Integer> itemsList = sale.getItems();
            itemsList.entrySet().forEach((value) -> {
                Book book = value.getKey();
                if (topSellingBooksMap.containsKey(book.getId())) {
                    Float oldSale = topSellingBooksMap.get(book.getId());
                    topSellingBooksMap.put(book.getId(), Float.parseFloat(DECIMAL_FORMAT.format(oldSale + Float.parseFloat(DECIMAL_FORMAT.format(book.getPrice() * value.getValue())))));
                } else {
                    topSellingBooksMap.put(book.getId(), Float.parseFloat(DECIMAL_FORMAT.format(book.getPrice() * value.getValue())));
                }
            });
        });

        Map<String, Float> descendingSortedTopSellingBooks = Utility.sortMapInDescendingOrder(topSellingBooksMap);
        System.out.print("top_selling_books");
        for (Map.Entry<String, Float> book : descendingSortedTopSellingBooks.entrySet()) {
            if (topSellingBooksCount == 0) {
                break;
            }
            System.out.print("\t" + book.getKey());
//            System.out.print("\t" + book.getValue());
            topSellingBooksCount--;
        }
    }

    /**
     * prints top customers
     *
     * @param sales
     * @param topCustomersCount
     */
    private void printTopCustomers(List<Sale> sales, int topCustomersCount) {
        Map<String, Float> topCustomersMap = new HashMap<>();
        sales.forEach((sale) -> {
            if (!topCustomersMap.containsKey(sale.getEmail())) {
                topCustomersMap.put(sale.getEmail(), getItemsTotalAmount(sale.getItems(), 0.0f));
            } else {
                topCustomersMap.put(sale.getEmail(), getItemsTotalAmount(sale.getItems(), topCustomersMap.get(sale.getEmail())));
            }
        });

        Map<String, Float> descendingSortedTopCustomers = Utility.sortMapInDescendingOrder(topCustomersMap);
        System.out.print("top_customers");
        for (Map.Entry<String, Float> customer : descendingSortedTopCustomers.entrySet()) {
            if (topCustomersCount == 0) {
                break;
            }
            System.out.print("\t" + customer.getKey());
//            System.out.print("\t" + customer.getValue());
            topCustomersCount--;
        }
    }

    /**
     * prints total sale amount for given date
     *
     * @param sales
     * @param sale_date
     */
    private void printSaleOnDate(List<Sale> sales, String sale_date) {
        Float total_sale_amount = 0.0f;
        for (Sale sale : sales) {
            String date = Utility.DATE_FORMAT.format(sale.getDate());
            if (date.equals(sale_date)) {
                total_sale_amount = getItemsTotalAmount(sale.getItems(), total_sale_amount);
            }
        }
        System.out.print("sales_on_date\t" + sale_date + "\t" + total_sale_amount);
    }

    /**
     * returns total amount of sold books pass previuosAmount value for previous
     * total
     *
     * @param itemsList
     * @param previuosAmount
     * @return
     */
    private Float getItemsTotalAmount(Map<Book, Integer> itemsList, Float previuosAmount) {
        Float totalValue = previuosAmount;
        for (Map.Entry<Book, Integer> value : itemsList.entrySet()) {
            Book book = value.getKey();
            totalValue = Float.parseFloat(DECIMAL_FORMAT.format(totalValue + Float.parseFloat(DECIMAL_FORMAT.format(book.getPrice() * value.getValue()))));
        }
        return totalValue;
    }

    /**
     * This function reads books CSV file and returns book map of BookId as ID
     * and Book object as value
     *
     * @param path
     * @return
     */
    private Map<String, Book> readBooksCSV(String path) {
        Map<String, Book> book_map = new HashMap<>();
        BufferedReader br = null;
        String line = "";
        try {
            File inputF = new File(path);
            InputStream inputFS = new FileInputStream(inputF);
            br = new BufferedReader(new InputStreamReader(inputFS));

            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(CSV_SPLIT_DELIMITER);
                Book book = Utility.getBookObject(attributes);
                book_map.put(book.getId(), book);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Exception: " + e);
                }
            }
        }
        return book_map;
    }

    /**
     * This function reads sales CSV file and returns sales list
     *
     * @param path
     * @return
     */
    private List<Sale> readSalesCSV(String path, Map<String, Book> book_map) {
        List<Sale> sales = new ArrayList<>();
        BufferedReader br = null;
        String line = "";
        try {
            File inputF = new File(path);
            InputStream inputFS = new FileInputStream(inputF);
            br = new BufferedReader(new InputStreamReader(inputFS));

            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(CSV_SPLIT_DELIMITER);
                Sale sale = Utility.getSaleObject(attributes, book_map);
                sales.add(sale);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } catch (ParseException e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Exception: " + e);
                }
            }
        }
        return sales;
    }
}
