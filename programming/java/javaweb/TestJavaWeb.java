
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {
	public static String work(String input1) {
		return input1 + " " + input1;
	}
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/", new IndexHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        		String data = t.getRequestURI().getQuery();
        		Map<String, String> paras = new HashMap<>();
        		for(String para : data.split("&")) {
        			String[] words = para.split("=");
        			paras.put(words[0].trim(), words[1].trim());
        		}
        		String input1 = paras.get("input1");
            String response = work(input1);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        		String root = "html/";
            URI uri = t.getRequestURI();
            File file = new File(root + uri.getPath()).getCanonicalFile();
            if (!file.isFile()) {
              // Object does not exist or is not a file: reject with 404 error.
              String response = "404 (Not Found)\n";
              t.sendResponseHeaders(404, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            } else {
              // Object exists and is a file: accept with response code 200.
              t.sendResponseHeaders(200, 0);
              OutputStream os = t.getResponseBody();
              FileInputStream fs = new FileInputStream(file);
              final byte[] buffer = new byte[0x10000];
              int count = 0;
              while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer,0,count);
              }
              fs.close();
              os.close();
            }
        }
    }

}
