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
	String startURL = "http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number";
	try {
	    //Connect to the Pokemon List page and set the timeout to 60 seconds
	    Document doc = Jsoup.connect(startURL).timeout(60*1000).get();
	    //Generations are separated by tables
	    Elements tables = doc.select("table[align=center]");
	    
	    //Go through each table/generation
	    for(Element table: tables) {
		Elements rows = table.select("tr");
		//Go through each row representing a specific species
		for (Element pokemon: rows) {
		    //Skip the first row which contains the header
		    if (pokemon.equals(rows.get(0)))
			continue;
		    String name = pokemon.select("td:eq(3) > a").html();
		    String url = "http://bulbapedia.bulbagarden.net" + pokemon.select("td:eq(3) > a").attr("href");
		    //Get the types
		    String type1 = pokemon.select("td[colspan] > a > span").first().html();
		    String type2 = pokemon.select("td[colspan] > a > span").last().html();
		    //If it has only one type, set the second type to null
		    if (type2.equals(type1))
			type2 = null;
		    //System.out.println(name + ": " + url);
		    
		    //Go to Pokemon's page and set timeout to 60 seconds
		    Document pokemonDoc = Jsoup.connect(url).timeout(60*1000).get();
		    
		    //Species name
		    String species = pokemonDoc.select("a[title=Pokémon category] > span").html().replace(" Pokémon", "");
		    //If the species name has an "explain" abbr, take the child span instead
		    if (species.charAt(0) == '<')
			species = pokemonDoc.select("a[title=Pokémon category] > span > span").html().replace(" Pokémon", "");
		    System.out.println(name + ", " + species + ", " + type1 + "/" + type2);
		}
	    }
	} catch (IOException e) {
	    System.err.println("Could not connect to provided URL: " + startURL);
	    //e.printStackTrace();
	}
    }
}
