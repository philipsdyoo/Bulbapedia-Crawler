# Bulbapedia-Crawler

Crawling through Bulbapedia to collect data on each species

Using Jsoup to parse the HTML pages

### Known issues:
* Pokemon with multiple forms will be repeated since only the first stat table is taken
* Pokemon with multiple forms that have different species name will only have the first listed species name
* Pokemon that had stat changes between generations (specifically from generation 5 to 6) will not have their current stat values
* Cannot collect data from newer not fully revealed Pokemon and program will throw an error
* Pokemon with special characters will have ? instead (e.g. male and female Nidoran)