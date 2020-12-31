package com.yaoyue.elasticsearch.util;

import com.yaoyue.elasticsearch.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 解析网页工具类
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/30 21:45
 **/
public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
        HtmlParseUtil htmlParseUtil = new HtmlParseUtil();
        List<Product> list = htmlParseUtil.parseHtml("手机");
        for (Product product : list) {
            System.out.println(product.getImg() + product.getTitle() + product.getPrice());
        }
    }

    public static List<Product> parseHtml(String keyword) throws Exception {
        // 获取请求，前提需要联网，不能获取Ajax请求，原因：获取Ajax响应需要发送客户端请求
        String url = "https://search.jd.com/Search?keyword=" + keyword + "&enc=utf-8";

        // Jsoup返回Document对象就是JavaScript的Document对象
        Document document = Jsoup.parse(new URL(url), 3000);
        // 所有在JS中可以用的方法均可以使用
        Element element = document.getElementById("J_goodsList");

        // 分析前端网页可得，li中存放的即是每个商品的信息
        // 获取所有的元素
        Elements elements = element.getElementsByTag("li");

        List<Product> list = new ArrayList<>();

        // 获取元素中的内容
        for(Element el : elements) {
            Product product = new Product();
            // 关于图片比较多的网站，都是采用延迟加载（即懒加载）的模式，以加快网页的响应时间
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            product.setImg(img);
            product.setPrice(price);
            product.setTitle(title);
            list.add(product);
        }
        return list;
    }
}
