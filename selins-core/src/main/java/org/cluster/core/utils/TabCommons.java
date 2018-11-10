package org.cluster.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/7 14:31
 * @Version: 1.0
 * @Description: TODO
 */
public class TabCommons {
    private String[][] tabValue;//数据

    public TabCommons(List<String[]> tabValue){
        this.tabValue = tabValue.toArray(new String[tabValue.size()][]);
    }

    /**
     * 获取字符串占的字符位数
     */
    private int getStringCharLength(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");//利用正则找到中文
        Matcher m = p.matcher(str);
        int count = 0;
        while (m.find()){
            count++;
        }
        return str.length() + count;
    }

    /**
     * 纵向遍历获取数据每列的长度
     */
    private int[] getColLengths(){
        int[] columns = new int[tabValue[0].length];
        for (int i = 0; i < tabValue[0].length; i++) {
            for (int j = 0; j < tabValue.length; j++) {
                columns[i] = Math.max(getStringCharLength(tabValue[j][i]),columns[i]);
            }
        }
        return columns;
    }

    public String getLineSegment(int[] colLength){
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < colLength.length; i++) {
            for (int j = 0; j < colLength[i] + 8; j++) {
                if((i + j) % 2 == 0){
                    buffer.append("-");
                }else{
                    buffer.append(" ");
                }
            }
        }
        return buffer.append("-").toString();
    }

    public String getFieldSegment(int colLength, String field){
        StringBuilder buffer = new StringBuilder();
        int total = (colLength + 8 - getStringCharLength(field));
        buffer.append("|");
        for (int i = 1; i < total/2; i++) {
            buffer.append(" ");
        }
        buffer.append(field);
        for (int i = 0; i < total - total/2; i++) {
            buffer.append(" ");
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        int colLength[] = getColLengths();
        for (int i = 0; i < tabValue.length; i++) {
            buffer.append(getLineSegment(colLength)).append("\n");
            for (int j = 0; j < tabValue[i].length; j++) {
                buffer.append(getFieldSegment(colLength[j],tabValue[i][j]));
            }
            buffer.append("|").append("\n");
        }
        buffer.append(getLineSegment(colLength)).append("\n");
        return buffer.toString();
    }

    /**
     * 使用示例
     * @param args
     */
    public static void main(String[] args) {
        List<String[]> data1 = new ArrayList<>();
        data1.add(new String[]{"用户名","密码","姓名"});
        data1.add(new String[]{"xiaoming","xm123","小明"});
        data1.add(new String[]{"xiaohong","xh123","小红"});
        System.out.println(new TabCommons(data1).toString());
    }
}
