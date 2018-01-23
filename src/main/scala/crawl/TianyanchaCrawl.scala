package crawl

import java.net.URLEncoder

import us.codecraft.webmagic.pipeline.ConsolePipeline
import us.codecraft.webmagic.{Page, Site, Spider}
import us.codecraft.webmagic.processor.PageProcessor

class TianyanchaProcessor extends PageProcessor{
  val site = Site.me().setDomain("www.tianyancha.com").setRetryTimes(10).setSleepTime(3000)
    .addCookie("BAEID", "2C893E221875908C93EDB0FEC8EFCC99")
    .addCookie("BAIDUID", "15031658129327F10406562264A9EF27:FG=1")
    .addCookie("BDORZ", "B490B5EBF6F3CD402E515D22BCDA1598")
    .addCookie("BDSFRCVID", "mn0sJeCCxG3watvA8OOraFuXZsI4f1ethRla3J")
    .addCookie("BIDUPSID", "7DAE1EC3BFF044B01C4468166C9E19F1")
    .addCookie("HMACCOUNT", "E1E742DFEB5BBA90")
    .addCookie("HMVT", "e92c8d65d92d534b0fc290df538b4758|1514249916|")
    .addCookie("H_BDCLCKID_SF", "tJCJoKKXtCP3HnRpKn-_MbLqMfQXKK62aKDsh4co-hcqEIL4jb5_35DVLf7I0bJPbKOqVRcbypT6OxbSj4QojUDzjPJ8LxRtWR70QDo60h5nhMJI257JDMP0-RJLWp3y523ion5vQpnOMUtuj6D2j5vQeaRf-b-XMI5XB4K8Kb7VbpQVyJbkbftd2-te-M7RJ2CO-brsBh6dMlru05jE0t4gKM7raTJZfJCO_CP2tIPbbP365ITMMt_H2fQX5-vKHD7yWCvEaDJ5OR5JLn7nDn-_5hjCqUR9-D6lLxnu-hIMeCjN5-7Ch-oyyG62btt_JbuqoM5")
    .addCookie("H_PS_PSSID", "1439_21082_25439_25177_22074")
    .addCookie("Hm_lpvt_e92c8d65d92d534b0fc290df538b4758", "1514249920")
    .addCookie("Hm_lvt_e92c8d65d92d534b0fc290df538b4758", "1513756232,1514186210,1514249582")
    .addCookie("OA", "UC3u+Ua1tN/OJ/9z609ClXeR6WTtLILzLYyxP8M7H2k=")
    .addCookie("PSTM", "1513672374")
    .addCookie("RTYCID", "549a634b771945ddb4073a9dae11cc53")
    .addCookie("TYCID", "70115190e55a11e78f5ebf530c0318a5")
    .addCookie("_csrf", "E9WhXGvlIGVXigHSDD2ZIw==")
    .addCookie("_csrf_bk", "c93d7105da41d9e76895701aa7748a46")
    .addCookie("_utm", "a08c0761ced746b6842a4567ce6f6453")
    .addCookie("aliyungf_tc", "AQAAAH96nj9f2gAAzkbCt17f0DXtqf/t")
    .addCookie("aliyungf_tc", "AQAAAKA0kxrSowsAzkbCtz2KloVQeEtj")
    .addCookie("auth_token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNzMyMTE3MTk1MyIsImlhdCI6MTUxNDI0OTc3OSwiZXhwIjoxNTI5ODAxNzc5fQ.FZgfnbRxEfLWkUeJxuczIWCalphIRLvGCIvN27AeVBpcbPqeEzZfIiWPShOGktazW8tLLUbmez7xb0gFSRcb1w")
    .addCookie("bannerFlag", "true")
    .addCookie("csrfToken", "mfdc0kXZZEsN2Ig4M-whg0-H")
    .addCookie("csrfToken", "EQRHSEN8aoFDroU2ceKmkewT")
    .addCookie("jsid", "SEM-BAIDU-PZPC-000000")
    .addCookie("p_o2_uin", "2881262044")
    .addCookie("pac_uid", "0_366878ffe4d24")
    .addCookie("pgv_pvid", "8994823440")
    .addCookie("ssuid", "8125855420")
    .addCookie("token", "2ba8e3c1fc784092a498f8969da36aad")
    .addCookie("tyc-user-info", "%257B%2522new%2522%253A%25221%2522%252C%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNzMyMTE3MTk1MyIsImlhdCI6MTUxNDI0OTc3OSwiZXhwIjoxNTI5ODAxNzc5fQ.FZgfnbRxEfLWkUeJxuczIWCalphIRLvGCIvN27AeVBpcbPqeEzZfIiWPShOGktazW8tLLUbmez7xb0gFSRcb1w%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vipManager%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252217321171953%2522%257D")
    .addCookie("undefined", "70115190e55a11e78f5ebf530c0318a5")

    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .addHeader("Accept-Encoding", "gzip, deflate")
    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
    .addHeader("Connection", "keep-alive")

  override def getSite: Site = {
    this.site
  }

  override def process(page: Page): Unit = {
    page.putField("COMPANY_NAME",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/a/em/em/text()").get())
    page.putField("MOBILE",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[2]/text()/text()").regex("电话：(.*)").get())
    page.putField("EMAIL",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[2]/span/text()").regex("邮箱：(.*)").get())
    page.putField("REGISTER_CAPITAL",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[1]/span[1]/text()").regex("注册资本：(.*)").get())
    page.putField("FOUND_DATE",page.getHtml.xpath("//*[@id=\"searchlist\"]/table/tbody/tr[1]/td[2]/p[1]/span[2]/text()").regex("成立时间：(.*)").get())
  }


}

object TianyanchaCrawl {
  def main(args: Array[String]): Unit = {

    val key="京东"
    val urlKey=URLEncoder.encode(key,"UTF-8")
    Spider.create(new TianyanchaProcessor)
      .addUrl(s"http://www.tianyancha.com/search?key=${urlKey}")
      .addPipeline(new ConsolePipeline)
      .thread(10)
      .run()
  }
}
