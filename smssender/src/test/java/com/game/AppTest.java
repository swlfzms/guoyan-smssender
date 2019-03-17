package com.game;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    String url = "http://ec2-18-191-123-41.us-east-2.compute.amazonaws.com:17289/reservation/insertCode";
    /**
     * Rigorous Test :-)
     */
    //@Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void insertCode() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/Users/jason/Desktop/1552060005812.txt"));
        String line = new String();
        JSONArray data = new JSONArray();
        while((line = br.readLine())!= null){
            data.add(line);
        }
        br.close();
        JSONObject param = new JSONObject();
        param.put("data", data);
        param.put("code", "c8pnadsghyov");
        String result = HttpClientUtils.post(url, param.toString());
        System.out.println(result);
    }
    //@Test
    public void findAll() throws IOException {
        String findAll = "http://localhost:17289/reservation/findValidCode";
        String result = HttpClientUtils.post(findAll, new JSONObject().toString());
        System.out.println(result);
    }
}
