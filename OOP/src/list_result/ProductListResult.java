package list_result;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ProductListResult {
    private ArrayList<String> page = new ArrayList<String>();
    private ArrayList<ArrayList<String>> allPages = new ArrayList<>();

    public ProductListResult(int pageNum){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/products.txt"));
            String currentLine = "";
            int lineCnt = 0;
            ArrayList<String> curPage = new ArrayList<String>();
            while (true){
                currentLine = reader.readLine();
                if (currentLine == null) {
                    if (curPage.size() > 0) allPages.add(curPage);
                    break;
                }
                curPage.add(currentLine);
                lineCnt++;
                if (lineCnt == 10) {
                    allPages.add(curPage);
                    curPage = new ArrayList<String>();
                    lineCnt = 0;
                }
            }
            for (String curLine : allPages.get(pageNum)){
                page.add(curLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getCurrentPage() {
        return page;
    }

    public ArrayList<ArrayList<String>> getAllPages() {
        return allPages;
    }
}
