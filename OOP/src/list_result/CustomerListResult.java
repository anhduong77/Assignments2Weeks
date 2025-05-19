package list_result;
import java.io.*;
import java.util.ArrayList;

import org.json.JSONObject;

public class CustomerListResult {
    private ArrayList<String> page = new ArrayList<String>();
    private ArrayList<ArrayList<String>> allPages = new ArrayList<>();

    public CustomerListResult(int pageNum){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"));
            String currentLine = "";
            int lineCnt = 0;

            ArrayList<String> curPage = new ArrayList<String>();
            while (true){
                currentLine = reader.readLine();
                if (currentLine == null) {
                    if (curPage.size() > 0) allPages.add(curPage);
                    break;
                }
                JSONObject jsObj = new JSONObject(currentLine);
                String curRole = jsObj.getString("user_role");
                if (!curRole.equals("customer")) continue;
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
