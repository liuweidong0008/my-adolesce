package com.adolesce.server.httpRequest;

import com.adolesce.common.entity.User;
import com.adolesce.server.config.RestTemplateConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: RestTemplate发起http请求测试
 * @date 2021/7/3 20:36
 * <p>
 * RestTemplate 是从 Spring3.0 开始支持的一个 HTTP 请求工具，它提供了常见的REST请求方案的模版，
 * 例如 GET 请求、POST 请求、PUT 请求、DELETE 请求以及一些通用的请求执行方法 exchange 以及 execute。
 * <p>
 * RestTemplate 继承自 InterceptingHttpAccessor 并且实现了 RestOperations 接口
 * 其中 RestOperations 接口定义了基本的 RESTful 操作，这些操作在 RestTemplate 中都得到了实现
 */
public class RestTemplateTest {
    @Autowired
    private RestTemplate restTemplate;
    private final static String SERVER_UTL = "http://127.0.0.1:8081/my_adolesce/requestStyle/";

    //RestTemplate GET请求测试

    /**
     * 测试GET请求：getForObject 请求参数放在url上
     */
    @Test
    public void testGet1() {
        String url = SERVER_UTL + "test1" + "?userName=刘威东&age=4";
        String result = RestTemplateConfig.TEMPLATE.getForObject(url, String.class);
        //ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.exchange(url,HttpMethod.GET,null,String.class);
        System.out.println(result);
    }

    /**
     * 测试GET请求：getForObject 请求参数放在占位符上
     */
    @Test
    public void testGet2() {
        String url = SERVER_UTL + "test1" + "?userName={?}&age={?}";
        String result = RestTemplateConfig.TEMPLATE.getForObject(url, String.class, "lwd", 22);
        //ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.exchange(url,HttpMethod.GET,null,String.class,"lwd",22);
        System.out.println(result);
    }

    /**
     * 测试GET请求：getForObject 请求参数放在占位符上,用map进行封装
     * 注意：map中的key要和url上的占位符命名一致
     */
    @Test
    public void testGet3() {
        //String url = SERVER_UTL + "test1" + "?userName={userName}&age={age}";
        String url = SERVER_UTL + "test12" + "/{userName}/{age}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", "张三");
        paramMap.put("age", 25);
        String result = RestTemplateConfig.TEMPLATE.getForObject(url, String.class, paramMap);

        //ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.exchange(url,HttpMethod.GET,null,String.class,paramMap);
        System.out.println(result);
    }

    /**
     * 测试GET请求：getForObject url 用 URI 进行封装
     * 注意：这种方式如果url上存在中文，需要进行转码
     */
    @Test
    public void testGet4() throws UnsupportedEncodingException {
        String url = SERVER_UTL + "test1" + "?userName=" + URLEncoder.encode("李四", "UTF-8") + "&age=23";
        URI uri = URI.create(url);
        String result = RestTemplateConfig.TEMPLATE.getForObject(uri, String.class);
        System.out.println(result);
    }

    /**
     * 测试GET请求：getForEntity
     * <p>
     * getForEntity 与 getForObject API基本一致，都有三个重载方法，参数也一样，区别在于：
     * getForEntity返回ResponseEntity，除了能拿到返回体，还能拿到返回http状态码和头信息
     * 而getForObject只能拿到返回体，如果调用者只关心返回值，就用getForObject即可
     */
    @Test
    public void testGet5() {
        String url = SERVER_UTL + "test1" + "?userName={?}&age={?}";
        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.getForEntity(url, String.class, "lwd", 22);
        System.out.println(responseEntity.getBody());
    }


    //RestTemplate POST请求测试

    /**
     * 测试Post请求：postForObject 请求参数放在url上  (与get请求一致，只是多了第二个参数，第二个参数用于放请求体)
     */
    @Test
    public void testPost1() {
        String url = SERVER_UTL + "test1" + "?userName=刘威东&age=4";
        String result = RestTemplateConfig.TEMPLATE.postForObject(url, null, String.class);
        System.out.println(result);
    }

    /**
     * 测试Post请求：postForObject 请求参数放在占位符上 (与get请求一致，只是多了第二个参数，第二个参数用于放请求体)
     */
    @Test
    public void testPost2() {
        String url = SERVER_UTL + "test1" + "?userName={?}&age={?}";
        String result = RestTemplateConfig.TEMPLATE.postForObject(url, null, String.class, "lwd", 22);
        System.out.println(result);
    }

    /**
     * 测试POST请求：postForObject 请求参数放在占位符上,用map进行封装  (与get请求一致，只是多了第二个参数，第二个参数用于放请求体)
     * 注意：map中的key要和url上的占位符命名一致
     */
    @Test
    public void testPost3() {
        //String url = SERVER_UTL + "test1" + "?userName={userName}&age={age}";
        String url = SERVER_UTL + "test12" + "/{userName}/{age}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", "张三");
        paramMap.put("age", "25");

        //MultiValueMap requestMap = new LinkedMultiValueMap();
        //requestMap.add("age",25);
        String result = RestTemplateConfig.TEMPLATE.postForObject(url, null, String.class, paramMap);
        System.out.println(result);
    }

    /**
     * 测试POST请求：postForObject url 用 URI 进行封装 (与get请求一致，只是多了第二个参数，第二个参数用于放请求体)
     * 注意：这种方式如果url上存在中文，需要进行转码
     */
    @Test
    public void testPost4() throws UnsupportedEncodingException {
        String url = SERVER_UTL + "test1" + "?userName=" + URLEncoder.encode("李四", "UTF-8") + "&age=23";
        URI uri = URI.create(url);
        String result = RestTemplateConfig.TEMPLATE.postForObject(uri, null, String.class);
        System.out.println(result);
    }

    /**
     * 测试POST请求：postForEntity  (与get请求一致，只是多了第二个参数，第二个参数用于放请求体)
     * <p>
     * postForEntity 与 postForObject API基本一致，都有三个重载方法，参数也一样，区别在于：
     * postForEntity返回ResponseEntity，除了能拿到返回体，还能拿到返回http状态码和头信息
     * 而postForObject只能拿到返回体，如果调用者只关心返回值，就用postForObject即可
     */
    @Test
    public void testPost5() {
        String url = SERVER_UTL + "test1" + "?userName={?}&age={?}";
        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.postForEntity(url, null, String.class, "lwd", 22);
        System.out.println(responseEntity.getBody());
    }

