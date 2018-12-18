import java.io._
import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity

object HttpPostTester {

  def main(args: Array[String]) {

    val url = "http://localhost:8080/posttest";

    val post = new HttpPost(url)
    post.addHeader("appid","YahooDemo")
    post.addHeader("query","umbrella")
    post.addHeader("results","10")

    val client = new DefaultHttpClient
    val params = client.getParams
    params.setParameter("foo", "bar")
    
    val nameValuePairs = new ArrayList[NameValuePair](1)
    nameValuePairs.add(new BasicNameValuePair("registrationid", "123456789"));
    nameValuePairs.add(new BasicNameValuePair("accountType", "GOOGLE"));
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    
    // send the post request
    val response = client.execute(post)
    println("--- HEADERS ---")
    response.getAllHeaders.foreach(arg => println(arg))
	
  }

}
