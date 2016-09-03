/**
 * Description:
 * @author: Philip Yoo (philipsdyoo)
 */

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PokemonCrawler {

    public static void main(String[] args) {
	try {
	    Document doc = Jsoup.connect("http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number").get();
	    Elements tables = doc.select("table[align=center]");
	    
	    for(Element table: tables) {
		Elements rows = table.select("tr");
		for (Element pokemon: rows) {
		    if (pokemon.equals(rows.get(0)))
			continue;
		    String name = pokemon.select("td:eq(3) > a").html();
		    String url = "http://bulbapedia.bulbagarden.net" + pokemon.select("td:eq(3) > a").attr("href");
		    System.out.println(name + ": " + url);
		}
	    }
	} catch (IOException e) {
	    System.err.println("Could not connect to provided URL");
	    e.printStackTrace();
	}
    }
}
