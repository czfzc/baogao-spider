package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main implements Runnable{
    private static int nameIndex=0;
    private static boolean founded=false;
    private final static int MAXThread=40;
    private static int initValue=10000;
    private static int i=initValue;
    private static int shiyanIndex=0;

    private static int printNum = 0;
    private final static int maxPrintNum = 1000;

    private static boolean isReseting=false;


    private static String name;
    private static String id;

    private static String path=System.getProperty("user.dir")+"/shiyanbaogao/";

    private static int success=0;

    private static String uri="";

    private static String shiyan = "";

    public static void main(String[] args) throws Exception{

        if(args.length==0){
            System.out.println("usage: \nshiyanbaogao.jar nameIndex[defaut:0] baogaoIndex[defaut:0] startIdIndex[defaut:10000] ");
        }

        System.out.println("started: "+args[0]);

        if(args.length>0)
            nameIndex = Integer.valueOf(args[0]);
        if(args.length>1)
            shiyanIndex = Integer.valueOf(args[1]);
        if(args.length>2)
            initValue = Integer.valueOf(args[2]);

        init();

    }

    public static void init() throws Exception{
        reset();
        for(int j=0;j<MAXThread;j++){
            new Thread(new Main()).start();
        }
    }

    public static int sendGet(String url) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Connection", "keep-alive");

            conn.connect();

            return conn.getResponseCode();

        } catch (Exception e) {
            System.out.println("发送请求出现异常！正在重发请求"+e);
            try {
                Thread.sleep(200);
                return sendGet(url);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public static void reset() throws Exception{

        if(isReseting)
            return;

        isReseting=true;

        String nameAndId=getNameAndIdByIndex(nameIndex++);

        if(shiyan==null)
            shiyan=getShiyanByIndex(shiyanIndex);

        if(nameAndId==null){
            System.out.println("finished one!");
            shiyan=getShiyanByIndex(++shiyanIndex);
            nameIndex=0;
            nameAndId=getNameAndIdByIndex(nameIndex++);
            if(shiyan==null){
                System.out.println("finished all!");
                System.exit(0);
            }
        }else{
            name=nameAndId.split("\t")[0];
            id=nameAndId.split("\t")[1];
        }

        uri=id+"-"+name+"-"+shiyan;
        nameIndex++;
        i=initValue;

        isReseting=false;
    }

    public static String getNameAndIdByIndex(int index) throws Exception{
        File file=new File(path+"names.txt");
        FileInputStream fs = new FileInputStream(file);
        InputStreamReader ins = new InputStreamReader(fs,"gbk");
        BufferedReader br=new BufferedReader(ins);
        String line;
        for(int k=0;k<=index;k++){
            if((line=br.readLine())==null){
                return null;
            }
            name=line.split("\t")[0];
            id=line.split("\t")[1];
        }
        return name+"\t"+id;
    }

    public static String getShiyanByIndex(int index) throws Exception{
        String shiyan=null;
        File file=new File(path+"shiyan.txt");
        FileInputStream fs = new FileInputStream(file);
        InputStreamReader ins = new InputStreamReader(fs,"gbk");
        BufferedReader br=new BufferedReader(ins);
        String line;
        for(int j=0;j<=shiyanIndex;j++){
            if((line=br.readLine())==null){
                return null;
            }
            shiyan=line;
        }
        return shiyan;
    }

    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            conn.setRequestProperty("X-MicrosoftAjax", "Delta=true");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie", "ASP.NET_SessionId=4va1yj3c3qlsip55w0ipc155");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1.获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //2.中文有乱码的需要将PrintWriter改为如下
            //out=new OutputStreamWriter(conn.getOutputStream(),"UTF-8")
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map<String,List<String>> map=conn.getHeaderFields();
            for(Entry<String, List<String>> list:map.entrySet()){
                for(int i=0;i<list.getValue().size();i++)
                    result+=list.getKey()+": "+list.getValue().get(i)+"\n";
            }
            result+="\n";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            try {
                Thread.sleep(1  );
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        //   System.out.println("post推送结果："+result);
        return result;
    }

    public static void download(String url,String filePath) throws Exception{
        URL u=new URL(url);
        URLConnection conn=u.openConnection();
        conn.setDoOutput(true);
        long totallen=conn.getContentLengthLong();
        File file=new File(filePath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        OutputStream out=new FileOutputStream(file);
        BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
        byte[] buf=new byte[1000];
        int size=0;
        int len=0;
        while((size=bin.read(buf))!=-1){
            len+=size;
            out.write(buf, 0, size);
        }
        bin.close();
        out.close();
    }

    @Override
    public void run() {
        try{
            for(;;++i){
                int k=i;
                String url="http://202.199.14.2/model/Center/student/baogao/UpLoadFile/YuXiBaoGao/"+
                        URLEncoder.encode(uri,"UTF-8")+"-"+(i-1)+".doc";
                //	System.out.println(url);
                int code=sendGet(url);
                if(code==200){
                    System.out.println("success:"+url);
                    success++;
                    System.out.println(URLDecoder.decode(url, "UTF-8"));
                    Main.download(url,path+shiyan+"/"+URLDecoder.decode(uri, "UTF-8")+".doc");
                    reset();
                }else{
                    printNum++;
                    if(printNum>maxPrintNum) {
                        System.out.println(k + ":" + code + "\t" + name + "\t" + id + "\t" + nameIndex + "\t" + success);
                        printNum = 0;
                    }
                }
                if(i==99999)
                    reset();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


}