//----------------------以下测试POST特有的-----------------------------

    /**
     * 测试POST请求：post请求体方式放置请求数据
     * postForEntity 或 postForObject第二个参数为post方式特有参数，该参数为请求体
     */
    @Test
    public void testPost6() {
        String url = SERVER_UTL + "test12?age={?}";  //test4  test5
        MultiValueMap requestBody = new LinkedMultiValueMap();
        requestBody.add("userName", "张三");

        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.postForEntity(url, requestBody, String.class, 30);
        System.out.println(responseEntity.getBody());
    }

    /**
     * 测试POST请求：post请求体方式放置请求数据
     * 注意: 第二个参数如果是一个 MultiValueMap 的实例，则以 key/value 的形式发送
     * 如果是一个普通对象，则会被转成 json 发送。此时接收方要使用@RequestBody接收，请求到达后json又会被转成对应类型
     */
    @Test
    public void testPost7() {
       /* String url = SERVER_UTL + "test7";
        Map requestBody = new HashMap();
        requestBody.put("userName","张三");
        requestBody.put("age","123");*/

        String url = SERVER_UTL + "test8";
        User user = new User();
        user.setUserName("王五");
        user.setAge(88);

        /*url = SERVER_UTL + "test9";
        List<MyUser> myUsers = new ArrayList<>();
        myUsers.add(myUser);*/

        url = SERVER_UTL + "test10";
        Map<String, User> map = new HashMap<>();
        map.put("mykey", user);

        ResponseEntity<User> responseEntity = RestTemplateConfig.TEMPLATE.postForEntity(url, map, User.class);
        System.out.println(responseEntity.getBody());
    }

    /**
     * 测试POST请求：post请求体方式放置请求数据，文件上传
     */
    @Test
    public void testPost8() throws IOException {
        String url = SERVER_UTL + "upload";  //test4  test5

        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("multipart/form-data");
        headers.setContentType(mediaType);
        headers.set("Authorization", "888000");

        //设置请求体，注意是LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource("D:/pictrues/hourse2.jpg");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("files", fileSystemResource);
        form.add("userName", "张三");

        //用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.postForEntity(url, httpEntity, String.class);
        System.out.println(responseEntity.getBody());
    }

    /**
     * 测试POST请求：post请求体方式放置请求数据，文件上传
     */
    @Test
    public void testPost9() {
        try {
            MultipartFile mulFile = null;
            File file = new File(mulFile.getOriginalFilename());
            FileUtils.copyInputStreamToFile(mulFile.getInputStream(), file);

            String url = SERVER_UTL + "upload";  //test4  test5

            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("multipart/form-data");
            headers.setContentType(mediaType);
            headers.set("Authorization", "888000");

            //设置请求体，注意是LinkedMultiValueMap
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("files", fileSystemResource);
            form.add("userName", "张三");

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

            ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.postForEntity(url, httpEntity, String.class);
            System.out.println(responseEntity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Hotool 链式构建请求
        /* HttpResponse httpResponse = HttpRequest.post(url)
                .header("Authorization","333")  //设置请求头
                .form("headPhoto",file)               //设置请求体（文件上传）
                .timeout(20000)//超时，毫秒
                .execute();*/
    }


    //RestTemplate postForLocation请求测试

    /**
     * postForLocation 方法的返回值是一个 Uri 对象，因为 POST 请求一般用来添加数据，有的时候需要将URL 返回来，此时就可以使用这个方法
     * 如用户登录完需要返回注册URL等场景
     */
    @Test
    public void testPostForLocation1() {
        String url = SERVER_UTL + "register";

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("userName", "lwd");
        form.add("age", 12);

        URI uri = RestTemplateConfig.TEMPLATE.postForLocation(url, form);
        System.out.println(uri);
    }


    //RestTemplate PUT 请求

    /**
     * PUT 请求容易很多，PUT 请求本身方法也比较少，只有三个，与get和post相比，这三个方法，且都只有三个参数，都没有返回值类型
     */
    @Test
    public void testPut1() {
        String url = SERVER_UTL + "test1?userName={?}";

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("age", 12);

        RestTemplateConfig.TEMPLATE.put(url, form, "赵六");
    }

    @Test
    public void testPut2() {
        String url = SERVER_UTL + "test7";

        Map<String, Object> params = new HashMap<>();
        params.put("age", 12);
        params.put("userName", "刘德华");

        RestTemplateConfig.TEMPLATE.put(url, params);
    }

    //RestTemplate DELETE 请求

    /**
     * 和 PUT 请求一样，DELETE 请求也是比较简单的，只有三个方法，且都只有两个参数，没有请求体和返回值类型，请求参数只能放在URL上
     */
    @Test
    public void testDelete1() {
        String url = SERVER_UTL + "test1?userName={?}&age={?}";  //test4  test5
        RestTemplateConfig.TEMPLATE.delete(url, "赵六", 27);
    }

    @Test
    public void testDelete2() {
        String url = SERVER_UTL + "test1?userName={userName}&age={age}";  //test4  test5
        Map<String, Object> param = new HashMap<>();
        param.put("userName", "张学友");
        param.put("age", 36);
        RestTemplateConfig.TEMPLATE.delete(url, param);
    }

    //RestTemplate 通用方法 exchange

    /**
     * 这个方法需要你在调用的时候去指定请求类型，即它既能做 GET 请求，也能做 POST 请求，也能做其它各种类型的请求。
     * 如果开发的时候需要对请求进行封装，使用它再合适不过了
     * 它的参数与postForxxx几乎一致，只是多了一个指定Http方法的参数
     */
    @Test
    public void testExchange1() {
        String url = SERVER_UTL + "upload";  //test4  test5

        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "888000");
        headers.add("myName", "yoyo");

        //设置请求体，注意是LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource("D:/pictrues/hourse2.jpg");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("files", fileSystemResource);
        form.add("userName", "张三");

        //用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.exchange(url, HttpMethod.POST, httpEntity, String.class);


        System.out.println(responseEntity.getBody());
    }

    /**
     * 测试get请求 设置请求头、Cookie
     */
    @Test
    public void testExchange2() {
        String url = SERVER_UTL + "test1" + "?userName=刘威东&age=4";

        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "888000");
        headers.add("myHeader", "yoyo");
        headers.add("Cookie", "mycookie1=123;mycookie2=456");

        //用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = RestTemplateConfig.TEMPLATE.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println(responseEntity.getBody());
    }


    /**
     * 测试get请求 设置请求头、Cookie
     */
    @Test
    public void testBigDecimal() {
        BigDecimal a = new BigDecimal(10.897);
        BigDecimal b = new BigDecimal("10.897");
        BigDecimal c = BigDecimal.valueOf(10.897);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }


}
