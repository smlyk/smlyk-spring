package com.smlyk.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yekai
 */
public class YKView {

    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public YKView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getContentType(){
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception{

        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(viewFile, "r");

        try {
            String line = null;
            while (null != (line = ra.readLine())){

                line = new String(line.getBytes("ISO-8859-1"),"utf-8");
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()){

                    String paramName = matcher.group();
                    //paramName：￥{userName} --> userName
                    paramName = paramName.replaceAll("￥\\{|\\}","");
                    Object paramValue = model.get(paramName);
                    if (null == paramValue) continue;

                    //要把￥{}中间的这个字符串给取出来?
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                }
                sb.append(line);
            }
        } finally {
            ra.close();
        }

        response.setCharacterEncoding("utf-8");

        response.getWriter().write(sb.toString());
    }

    /**
     * 处理特殊的字符
     * @param str
     * @return
     */
    private String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }


}
