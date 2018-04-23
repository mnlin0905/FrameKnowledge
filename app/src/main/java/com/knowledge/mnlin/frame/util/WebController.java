package com.knowledge.mnlin.frame.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lxk on 2018/4/8.
 */

public class WebController {
    public void readHtml(String httpUrl, String savePath) throws IOException, URISyntaxException {
        String result = getConnectResult(httpUrl);
        saveToLocal(getTitle(result), getContent(result), savePath);
    }

    String getConnectResult(String httpUrl) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = null;
        BufferedReader br = null;
        URL url = new URL(httpUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setAllowUserInteraction(false);
        isr = new InputStreamReader(url.openStream());
        br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private String getTitle(String html) {
        String title = "";
        Pattern p = Pattern.compile("([\\s\\S]*)(<title>)([\\s\\S]*?)(</title>)([\\s\\S]*)");
        Matcher m = p.matcher(html);
        if (m.matches()) {
            title = m.group(3);
        }
        return title;
    }

    private String getContent(String html) {
        StringBuilder builder = new StringBuilder();
        Pattern p = Pattern.compile("([\\s\\S]*)(<div class=\"bookname\">)([\\s\\S]*?)(</div>)([\\s\\S]*)");
        Matcher m = p.matcher(html);
        if (m.matches()) {
            String bookNameH = m.group(3);
            p = Pattern.compile("([\\s\\S]*)(<h1>)(.*?)(</h1>)([\\s\\S]*)");
            m = p.matcher(bookNameH);
            if (m.matches()) {
                String bookName = m.group(3);
                builder.append(bookName);
            }
        }
        p = Pattern.compile("([\\s\\S]*)(<div id=\"content\">)([\\s\\S]*?)(</div>)([\\s\\S]*)");
        m = p.matcher(html);
        if (m.matches()) {
            String contentDiv = m.group(3);
            p = Pattern.compile("(<script>)([\\s\\S]*?)(</script>)");
            m = p.matcher(contentDiv);
            String content = m.replaceAll("");
            content = content.replaceAll("<br/>", "\\\r\\\n").trim();
            builder.append("\r\n").append(content);
        }
        return builder.toString();
    }

    public static void saveToLocal(String title, String content, String savePath) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String path = savePath + "/" + title + ".txt";
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
            if (fw != null) {
                fw.close();
            }
        }

    }


}
