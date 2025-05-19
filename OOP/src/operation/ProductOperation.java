package operation;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.json.JSONObject;

import list_result.ProductListResult;
import user.Product;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ProductOperation {
    private static ProductOperation instance = null;
    private static HashMap<String, ArrayList<Object>>  data = new HashMap<>();


    private ProductOperation() {
        extractProductFromFiles();
    }
    public void extractProductFromFiles() {
        data.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/products.txt"));
            String currentLine = "";
            while ((currentLine = reader.readLine()) != null) {

                JSONObject jsObj = new JSONObject(currentLine);
                ArrayList <Object> line = new ArrayList<>();
                line.add(jsObj.getString("pro_model"));
                line.add(jsObj.getString("pro_category"));
                line.add(jsObj.getString("pro_name"));
                line.add(jsObj.getDouble("pro_current_price"));
                line.add(jsObj.getDouble("pro_raw_price"));
                line.add(jsObj.getDouble("pro_discount"));
                line.add(jsObj.getInt("pro_likes_count"));
                data.put(jsObj.getString("pro_id"), line);
            
             }
             reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProductListResult getProductList(int pageNumber) {
        ProductListResult productList = new ProductListResult(pageNumber);
        return productList;
    }

    public boolean deleteProduct(String productId) {
        boolean state = false;
        File originalFile = new File("data/products.txt");
        File tempFile = new File("data/tempFile.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine = "";
            while (true) {
                currentLine = reader.readLine();
                if (currentLine == null) break;
                JSONObject jsObject = new JSONObject(currentLine);
                String curId = jsObject.getString("pro_id");
                if (curId.equals(productId)) {
                    System.out.println("Yes");
                    continue;

                }
                writer.write(currentLine);
                writer.newLine();

            }
            writer.close();
            reader.close();
            if (originalFile.delete()) {
                state = tempFile.renameTo(originalFile);
            } 
        } catch (IOException e) {
            e.printStackTrace();
        } 
    
        return state;
        
    }

    public ArrayList<Product> getProductListByKeyword(String keyword) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/products.txt"));
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            String currentLine = "";
            while ((currentLine = reader.readLine()) != null){
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find()) {
                    JSONObject jsObj = new JSONObject(currentLine);
                    String curId = jsObj.getString("pro_id");
                    ArrayList<Object> proInf = data.get(curId);
                    Product product = new Product(curId, 
                                                String.valueOf(proInf.get(0)),
                                                String.valueOf(proInf.get(1)),
                                                String.valueOf(proInf.get(2)),
                                                (double) proInf.get(3), 
                                                (double) proInf.get(4), 
                                                (double) proInf.get(5), 
                                                (int) proInf.get(6));
                    products.add(product);

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (products.size() == 0) return null;
        return products;
        
    }

    public Product getProductById(String productId) {
        if (!data.containsKey(productId)) return null;
        ArrayList<Object> proInf = data.get(productId);
        Product product = new Product(productId, 
                                        String.valueOf(proInf.get(0)),
                                        String.valueOf(proInf.get(1)),
                                        String.valueOf(proInf.get(2)),
                                        (double) proInf.get(3),
                                        (double) proInf.get(4),
                                        (double) proInf.get(5),
                                        (int) proInf.get(6));
        return product;
    }

    public void generateCategoryFigure() {
        HashMap<String, Double> categoryFigure = new HashMap<>();
        for(ArrayList<Object> product: data.values()) {
            String category = String.valueOf(product.get(1));
            double currentPrice = (double) product.get(3);
            if (categoryFigure.containsKey(category)) {
                double currentTotal = categoryFigure.get(category);
                categoryFigure.put(category, currentTotal + currentPrice);
            } else {
                categoryFigure.put(category, currentPrice);
            }
        }

        //Draw chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String category : categoryFigure.keySet()) {
            dataset.addValue(categoryFigure.get(category), "Category", category);
        }
        JFreeChart chart = ChartFactory.createBarChart(
            "Category Figure",
            "Category",
            "Total Price",
            dataset
        );
        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/category.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    @SuppressWarnings("unchecked")
    public void generateDiscountFigure() {
        int i = 0, j = 0, k = 0;
        for (ArrayList<Object> product : data.values()) {
            double discount = (double) product.get(5);
            if (discount < 30) i++;
            else if (discount < 60) j++; 
            else k++;
        }

        @SuppressWarnings("rawtypes")
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("0-30", i);
        dataset.setValue("30-60", j);
        dataset.setValue(">60", k);
        JFreeChart chart = ChartFactory.createPieChart(
            "Discount Figure",
            dataset,
            true,
            true,
            false
        );
        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/discount.png"), chart, 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateLikesCountFigure() {
        HashMap<String, Integer> likesCountFigure = new HashMap<>();
        for (ArrayList<Object> product: data.values()) {
            String category = (String) product.get(1);
            if (likesCountFigure.containsKey(category)) {
                int currentTotal = likesCountFigure.get(category);
                likesCountFigure.put(category, currentTotal + (int) product.get(6));
            }
            else {
                likesCountFigure.put(category, (int) product.get(6));
            }
        }

        //Draw chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String category : likesCountFigure.keySet()) {
            dataset.addValue(likesCountFigure.get(category), "Category", category);
        }
        JFreeChart chart = ChartFactory.createBarChart(
            "Likes Count Figure",
            "Category",
            "Total Likes Count",
            dataset
        );

        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/like_counts.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateDiscountLikesCountFigure() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Discount vs Likes Count");
        for(ArrayList<Object> product: data.values()) {
            double discount = (double) product.get(5);
            int likesCount = (int) product.get(6);
            series.add(discount, likesCount);
        }
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Discount vs Likes Count",
            "Discount",
            "Likes Count",
            dataset
        );

        try {
            ChartUtils.saveChartAsPNG(new File("data/figure/discount_vs_like.png"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllProducts() {
        File originalFile = new File("data/products.txt");
        originalFile.delete();
    }



    public static ProductOperation getInstance() {
        if (instance == null)  {
            instance = new ProductOperation();
        }
        return instance;
    }
    public HashMap<String, ArrayList<Object>> getData() {
        return data;
    }
    
}
