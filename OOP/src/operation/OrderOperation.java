package operation;
import java.io.*;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import list_result.OrderListResult;
import user.Order;


public class OrderOperation {
    private static OrderOperation instance = null;
    private OrderOperation() {}
    private static long orderId = 10000;

    public String generateUniqueOrderId() {
        String orderIdStr = "o_" + orderId;
        orderId++;
        return orderIdStr;
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        String orderId = generateUniqueOrderId();
        Order order = new Order(orderId, customerId, productId, createTime);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/orders.txt", true));
            writer.write(order.toString());
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteOrder(String orderId) {
        boolean isDeleted = false;
        File file = new File("data/orders.txt");
        File tempFile = new File("data/temp_ordes.txt");

        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.contains(orderId)) {
                    writer.write(currentLine);
                    writer.newLine();
                }
            }
            reader.close();
            writer.close();
            if (file.delete()) {
                if (tempFile.renameTo(file)) {
                    isDeleted = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public OrderListResult getOrderList(int pageNum) {
        return new OrderListResult(pageNum);
    }

    public void generateTestOrderData() {
        CustomerOperation customerInstance = CustomerOperation.getInstance();
        ProductOperation productInstance = ProductOperation.getInstance();

        String[] names = {"Isabella", "Benjamin", "Sophia", "Matthew", "Oliver", "Scarlett", "Gabriel", "Adeline", "Eleanor", "Juliana"};
        HashMap<String, ArrayList<Object>> products = productInstance.getData();
        ArrayList<String> productIds = new ArrayList<>();

        for (String key : products.keySet()) {
            productIds.add(key);
        }
        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            String phone = "03" + rand.nextLong(10000000, 99999999);
            customerInstance.registerCustomer(names[i], names[i] + "1234", names[i] + "@gmail.com", phone);
            String customerId = customerInstance.getCustomerId(names[i], names[i] + "1234");
            Random randConsume = new Random();
            int consume = randConsume.nextInt(50,100);
            for (int j = 0; j < consume; j++) {
                String productId = productIds.get(rand.nextInt(0, productIds.size()));
                String createTime;
                Random randTime = new Random();
                int month = randTime.nextInt(1, 13);
                if (month < 10) createTime = "2023-0" + month + "-01_12:00:0";
                else createTime = "2023-" + month + "-01_12:00:0";
                
                createAnOrder(customerId, productId, createTime);
            }

        }
    }

    public void generateSingleCustomerConsumptionFigure(String customerId) {
        HashMap<Integer, Double> consumption = new HashMap<>();
        HashMap<String, ArrayList<Object>> products = ProductOperation.getInstance().getData();
        HashMap<String, Double> prices = new HashMap<>();
        for (String key : products.keySet()) {
            ArrayList<Object> productData = products.get(key);
            prices.put(key, (double) productData.get(3));
        }
        for (int i = 0; i < 12; i++) {
            consumption.put(i + 1, 0.0);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("data/orders.txt"))) {
            String currentLine = "";
            while (true) {
                currentLine = reader.readLine();
                if (currentLine == null) break;
                JSONObject jsObj = new JSONObject(currentLine);
                String curCustomerId = jsObj.getString("user_id");
                if (!curCustomerId.equals(customerId)) continue;
            
                String productId = jsObj.getString("pro_id");
                double price = prices.get(productId);
                String time = jsObj.getString("order_time").substring(5, 7);

                int month = Integer.parseInt(time);

                if (curCustomerId.equals(customerId)) {
                    consumption.put(month, consumption.get(month) + price);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 12; i++) {
            dataset.addValue(consumption.get(i), "Consumption", String.valueOf(i));
        }

        // Draw chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Customer Consumption",
                "Month",
                "Consumption",
                dataset);
        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/customer_consumption.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAllCustomerConsumptionFigure() {
        double[] consumption = new double[13];
        ProductOperation productInstance = ProductOperation.getInstance();
        HashMap<String, ArrayList<Object>> products = productInstance.getData();
        HashMap<String, Double> prices = new HashMap<>();
        for (String proId : products.keySet()) {
            double price = (double) products.get(proId).get(3);
            prices.put(proId, price);
        }
        try(BufferedReader reader = new BufferedReader(new FileReader("data/orders.txt"))){
            String currentLine = "";
            while((currentLine = reader.readLine()) != null) {
                JSONObject jsObj = new JSONObject(currentLine);
                int month = Integer.parseInt(jsObj.getString("order_time").substring(5, 7));
                consumption[month] += prices.get(jsObj.getString("pro_id"));

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 12; i++) {
            dataset.addValue(consumption[i], "Consumption", String.valueOf(i));
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "All Customer Consumption",
                "Month",
                "Consumption",
                dataset);
        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/monthly_consumption.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAllTop10BestSellersFigure() {
        HashMap<String, Double> consumption = new HashMap<>();
        ArrayList<String> productIds = new ArrayList<>();
        HashMap<String, ArrayList<Object>> products = ProductOperation.getInstance().getData();
        HashMap<String, Double> prices = new HashMap<>();
        for (String key : products.keySet()) {
            ArrayList<Object> productData = products.get(key);
            prices.put(key, (double) productData.get(3));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("data/orders.txt"))) {
            String currentLine = "";
            while (true) {
                currentLine = reader.readLine();
                if (currentLine == null) break;
                JSONObject jsObj = new JSONObject(currentLine);
                String productId = jsObj.getString("pro_id");
                double price = prices.get(productId);
                consumption.put(productId, consumption.getOrDefault(productId, 0.0) + price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumption.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .limit(10)
                .forEach(k -> productIds.add(k.getKey()));
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String productId : productIds) {
            dataset.addValue(consumption.get(productId), "Consumption", productId);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 10 Best Sellers",
                "Product ID",
                "Consumption",
                dataset);

        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/top_best_seller.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllOrders() {
        File file = new File("data/orders.txt");
        file.delete();
    }


    public void deleteAllFigures() {
        File file = new File("data/figure");
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
    
    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }
}
